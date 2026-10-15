package com.foodbridge.service;

import com.foodbridge.dto.DonationRequestDto;
import com.foodbridge.entity.DonationRequest;
import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.MaterialDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.DonationType;
import com.foodbridge.entity.enums.NotificationType;
import com.foodbridge.entity.enums.RequestStatus;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.DonationRequestRepository;
import com.foodbridge.repository.FoodDonationRepository;
import com.foodbridge.repository.MaterialDonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class DonationRequestService {

    private final DonationRequestRepository donationRequestRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final MaterialDonationRepository materialDonationRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public DonationRequestService(DonationRequestRepository donationRequestRepository,
                                  FoodDonationRepository foodDonationRepository,
                                  MaterialDonationRepository materialDonationRepository,
                                  NotificationService notificationService,
                                  AuditService auditService) {
        this.donationRequestRepository = donationRequestRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.materialDonationRepository = materialDonationRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    public DonationRequest submitRequest(User requesterOrg, DonationRequestDto dto) {
        if (dto.getRequestedQuantity() == null || dto.getRequestedQuantity() <= 0) {
            throw new BadRequestException("Requested quantity must be greater than zero.");
        }

        DonationRequest request = new DonationRequest();
        request.setRequesterOrg(requesterOrg);
        request.setDonationType(dto.getDonationType());
        request.setRequestedQuantity(dto.getRequestedQuantity());
        request.setMessage(dto.getMessage());
        request.setStatus(RequestStatus.PENDING);

        if (dto.getDonationType() == DonationType.FOOD) {
            FoodDonation food = foodDonationRepository.findById(dto.getDonationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food donation not found: " + dto.getDonationId()));

            if (food.getStatus() != DonationStatus.AVAILABLE) {
                throw new BadRequestException("This food donation is no longer available.");
            }
            if (food.isExpired()) {
                food.setStatus(DonationStatus.EXPIRED);
                foodDonationRepository.save(food);
                throw new BadRequestException("This food listing has passed its safe collection deadline.");
            }
            if (dto.getRequestedQuantity() > food.getRemainingQuantity()) {
                throw new BadRequestException("Requested quantity (" + dto.getRequestedQuantity() +
                        ") exceeds available remaining quantity (" + food.getRemainingQuantity() + ").");
            }

            // Prevent duplicate pending request
            donationRequestRepository.findByFoodDonationAndRequesterOrgAndStatusIn(
                    food, requesterOrg, List.of(RequestStatus.PENDING, RequestStatus.ACCEPTED))
                    .ifPresent(existing -> {
                        throw new BadRequestException("You already have an active request for this food listing.");
                    });

            request.setFoodDonation(food);
            request.setUnit(food.getUnit());
            food.setStatus(DonationStatus.REQUESTED);
            foodDonationRepository.save(food);

            DonationRequest saved = donationRequestRepository.save(request);

            // Notify Donor
            notificationService.sendNotification(food.getDonor(), "New Request for " + food.getFoodName(),
                    requesterOrg.getEffectiveName() + " has requested " + dto.getRequestedQuantity() + " " + food.getUnit() + " of " + food.getFoodName(),
                    NotificationType.REQUEST_RECEIVED, "/dashboard/donor");

            auditService.logAction(requesterOrg.getEmail(), "REQUEST_FOOD", "DonationRequest", saved.getId(),
                    "Requested " + dto.getRequestedQuantity() + " " + food.getUnit() + " of " + food.getFoodName());
            return saved;

        } else {
            MaterialDonation mat = materialDonationRepository.findById(dto.getDonationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material donation not found: " + dto.getDonationId()));

            if (mat.getStatus() != DonationStatus.AVAILABLE) {
                throw new BadRequestException("This material donation is no longer available.");
            }
            if (dto.getRequestedQuantity() > mat.getRemainingQuantity()) {
                throw new BadRequestException("Requested quantity (" + dto.getRequestedQuantity() +
                        ") exceeds available remaining quantity (" + mat.getRemainingQuantity() + ").");
            }

            donationRequestRepository.findByMaterialDonationAndRequesterOrgAndStatusIn(
                    mat, requesterOrg, List.of(RequestStatus.PENDING, RequestStatus.ACCEPTED))
                    .ifPresent(existing -> {
                        throw new BadRequestException("You already have an active request for this material listing.");
                    });

            request.setMaterialDonation(mat);
            request.setUnit(mat.getUnit());
            mat.setStatus(DonationStatus.REQUESTED);
            materialDonationRepository.save(mat);

            DonationRequest saved = donationRequestRepository.save(request);

            // Notify Donor
            notificationService.sendNotification(mat.getDonor(), "New Request for " + mat.getItemName(),
                    requesterOrg.getEffectiveName() + " has requested " + dto.getRequestedQuantity().intValue() + " " + mat.getUnit() + " of " + mat.getItemName(),
                    NotificationType.REQUEST_RECEIVED, "/dashboard/donor");

            auditService.logAction(requesterOrg.getEmail(), "REQUEST_MATERIAL", "DonationRequest", saved.getId(),
                    "Requested " + dto.getRequestedQuantity() + " " + mat.getUnit() + " of " + mat.getItemName());
            return saved;
        }
    }

    public void acceptRequest(Long requestId, User donor) {
        DonationRequest request = getRequestById(requestId);
        User actualDonor = request.getDonor();
        if (actualDonor == null || !actualDonor.getId().equals(donor.getId())) {
            throw new UnauthorizedException("You are not authorized to respond to this request.");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be accepted.");
        }

        request.setStatus(RequestStatus.ACCEPTED);

        if (request.getDonationType() == DonationType.FOOD && request.getFoodDonation() != null) {
            FoodDonation food = request.getFoodDonation();
            food.setStatus(DonationStatus.ACCEPTED);
            foodDonationRepository.save(food);
        } else if (request.getDonationType() == DonationType.MATERIAL && request.getMaterialDonation() != null) {
            MaterialDonation mat = request.getMaterialDonation();
            mat.setStatus(DonationStatus.ACCEPTED);
            materialDonationRepository.save(mat);
        }

        donationRequestRepository.save(request);

        // Notify Recipient Org
        notificationService.sendNotification(request.getRequesterOrg(), "Request Accepted!",
                donor.getEffectiveName() + " has accepted your request for " + request.getItemTitle() + ". Please proceed with pickup coordination.",
                NotificationType.REQUEST_ACCEPTED, "/pickups/schedule?requestId=" + request.getId());

        auditService.logAction(donor.getEmail(), "ACCEPT_REQUEST", "DonationRequest", requestId, "Accepted request");
    }

    public void rejectRequest(Long requestId, String reason, User donor) {
        DonationRequest request = getRequestById(requestId);
        User actualDonor = request.getDonor();
        if (actualDonor == null || !actualDonor.getId().equals(donor.getId())) {
            throw new UnauthorizedException("You are not authorized to respond to this request.");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be rejected.");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);

        // Restore listing availability
        if (request.getDonationType() == DonationType.FOOD && request.getFoodDonation() != null) {
            FoodDonation food = request.getFoodDonation();
            food.setStatus(DonationStatus.AVAILABLE);
            foodDonationRepository.save(food);
        } else if (request.getDonationType() == DonationType.MATERIAL && request.getMaterialDonation() != null) {
            MaterialDonation mat = request.getMaterialDonation();
            mat.setStatus(DonationStatus.AVAILABLE);
            materialDonationRepository.save(mat);
        }

        donationRequestRepository.save(request);

        // Notify Recipient Org
        notificationService.sendNotification(request.getRequesterOrg(), "Request Declined",
                donor.getEffectiveName() + " was unable to fulfill your request for " + request.getItemTitle() + (reason != null ? ". Reason: " + reason : "."),
                NotificationType.REQUEST_REJECTED, "/dashboard/organization");

        auditService.logAction(donor.getEmail(), "REJECT_REQUEST", "DonationRequest", requestId, "Rejected request");
    }

    @Transactional(readOnly = true)
    public DonationRequest getRequestById(Long id) {
        return donationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation request not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<DonationRequest> getRequestsForDonor(User donor) {
        return donationRequestRepository.findByDonorOrderByCreatedAtDesc(donor);
    }

    @Transactional(readOnly = true)
    public List<DonationRequest> getRequestsByOrganization(User requesterOrg) {
        return donationRequestRepository.findByRequesterOrgOrderByCreatedAtDesc(requesterOrg);
    }
}
