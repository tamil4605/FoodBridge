package com.foodbridge.service;

import com.foodbridge.dto.CashCampaignDto;
import com.foodbridge.dto.CashContributionDto;
import com.foodbridge.entity.CashCampaign;
import com.foodbridge.entity.CashTransaction;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.ApprovalStatus;
import com.foodbridge.entity.enums.CampaignStatus;
import com.foodbridge.entity.enums.CauseCategory;
import com.foodbridge.entity.enums.NotificationType;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.CashCampaignRepository;
import com.foodbridge.repository.CashTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CashCampaignService {

    private final CashCampaignRepository cashCampaignRepository;
    private final CashTransactionRepository cashTransactionRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public CashCampaignService(CashCampaignRepository cashCampaignRepository,
                               CashTransactionRepository cashTransactionRepository,
                               NotificationService notificationService,
                               AuditService auditService) {
        this.cashCampaignRepository = cashCampaignRepository;
        this.cashTransactionRepository = cashTransactionRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    public CashCampaign createCampaign(User organization, CashCampaignDto dto) {
        if (dto.getDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Campaign deadline must be in the future.");
        }
        if (dto.getTargetAmount() == null || dto.getTargetAmount().compareTo(BigDecimal.valueOf(100)) < 0) {
            throw new BadRequestException("Funding target must be at least ₹100.");
        }

        CashCampaign campaign = new CashCampaign();
        campaign.setOrganization(organization);
        campaign.setTitle(dto.getTitle().trim());
        campaign.setCauseCategory(dto.getCauseCategory());
        campaign.setDescription(dto.getDescription());
        campaign.setTargetAmount(dto.getTargetAmount());
        campaign.setRaisedAmount(BigDecimal.ZERO);
        campaign.setDeadline(dto.getDeadline());
        campaign.setApprovalStatus(ApprovalStatus.PENDING); // Needs Admin Approval
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setImagePath(dto.getImagePath());

        CashCampaign saved = cashCampaignRepository.save(campaign);
        notificationService.sendNotification(organization, "Campaign Submitted",
                "Your campaign '" + saved.getTitle() + "' has been submitted for administrator review.",
                NotificationType.CAMPAIGN_UPDATE, "/cash/details/" + saved.getId());

        auditService.logAction(organization.getEmail(), "CREATE_CAMPAIGN", "CashCampaign", saved.getId(),
                "Created campaign target ₹" + saved.getTargetAmount());
        return saved;
    }

    public void approveCampaign(Long id, boolean approve, String notes, String adminEmail) {
        CashCampaign campaign = getCampaignById(id);
        campaign.setApprovalStatus(approve ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        campaign.setAdminReviewNotes(notes);
        cashCampaignRepository.save(campaign);

        String msg = approve
                ? "Your fundraising campaign '" + campaign.getTitle() + "' was approved and is now live!"
                : "Your fundraising campaign '" + campaign.getTitle() + "' was rejected. Notes: " + notes;

        notificationService.sendNotification(campaign.getOrganization(), "Campaign Moderation Update",
                msg, NotificationType.CAMPAIGN_UPDATE, "/cash/details/" + campaign.getId());

        auditService.logAction(adminEmail, "MODERATE_CAMPAIGN", "CashCampaign", id,
                (approve ? "Approved" : "Rejected") + " campaign. Notes: " + notes);
    }

    public CashTransaction processDemoContribution(CashContributionDto dto, User loggedInDonor) {
        CashCampaign campaign = getCampaignById(dto.getCampaignId());
        if (campaign.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("This campaign is not currently open for donations.");
        }
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new BadRequestException("This campaign is not active.");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.valueOf(10)) < 0) {
            throw new BadRequestException("Minimum donation amount is ₹10.");
        }

        CashTransaction tx = new CashTransaction();
        tx.setCampaign(campaign);
        tx.setDonor(loggedInDonor);
        tx.setDonorName(dto.getDonorName().trim());
        tx.setDonorEmail(dto.getDonorEmail().trim().toLowerCase());
        tx.setAmount(dto.getAmount());
        tx.setPaymentMode("DEMO_SIMULATION (Course Project Demo)");
        tx.setTransactionReference("FB-DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setDonorMessage(dto.getDonorMessage());
        tx.setConfirmed(true);

        CashTransaction savedTx = cashTransactionRepository.save(tx);

        // Update campaign raised amount
        campaign.setRaisedAmount(campaign.getRaisedAmount().add(dto.getAmount()));
        if (campaign.getRaisedAmount().compareTo(campaign.getTargetAmount()) >= 0) {
            campaign.setStatus(CampaignStatus.COMPLETED);
        }
        cashCampaignRepository.save(campaign);

        // Notify Campaign Organizer
        notificationService.sendNotification(campaign.getOrganization(), "New Donation Received",
                "Received demo donation of ₹" + dto.getAmount() + " from " + dto.getDonorName() + " for '" + campaign.getTitle() + "'",
                NotificationType.CAMPAIGN_UPDATE, "/cash/details/" + campaign.getId());

        // Notify Donor if registered
        if (loggedInDonor != null) {
            notificationService.sendNotification(loggedInDonor, "Thank you for your donation!",
                    "Your simulated contribution of ₹" + dto.getAmount() + " for '" + campaign.getTitle() + "' was confirmed. Ref: " + savedTx.getTransactionReference(),
                    NotificationType.GENERAL, "/cash/details/" + campaign.getId());
        }

        auditService.logAction(dto.getDonorEmail(), "DEMO_CONTRIBUTION", "CashTransaction", savedTx.getId(),
                "Contributed ₹" + dto.getAmount() + " to " + campaign.getTitle());

        return savedTx;
    }

    @Transactional(readOnly = true)
    public CashCampaign getCampaignById(Long id) {
        return cashCampaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<CashCampaign> getApprovedActiveCampaigns(CauseCategory category, String keyword) {
        String k = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return cashCampaignRepository.searchCampaigns(ApprovalStatus.APPROVED, CampaignStatus.ACTIVE, category, k);
    }

    @Transactional(readOnly = true)
    public List<CashCampaign> getFeaturedCampaigns() {
        return cashCampaignRepository.findTop4ByApprovalStatusAndStatusOrderByCreatedAtDesc(
                ApprovalStatus.APPROVED, CampaignStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<CashCampaign> getCampaignsByOrganization(User org) {
        return cashCampaignRepository.findByOrganizationOrderByCreatedAtDesc(org);
    }

    @Transactional(readOnly = true)
    public List<CashCampaign> getCampaignsByApprovalStatus(ApprovalStatus status) {
        return cashCampaignRepository.findByApprovalStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public List<CashTransaction> getTransactionsForCampaign(CashCampaign campaign) {
        return cashTransactionRepository.findByCampaignOrderByCreatedAtDesc(campaign);
    }

    @Transactional(readOnly = true)
    public List<CashTransaction> getTransactionsByDonor(User donor) {
        return cashTransactionRepository.findByDonorOrderByCreatedAtDesc(donor);
    }

    @Transactional(readOnly = true)
    public CashTransaction getTransactionById(Long id) {
        return cashTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }
}
