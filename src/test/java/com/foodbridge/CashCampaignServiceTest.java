package com.foodbridge;

import com.foodbridge.dto.CashCampaignDto;
import com.foodbridge.dto.CashContributionDto;
import com.foodbridge.entity.CashCampaign;
import com.foodbridge.entity.CashTransaction;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.AccountType;
import com.foodbridge.entity.enums.ApprovalStatus;
import com.foodbridge.entity.enums.CauseCategory;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.repository.CashCampaignRepository;
import com.foodbridge.repository.CashTransactionRepository;
import com.foodbridge.repository.UserRepository;
import com.foodbridge.service.CashCampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CashCampaignServiceTest {

    @Autowired
    private CashCampaignService cashCampaignService;

    @Autowired
    private CashCampaignRepository cashCampaignRepository;

    @Autowired
    private CashTransactionRepository cashTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User organization;
    private User admin;

    @BeforeEach
    void setUp() {
        cashTransactionRepository.deleteAll();
        cashCampaignRepository.deleteAll();
        userRepository.deleteAll();

        organization = userRepository.findByEmail("ngo@test.com").orElseGet(() -> {
            User u = new User("Child Care NGO", "ngo@test.com", "hash", "+91 98401 22334",
                    AccountType.RECIPIENT_ORG, Role.ROLE_RECIPIENT_ORG, "Anna Nagar", "Chennai", "Tamil Nadu", "600040");
            return userRepository.save(u);
        });

        admin = userRepository.findByEmail("admin@foodbridge.org").orElseGet(() -> {
            User a = new User("Platform Admin", "admin@foodbridge.org", "hash", "+91 98401 00000",
                    AccountType.ADMIN, Role.ROLE_ADMIN, "Secretariat", "Chennai", "Tamil Nadu", "600009");
            return userRepository.save(a);
        });
    }

    @Test
    void testCampaignCreationApprovalAndDemoContribution() {
        CashCampaignDto dto = new CashCampaignDto();
        dto.setTitle("Midday Milk and Nutrition Support");
        dto.setCauseCategory(CauseCategory.CHILD_WELFARE);
        dto.setDescription("Supplying milk and nutrition biscuits for school children.");
        dto.setTargetAmount(BigDecimal.valueOf(25000.00));
        dto.setDeadline(LocalDate.now().plusDays(30));

        CashCampaign created = cashCampaignService.createCampaign(organization, dto);
        assertEquals(ApprovalStatus.PENDING, created.getApprovalStatus());

        // Admin approves
        cashCampaignService.approveCampaign(created.getId(), true, "Approved for public donations", admin.getEmail());
        CashCampaign approved = cashCampaignService.getCampaignById(created.getId());
        assertEquals(ApprovalStatus.APPROVED, approved.getApprovalStatus());

        // Process simulated demo contribution of ₹1000
        CashContributionDto contrib = new CashContributionDto();
        contrib.setCampaignId(approved.getId());
        contrib.setAmount(BigDecimal.valueOf(1000.00));
        contrib.setDonorName("Generous Donor");
        contrib.setDonorEmail("donor@example.com");
        contrib.setDonorMessage("Wishing the children good health!");
        contrib.setDemoConsent(true);

        CashTransaction tx = cashCampaignService.processDemoContribution(contrib, null);

        assertNotNull(tx.getId());
        assertTrue(tx.getTransactionReference().startsWith("FB-DEMO-"));
        assertEquals(BigDecimal.valueOf(1000.00), tx.getAmount());

        CashCampaign updatedCampaign = cashCampaignService.getCampaignById(approved.getId());
        assertEquals(BigDecimal.valueOf(1000.00), updatedCampaign.getRaisedAmount());
        assertEquals(4, updatedCampaign.getProgressPercentage()); // 1000 / 25000 = 4%
    }
}
