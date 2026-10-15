package com.foodbridge.controller;

import com.foodbridge.dto.FeedbackDto;
import com.foodbridge.entity.Pickup;
import com.foodbridge.entity.User;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.FeedbackService;
import com.foodbridge.service.PickupService;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final PickupService pickupService;
    private final UserService userService;

    public FeedbackController(FeedbackService feedbackService,
                              PickupService pickupService,
                              UserService userService) {
        this.feedbackService = feedbackService;
        this.pickupService = pickupService;
        this.userService = userService;
    }

    @GetMapping("/create")
    public String feedbackForm(@RequestParam("pickupId") Long pickupId, Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        Pickup pickup = pickupService.getPickupById(pickupId);
        FeedbackDto dto = new FeedbackDto();
        dto.setPickupId(pickupId);
        dto.setRating(5);

        model.addAttribute("pickup", pickup);
        model.addAttribute("feedbackDto", dto);
        model.addAttribute("currentUser", currentUser);
        return "feedback/create";
    }

    @PostMapping("/create")
    public String submitFeedback(@Valid @ModelAttribute("feedbackDto") FeedbackDto feedbackDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            Pickup pickup = pickupService.getPickupById(feedbackDto.getPickupId());
            model.addAttribute("pickup", pickup);
            model.addAttribute("currentUser", currentUser);
            return "feedback/create";
        }

        try {
            feedbackService.submitFeedback(feedbackDto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thank you! Your feedback and rating have been recorded.");
            return "redirect:/dashboard";
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/report/{id}")
    public String reportFeedback(@PathVariable("id") Long id,
                                 @RequestParam("reason") String reason,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        feedbackService.reportFeedback(id, reason, currentUser);
        redirectAttributes.addFlashAttribute("successMessage",
                "Feedback has been flagged for administrator moderation review.");
        return "redirect:/dashboard";
    }
}
