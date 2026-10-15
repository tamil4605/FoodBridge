package com.foodbridge.controller;

import com.foodbridge.dto.DonationRequestDto;
import com.foodbridge.entity.DonationRequest;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.DonationRequestService;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/requests")
public class DonationRequestController {

    private final DonationRequestService donationRequestService;
    private final UserService userService;

    public DonationRequestController(DonationRequestService donationRequestService, UserService userService) {
        this.donationRequestService = donationRequestService;
        this.userService = userService;
    }

    @PostMapping("/submit")
    public String submitRequest(@Valid @ModelAttribute("requestDto") DonationRequestDto requestDto,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getRole() != Role.ROLE_RECIPIENT_ORG) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Only verified recipient organizations and NGOs can submit donation requests.");
            return "redirect:/food/details/" + requestDto.getDonationId();
        }

        try {
            DonationRequest req = donationRequestService.submitRequest(currentUser, requestDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your request has been sent to " + req.getDonor().getEffectiveName() + ". You will be notified when they accept.");
            return "redirect:/dashboard/organization";
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/dashboard/organization";
        }
    }

    @PostMapping("/accept/{id}")
    public String acceptRequest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        try {
            donationRequestService.acceptRequest(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Request accepted successfully! You or the recipient can now schedule the pickup.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard/donor";
    }

    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable("id") Long id,
                                @RequestParam(value = "reason", required = false) String reason,
                                RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        try {
            donationRequestService.rejectRequest(id, reason, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Request declined.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard/donor";
    }
}
