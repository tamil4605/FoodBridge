package com.foodbridge.controller;

import com.foodbridge.dto.PickupScheduleDto;
import com.foodbridge.entity.DonationRequest;
import com.foodbridge.entity.Pickup;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.DonationRequestService;
import com.foodbridge.service.PickupService;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pickups")
public class PickupController {

    private final PickupService pickupService;
    private final DonationRequestService donationRequestService;
    private final UserService userService;

    public PickupController(PickupService pickupService,
                            DonationRequestService donationRequestService,
                            UserService userService) {
        this.pickupService = pickupService;
        this.donationRequestService = donationRequestService;
        this.userService = userService;
    }

    @GetMapping
    public String listPickups(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getRole() == Role.ROLE_RECIPIENT_ORG) {
            model.addAttribute("pickups", pickupService.getPickupsForRecipientOrg(currentUser));
        } else {
            model.addAttribute("pickups", pickupService.getPickupsForDonor(currentUser));
        }
        model.addAttribute("currentUser", currentUser);
        return "pickups/list";
    }

    @GetMapping("/details/{id}")
    public String pickupDetails(@PathVariable("id") Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        Pickup pickup = pickupService.getPickupById(id);
        model.addAttribute("pickup", pickup);
        model.addAttribute("currentUser", currentUser);
        return "pickups/details";
    }

    @GetMapping("/schedule")
    public String schedulePage(@RequestParam("requestId") Long requestId, Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        DonationRequest request = donationRequestService.getRequestById(requestId);

        PickupScheduleDto dto = new PickupScheduleDto();
        dto.setRequestId(requestId);
        if (request.getFoodDonation() != null) {
            dto.setPickupAddress(request.getFoodDonation().getPickupAddress());
            dto.setCity(request.getFoodDonation().getCity());
            dto.setPincode(request.getFoodDonation().getPincode());
        } else if (request.getMaterialDonation() != null) {
            dto.setPickupAddress(request.getMaterialDonation().getPickupAddress());
            dto.setCity(request.getMaterialDonation().getCity());
            dto.setPincode(request.getMaterialDonation().getPincode());
        }

        model.addAttribute("pickupDto", dto);
        model.addAttribute("request", request);
        model.addAttribute("currentUser", currentUser);
        return "pickups/schedule";
    }

    @PostMapping("/schedule")
    public String schedulePickup(@Valid @ModelAttribute("pickupDto") PickupScheduleDto pickupDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            DonationRequest request = donationRequestService.getRequestById(pickupDto.getRequestId());
            model.addAttribute("request", request);
            model.addAttribute("currentUser", currentUser);
            return "pickups/schedule";
        }

        try {
            Pickup pickup = pickupService.schedulePickup(pickupDto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Pickup scheduled successfully for " + pickup.getScheduledDateTime());
            return "redirect:/pickups/details/" + pickup.getId();
        } catch (BadRequestException ex) {
            DonationRequest request = donationRequestService.getRequestById(pickupDto.getRequestId());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("request", request);
            model.addAttribute("currentUser", currentUser);
            return "pickups/schedule";
        }
    }

    @PostMapping("/confirm-collection/{id}")
    public String confirmCollection(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        try {
            pickupService.confirmCollection(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Collection confirmed successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pickups/details/" + id;
    }

    @PostMapping("/confirm-handover/{id}")
    public String confirmHandover(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        try {
            pickupService.confirmHandover(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Handover verified and marked as COMPLETED! Thank you for completing this donation.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pickups/details/" + id;
    }

    @PostMapping("/cancel/{id}")
    public String cancelPickup(@PathVariable("id") Long id,
                               @RequestParam("reason") String reason,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        try {
            pickupService.cancelPickup(id, reason, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Pickup cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/pickups";
    }
}
