package com.foodbridge.repository;

import com.foodbridge.entity.CashCampaign;
import com.foodbridge.entity.CashTransaction;
import com.foodbridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CashTransactionRepository extends JpaRepository<CashTransaction, Long> {

    List<CashTransaction> findByCampaignOrderByCreatedAtDesc(CashCampaign campaign);

    List<CashTransaction> findByDonorOrderByCreatedAtDesc(User donor);

    List<CashTransaction> findByDonorEmailOrderByCreatedAtDesc(String email);

    long countByCampaign(CashCampaign campaign);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM CashTransaction t WHERE t.campaign.id = :campaignId AND t.confirmed = true")
    BigDecimal sumConfirmedByCampaignId(@Param("campaignId") Long campaignId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM CashTransaction t WHERE t.donor = :donor AND t.confirmed = true")
    BigDecimal sumConfirmedByDonor(@Param("donor") User donor);
}
