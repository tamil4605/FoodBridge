package com.foodbridge.repository;

import com.foodbridge.entity.CashCampaign;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.ApprovalStatus;
import com.foodbridge.entity.enums.CampaignStatus;
import com.foodbridge.entity.enums.CauseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CashCampaignRepository extends JpaRepository<CashCampaign, Long> {

    List<CashCampaign> findByOrganizationOrderByCreatedAtDesc(User organization);

    List<CashCampaign> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus approvalStatus);

    List<CashCampaign> findByApprovalStatusAndStatusOrderByCreatedAtDesc(ApprovalStatus approvalStatus, CampaignStatus status);

    List<CashCampaign> findTop4ByApprovalStatusAndStatusOrderByCreatedAtDesc(ApprovalStatus approvalStatus, CampaignStatus status);

    long countByApprovalStatus(ApprovalStatus approvalStatus);

    @Query("SELECT SUM(c.raisedAmount) FROM CashCampaign c WHERE c.approvalStatus = 'APPROVED'")
    BigDecimal getTotalFundsRaised();

    @Query("SELECT c FROM CashCampaign c WHERE c.approvalStatus = :approvalStatus AND c.status = :status " +
           "AND (:category IS NULL OR c.causeCategory = :category) " +
           "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.createdAt DESC")
    List<CashCampaign> searchCampaigns(
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("status") CampaignStatus status,
            @Param("category") CauseCategory category,
            @Param("keyword") String keyword
    );
}
