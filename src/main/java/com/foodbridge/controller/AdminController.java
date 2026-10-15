package com.foodbridge.controller;

import com.foodbridge.dto.DashboardStatsDto;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.ApprovalStatus;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.OrgVerificationStatus;
import com.foodbridge.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AnalyticsService analyticsService;
    private final FoodDonationService foodDonationService;
    private final MaterialDonationService materialDonationService;
    private final CashCampaignService cashCampaignService;
    private final FeedbackService feedbackService;
    private final AuditService auditService;

    public AdminController(UserService userService,
                           AnalyticsService analyticsService,
                           FoodDonationService foodDonationService,
                           MaterialDonationService materialDonationService,
                           CashCampaignService cashCampaignService,
                           FeedbackService feedbackService,
                           AuditService auditService) {
        this.userService = userService;
        this.analyticsService = analyticsService;
        this.foodDonationService = foodDonationService;
        this.materialDonationService = materialDonationService;
        this.cashCampaignService = cashCampaignService;
        this.feedbackService = feedbackService;
        this.auditService = auditService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        User admin = userService.getCurrentUser();
        DashboardStatsDto stats = analyticsService.getPlatformStats();

        model.addAttribute("currentUser", admin);
        model.addAttribute("stats", stats);
        model.addAttribute("pendingOrgs", userService.getOrganizationsByStatus(OrgVerificationStatus.PENDING));
        model.addAttribute("pendingCampaigns", cashCampaignService.getCampaignsByApprovalStatus(ApprovalStatus.PENDING));
        model.addAttribute("reportedFeedbacks", feedbackService.getReportedFeedbacks());
        model.addAttribute("recentLogs", auditService.getRecentLogs());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/toggle-active/{id}")
    public String toggleUserActive(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User admin = userService.getCurrentUser();
        userService.toggleUserActive(id, admin.getEmail());
        redirectAttributes.addFlashAttribute("successMessage", "User account status updated.");
        return "redirect:/admin/users";
    }

    @GetMapping("/organizations")
    public String manageOrganizations(Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("pendingOrgs", userService.getOrganizationsByStatus(OrgVerificationStatus.PENDING));
        model.addAttribute("verifiedOrgs", userService.getOrganizationsByStatus(OrgVerificationStatus.VERIFIED));
        model.addAttribute("rejectedOrgs", userService.getOrganizationsByStatus(OrgVerificationStatus.REJECTED));
        return "admin/organizations";
    }

    @PostMapping("/organizations/verify")
    public String verifyOrganization(@RequestParam("orgUserId") Long orgUserId,
                                     @RequestParam("status") OrgVerificationStatus status,
                                     @RequestParam(value = "notes", required = false) String notes,
                                     RedirectAttributes redirectAttributes) {
        User admin = userService.getCurrentUser();
        try {
            userService.verifyOrganization(orgUserId, status, notes, admin.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Organization verification status updated to " + status.getLabel() + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/organizations";
    }

    @GetMapping("/campaigns")
    public String manageCampaigns(Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("pendingCampaigns", cashCampaignService.getCampaignsByApprovalStatus(ApprovalStatus.PENDING));
        model.addAttribute("approvedCampaigns", cashCampaignService.getCampaignsByApprovalStatus(ApprovalStatus.APPROVED));
        model.addAttribute("rejectedCampaigns", cashCampaignService.getCampaignsByApprovalStatus(ApprovalStatus.REJECTED));
        return "admin/campaigns";
    }

    @PostMapping("/campaigns/moderate")
    public String moderateCampaign(@RequestParam("campaignId") Long campaignId,
                                   @RequestParam("approve") boolean approve,
                                   @RequestParam(value = "notes", required = false) String notes,
                                   RedirectAttributes redirectAttributes) {
        User admin = userService.getCurrentUser();
        try {
            cashCampaignService.approveCampaign(campaignId, approve, notes, admin.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Campaign " + (approve ? "approved and made live" : "rejected") + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/campaigns";
    }

    @GetMapping("/feedbacks")
    public String manageFeedbacks(Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("reportedFeedbacks", feedbackService.getReportedFeedbacks());
        return "admin/feedbacks";
    }

    @PostMapping("/feedbacks/moderate")
    public String moderateFeedback(@RequestParam("feedbackId") Long feedbackId,
                                   @RequestParam("delete") boolean delete,
                                   RedirectAttributes redirectAttributes) {
        User admin = userService.getCurrentUser();
        try {
            feedbackService.moderateFeedback(feedbackId, delete, admin.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    delete ? "Feedback deleted." : "Feedback approved and cleared.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/feedbacks";
    }

    @GetMapping("/audit-logs")
    public String auditLogs(Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("logs", auditService.getRecentLogs());
        return "admin/audit-logs";
    }
}
