package com.foodbridge.controller;

import com.foodbridge.dto.DashboardStatsDto;
import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.CommunityNeed;
import com.foodbridge.entity.CashCampaign;
import com.foodbridge.entity.User;
import com.foodbridge.service.AnalyticsService;
import com.foodbridge.service.CommunityNeedService;
import com.foodbridge.service.CashCampaignService;
import com.foodbridge.service.FoodDonationService;
import com.foodbridge.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    private final FoodDonationService foodDonationService;
    private final CommunityNeedService communityNeedService;
    private final CashCampaignService cashCampaignService;
    private final AnalyticsService analyticsService;
    private final UserService userService;

    public HomeController(FoodDonationService foodDonationService,
                          CommunityNeedService communityNeedService,
                          CashCampaignService cashCampaignService,
                          AnalyticsService analyticsService,
                          UserService userService) {
        this.foodDonationService = foodDonationService;
        this.communityNeedService = communityNeedService;
        this.cashCampaignService = cashCampaignService;
        this.analyticsService = analyticsService;
        this.userService = userService;
    }

    @GetMapping({"/", "/home"})
    public String index(Model model) {
        User currentUser = userService.getCurrentUser();
        DashboardStatsDto stats = analyticsService.getPlatformStats();
        List<FoodDonation> featuredFood = foodDonationService.getFeaturedAvailableFood();
        List<CommunityNeed> featuredNeeds = communityNeedService.getFeaturedNeeds();
        List<CashCampaign> featuredCampaigns = cashCampaignService.getFeaturedCampaigns();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("stats", stats);
        model.addAttribute("featuredFood", featuredFood);
        model.addAttribute("featuredNeeds", featuredNeeds);
        model.addAttribute("featuredCampaigns", featuredCampaigns);

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        return "about";
    }
}
