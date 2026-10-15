package com.foodbridge.service;

import com.foodbridge.dto.PickupScheduleDto;
import com.foodbridge.entity.DonationRequest;
import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.MaterialDonation;
import com.foodbridge.entity.Pickup;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.DonationType;
import com.foodbridge.entity.enums.NotificationType;
import com.foodbridge.entity.enums.PickupStatus;
import com.foodbridge.entity.enums.RequestStatus;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.DonationRequestRepository;
import com.foodbridge.repository.FoodDonationRepository;
import com.foodbridge.repository.MaterialDonationRepository;
import com.foodbridge.repository.PickupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PickupService {

    private final PickupRepository pickupRepository;
    private final DonationRequestRepository donationRequestRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final MaterialDonationRepository materialDonationRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public PickupService(PickupRepository pickupRepository,
                         DonationRequestRepository donationRequestRepository,
                         FoodDonationRepository foodDonationRepository,
                         MaterialDonationRepository materialDonationRepository,
                         NotificationService notificationService,
                         AuditService auditService) {
        this.pickupRepository = pickupRepository;
        this.donationRequestRepository = donationRequestRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.materialDonationRepository = materialDonationRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    public Pickup schedulePickup(PickupScheduleDto dto, User schedulingUser) {
        DonationRequest request = donationRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + dto.getRequestId()));

        if (request.getStatus() != RequestStatus.ACCEPTED) {
            throw new BadRequestException("Pickup can only be scheduled for accepted requests.");
        }
        if (dto.getScheduledDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Pickup schedule time must be in the future.");
        }

        // Check if already scheduled
        pickupRepository.findByDonationRequest(request).ifPresent(p -> {
            throw new BadRequestException("A pickup has already been scheduled for this request.");
        });

        User donor = request.getDonor();
        User recipientOrg = request.getRequesterOrg();

        Pickup pickup = new Pickup();
        pickup.setDonationRequest(request);
        pickup.setDonor(donor);
        pickup.setRecipientOrg(recipientOrg);
        pickup.setScheduledDateTime(dto.getScheduledDateTime());
        pickup.setPickupAddress(dto.getPickupAddress().trim());
        pickup.setCity(dto.getCity().trim());
        pickup.setPincode(dto.getPincode().trim());
        pickup.setSpecialInstructions(dto.getSpecialInstructions());
        pickup.setStatus(PickupStatus.SCHEDULED);

        // Update donation status to PICKUP_SCHEDULED
        if (request.getDonationType() == DonationType.FOOD && request.getFoodDonation() != null) {
            FoodDonation food = request.getFoodDonation();
            food.setStatus(DonationStatus.PICKUP_SCHEDULED);
            foodDonationRepository.save(food);
        } else if (request.getDonationType() == DonationType.MATERIAL && request.getMaterialDonation() != null) {
            MaterialDonation mat = request.getMaterialDonation();
            mat.setStatus(DonationStatus.PICKUP_SCHEDULED);
            materialDonationRepository.save(mat);
        }

        Pickup saved = pickupRepository.save(pickup);

        // Notify both parties
        notificationService.sendNotification(donor, "Pickup Scheduled",
                "Pickup for " + request.getItemTitle() + " has been scheduled for " + dto.getScheduledDateTime() + " by " + recipientOrg.getEffectiveName(),
                NotificationType.PICKUP_SCHEDULED, "/pickups/details/" + saved.getId());

        notificationService.sendNotification(recipientOrg, "Pickup Confirmed",
                "Your pickup for " + request.getItemTitle() + " is confirmed for " + dto.getScheduledDateTime(),
                NotificationType.PICKUP_SCHEDULED, "/pickups/details/" + saved.getId());

        auditService.logAction(schedulingUser.getEmail(), "SCHEDULE_PICKUP", "Pickup", saved.getId(),
                "Scheduled pickup for " + request.getItemTitle());
        return saved;
    }

    public void confirmCollection(Long pickupId, User currentUser) {
        Pickup pickup = getPickupById(pickupId);
        if (!pickup.getRecipientOrg().getId().equals(currentUser.getId()) &&
            !pickup.getDonor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to confirm collection for this pickup.");
        }

        pickup.setStatus(PickupStatus.COLLECTED);
        pickup.setCollectedAt(LocalDateTime.now());

        if (pickup.getDonationRequest().getDonationType() == DonationType.FOOD) {
            FoodDonation food = pickup.getDonationRequest().getFoodDonation();
            if (food != null) {
                food.setStatus(DonationStatus.COLLECTED);
                foodDonationRepository.save(food);
            }
        } else {
            MaterialDonation mat = pickup.getDonationRequest().getMaterialDonation();
            if (mat != null) {
                mat.setStatus(DonationStatus.COLLECTED);
                materialDonationRepository.save(mat);
            }
        }

        pickupRepository.save(pickup);

        notificationService.sendNotification(pickup.getDonor(), "Donation Collected",
                pickup.getRecipientOrg().getEffectiveName() + " has collected " + pickup.getDonationRequest().getItemTitle(),
                NotificationType.PICKUP_COLLECTED, "/pickups/details/" + pickup.getId());

        auditService.logAction(currentUser.getEmail(), "CONFIRM_COLLECTION", "Pickup", pickupId, "Collection confirmed");
    }

    public void confirmHandover(Long pickupId, User currentUser) {
        Pickup pickup = getPickupById(pickupId);
        if (!pickup.getDonor().getId().equals(currentUser.getId()) &&
            !pickup.getRecipientOrg().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to confirm handover for this pickup.");
        }

        pickup.setStatus(PickupStatus.HANDED_OVER);
        pickup.setHandedOverAt(LocalDateTime.now());

        DonationRequest request = pickup.getDonationRequest();
        request.setStatus(RequestStatus.COMPLETED);
        donationRequestRepository.save(request);

        if (request.getDonationType() == DonationType.FOOD && request.getFoodDonation() != null) {
            FoodDonation food = request.getFoodDonation();
            food.setStatus(DonationStatus.COMPLETED);
            double remaining = food.getRemainingQuantity() - request.getRequestedQuantity();
            food.setRemainingQuantity(Math.max(0.0, remaining));
            foodDonationRepository.save(food);
        } else if (request.getDonationType() == DonationType.MATERIAL && request.getMaterialDonation() != null) {
            MaterialDonation mat = request.getMaterialDonation();
            mat.setStatus(DonationStatus.COMPLETED);
            int remaining = mat.getRemainingQuantity() - request.getRequestedQuantity().intValue();
            mat.setRemainingQuantity(Math.max(0, remaining));
            materialDonationRepository.save(mat);
        }

        pickupRepository.save(pickup);

        // Feedback reminder notifications
        notificationService.sendNotification(pickup.getRecipientOrg(), "Handover Completed - Leave Feedback",
                "Handover of " + request.getItemTitle() + " is completed. Please rate your experience with " + pickup.getDonor().getEffectiveName(),
                NotificationType.FEEDBACK_REMINDER, "/feedback/create?pickupId=" + pickup.getId());

        notificationService.sendNotification(pickup.getDonor(), "Donation Completed - Thank You!",
                "Your donation of " + request.getItemTitle() + " was successfully completed. Thank you for making an impact!",
                NotificationType.PICKUP_COMPLETED, "/dashboard/donor");

        auditService.logAction(currentUser.getEmail(), "CONFIRM_HANDOVER", "Pickup", pickupId, "Handover completed");
    }

    public void cancelPickup(Long pickupId, String reason, User currentUser) {
        Pickup pickup = getPickupById(pickupId);
        if (!pickup.getDonor().getId().equals(currentUser.getId()) &&
            !pickup.getRecipientOrg().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to cancel this pickup.");
        }

        pickup.setStatus(PickupStatus.CANCELLED);
        pickup.setCancellationReason(reason);
        pickupRepository.save(pickup);

        DonationRequest req = pickup.getDonationRequest();
        req.setStatus(RequestStatus.CANCELLED);
        donationRequestRepository.save(req);

        // Restore listing to Available
        if (req.getDonationType() == DonationType.FOOD && req.getFoodDonation() != null) {
            FoodDonation food = req.getFoodDonation();
            food.setStatus(DonationStatus.AVAILABLE);
            foodDonationRepository.save(food);
        } else if (req.getDonationType() == DonationType.MATERIAL && req.getMaterialDonation() != null) {
            MaterialDonation mat = req.getMaterialDonation();
            mat.setStatus(DonationStatus.AVAILABLE);
            materialDonationRepository.save(mat);
        }

        notificationService.sendNotification(pickup.getDonor(), "Pickup Cancelled",
                "Pickup for " + req.getItemTitle() + " was cancelled. Reason: " + reason,
                NotificationType.GENERAL, "/dashboard/donor");

        notificationService.sendNotification(pickup.getRecipientOrg(), "Pickup Cancelled",
                "Pickup for " + req.getItemTitle() + " was cancelled. Reason: " + reason,
                NotificationType.GENERAL, "/dashboard/organization");

        auditService.logAction(currentUser.getEmail(), "CANCEL_PICKUP", "Pickup", pickupId, "Cancelled pickup: " + reason);
    }

    @Transactional(readOnly = true)
    public Pickup getPickupById(Long id) {
        return pickupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pickup> getPickupsForDonor(User donor) {
        return pickupRepository.findByDonorOrderByScheduledDateTimeDesc(donor);
    }

    @Transactional(readOnly = true)
    public List<Pickup> getPickupsForRecipientOrg(User recipientOrg) {
        return pickupRepository.findByRecipientOrgOrderByScheduledDateTimeDesc(recipientOrg);
    }
}
