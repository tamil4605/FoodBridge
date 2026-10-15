package com.foodbridge.controller;

import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;
    private final FoodDonationService foodDonationService;
    private final MaterialDonationService materialDonationService;
    private final CashCampaignService cashCampaignService;
    private final CommunityNeedService communityNeedService;
    private final DonationRequestService donationRequestService;
    private final PickupService pickupService;
    private final NotificationService notificationService;
    private final FeedbackService feedbackService;

    public DashboardController(UserService userService,
                               FoodDonationService foodDonationService,
                               MaterialDonationService materialDonationService,
                               CashCampaignService cashCampaignService,
                               CommunityNeedService communityNeedService,
                               DonationRequestService donationRequestService,
                               PickupService pickupService,
                               NotificationService notificationService,
                               FeedbackService feedbackService) {
        this.userService = userService;
        this.foodDonationService = foodDonationService;
        this.materialDonationService = materialDonationService;
        this.cashCampaignService = cashCampaignService;
        this.communityNeedService = communityNeedService;
        this.donationRequestService = donationRequestService;
        this.pickupService = pickupService;
        this.notificationService = notificationService;
        this.feedbackService = feedbackService;
    }

    @GetMapping("/dashboard")
    public String dashboardRouter() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (currentUser.getRole() == Role.ROLE_RECIPIENT_ORG) {
            return "redirect:/dashboard/organization";
        } else {
            return "redirect:/dashboard/donor";
        }
    }

    @GetMapping("/dashboard/donor")
    public String donorDashboard(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("foodDonations", foodDonationService.getListingsByDonor(currentUser));
        model.addAttribute("materialDonations", materialDonationService.getListingsByDonor(currentUser));
        model.addAttribute("cashContributions", cashCampaignService.getTransactionsByDonor(currentUser));
        model.addAttribute("incomingRequests", donationRequestService.getRequestsForDonor(currentUser));
        model.addAttribute("pickups", pickupService.getPickupsForDonor(currentUser));
        model.addAttribute("averageRating", feedbackService.getAverageRating(currentUser));
        model.addAttribute("feedbacks", feedbackService.getFeedbacksForUser(currentUser));

        return "dashboard/donor-dashboard";
    }

    @GetMapping("/dashboard/organization")
    public String organizationDashboard(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myRequests", donationRequestService.getRequestsByOrganization(currentUser));
        model.addAttribute("myNeeds", communityNeedService.getNeedsByOrganization(currentUser));
        model.addAttribute("myCampaigns", cashCampaignService.getCampaignsByOrganization(currentUser));
        model.addAttribute("pickups", pickupService.getPickupsForRecipientOrg(currentUser));
        model.addAttribute("averageRating", feedbackService.getAverageRating(currentUser));
        model.addAttribute("feedbacks", feedbackService.getFeedbacksForUser(currentUser));

        return "dashboard/organization-dashboard";
    }
}
