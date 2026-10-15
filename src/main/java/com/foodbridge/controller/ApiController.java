package com.foodbridge.controller;

import com.foodbridge.dto.DashboardStatsDto;
import com.foodbridge.entity.*;
import com.foodbridge.entity.enums.ApprovalStatus;
import com.foodbridge.entity.enums.CampaignStatus;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.NeedStatus;
import com.foodbridge.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;
    private final FoodDonationService foodDonationService;
    private final MaterialDonationService materialDonationService;
    private final CashCampaignService cashCampaignService;
    private final CommunityNeedService communityNeedService;
    private final AnalyticsService analyticsService;

    public ApiController(UserService userService,
                         FoodDonationService foodDonationService,
                         MaterialDonationService materialDonationService,
                         CashCampaignService cashCampaignService,
                         CommunityNeedService communityNeedService,
                         AnalyticsService analyticsService) {
        this.userService = userService;
        this.foodDonationService = foodDonationService;
        this.materialDonationService = materialDonationService;
        this.cashCampaignService = cashCampaignService;
        this.communityNeedService = communityNeedService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/auth/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        User user = userService.getCurrentUser();
        Map<String, Object> res = new HashMap<>();
        if (user == null) {
            res.put("authenticated", false);
            return ResponseEntity.ok(res);
        }
        res.put("authenticated", true);
        res.put("id", user.getId());
        res.put("name", user.getName());
        res.put("email", user.getEmail());
        res.put("role", user.getRole());
        res.put("accountType", user.getAccountType());
        res.put("city", user.getCity());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/food")
    public ResponseEntity<List<FoodDonation>> getAvailableFood(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "pincode", required = false) String pincode,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(foodDonationService.getAvailableFoodListings(null, null, city, pincode, keyword));
    }

    @GetMapping("/food/{id}")
    public ResponseEntity<FoodDonation> getFoodDetails(@PathVariable("id") Long id) {
        return ResponseEntity.ok(foodDonationService.getFoodDonationById(id));
    }

    @GetMapping("/materials")
    public ResponseEntity<List<MaterialDonation>> getMaterials(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "pincode", required = false) String pincode,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(materialDonationService.getAvailableMaterials(null, null, city, pincode, keyword));
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<MaterialDonation> getMaterialDetails(@PathVariable("id") Long id) {
        return ResponseEntity.ok(materialDonationService.getMaterialDonationById(id));
    }

    @GetMapping("/campaigns")
    public ResponseEntity<List<CashCampaign>> getCampaigns(
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(cashCampaignService.getApprovedActiveCampaigns(null, keyword));
    }

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<CashCampaign> getCampaignDetails(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cashCampaignService.getCampaignById(id));
    }

    @GetMapping("/needs")
    public ResponseEntity<List<CommunityNeed>> getNeeds(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(communityNeedService.getOpenNeeds(null, null, city, null, keyword));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(analyticsService.getPlatformStats());
    }
}
