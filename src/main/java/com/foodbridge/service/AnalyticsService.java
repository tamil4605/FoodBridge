package com.foodbridge.service;

import com.foodbridge.dto.DashboardStatsDto;
import com.foodbridge.entity.enums.ApprovalStatus;
import com.foodbridge.entity.enums.CampaignStatus;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.OrgVerificationStatus;
import com.foodbridge.entity.enums.PickupStatus;
import com.foodbridge.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final FoodDonationRepository foodDonationRepository;
    private final MaterialDonationRepository materialDonationRepository;
    private final CashCampaignRepository cashCampaignRepository;
    private final DonationRequestRepository donationRequestRepository;
    private final PickupRepository pickupRepository;
    private final UserRepository userRepository;

    public AnalyticsService(FoodDonationRepository foodDonationRepository,
                            MaterialDonationRepository materialDonationRepository,
                            CashCampaignRepository cashCampaignRepository,
                            DonationRequestRepository donationRequestRepository,
                            PickupRepository pickupRepository,
                            UserRepository userRepository) {
        this.foodDonationRepository = foodDonationRepository;
        this.materialDonationRepository = materialDonationRepository;
        this.cashCampaignRepository = cashCampaignRepository;
        this.donationRequestRepository = donationRequestRepository;
        this.pickupRepository = pickupRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatsDto getPlatformStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalFoodListings(foodDonationRepository.count());
        stats.setActiveFoodAvailable(foodDonationRepository.countByStatus(DonationStatus.AVAILABLE));

        stats.setTotalMaterialListings(materialDonationRepository.count());
        stats.setActiveMaterialsAvailable(materialDonationRepository.countByStatus(DonationStatus.AVAILABLE));

        stats.setTotalRequests(donationRequestRepository.count());
        stats.setCompletedPickups(pickupRepository.countByStatus(PickupStatus.HANDED_OVER));

        stats.setActiveCampaigns(cashCampaignRepository.countByApprovalStatus(ApprovalStatus.APPROVED));
        BigDecimal raised = cashCampaignRepository.getTotalFundsRaised();
        stats.setTotalFundsRaised(raised != null ? raised : BigDecimal.ZERO);

        stats.setTotalUsers(userRepository.count());
        stats.setPendingOrgVerifications(userRepository.countByVerificationStatus(OrgVerificationStatus.PENDING));
        stats.setPendingCampaignApprovals(cashCampaignRepository.countByApprovalStatus(ApprovalStatus.PENDING));

        return stats;
    }
}
