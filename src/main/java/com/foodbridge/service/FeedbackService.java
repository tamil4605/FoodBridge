package com.foodbridge.service;

import com.foodbridge.dto.FeedbackDto;
import com.foodbridge.entity.Feedback;
import com.foodbridge.entity.Pickup;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.PickupStatus;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.FeedbackRepository;
import com.foodbridge.repository.PickupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final PickupRepository pickupRepository;
    private final AuditService auditService;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           PickupRepository pickupRepository,
                           AuditService auditService) {
        this.feedbackRepository = feedbackRepository;
        this.pickupRepository = pickupRepository;
        this.auditService = auditService;
    }

    public Feedback submitFeedback(FeedbackDto dto, User giver) {
        Pickup pickup = pickupRepository.findById(dto.getPickupId())
                .orElseThrow(() -> new ResourceNotFoundException("Pickup not found: " + dto.getPickupId()));

        if (pickup.getStatus() != PickupStatus.HANDED_OVER && pickup.getStatus() != PickupStatus.COLLECTED) {
            throw new BadRequestException("Feedback can only be submitted for completed or collected donations.");
        }

        // Determine receiver
        User receiver;
        if (pickup.getDonor().getId().equals(giver.getId())) {
            receiver = pickup.getRecipientOrg();
        } else if (pickup.getRecipientOrg().getId().equals(giver.getId())) {
            receiver = pickup.getDonor();
        } else {
            throw new UnauthorizedException("You were not part of this donation pickup.");
        }

        feedbackRepository.findByPickupAndGiver(pickup, giver).ifPresent(existing -> {
            throw new BadRequestException("You have already submitted feedback for this donation.");
        });

        Feedback feedback = new Feedback(giver, receiver, pickup, dto.getRating(), dto.getComment().trim());
        Feedback saved = feedbackRepository.save(feedback);

        auditService.logAction(giver.getEmail(), "SUBMIT_FEEDBACK", "Feedback", saved.getId(),
                "Rated " + receiver.getEffectiveName() + " with " + dto.getRating() + " stars");
        return saved;
    }

    public void reportFeedback(Long feedbackId, String reason, User reporter) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + feedbackId));
        feedback.setReported(true);
        feedback.setReportReason(reason);
        feedbackRepository.save(feedback);
        auditService.logAction(reporter.getEmail(), "REPORT_FEEDBACK", "Feedback", feedbackId, "Reported feedback: " + reason);
    }

    public void moderateFeedback(Long feedbackId, boolean delete, String adminEmail) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + feedbackId));
        if (delete) {
            feedbackRepository.delete(feedback);
            auditService.logAction(adminEmail, "MODERATE_FEEDBACK", "Feedback", feedbackId, "Deleted reported feedback");
        } else {
            feedback.setReported(false);
            feedback.setModerated(true);
            feedbackRepository.save(feedback);
            auditService.logAction(adminEmail, "MODERATE_FEEDBACK", "Feedback", feedbackId, "Approved reported feedback");
        }
    }

    @Transactional(readOnly = true)
    public List<Feedback> getFeedbacksForUser(User user) {
        return feedbackRepository.findByReceiverOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Feedback> getReportedFeedbacks() {
        return feedbackRepository.findByReportedTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(User user) {
        Double avg = feedbackRepository.getAverageRatingForUser(user);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 5.0;
    }
}
