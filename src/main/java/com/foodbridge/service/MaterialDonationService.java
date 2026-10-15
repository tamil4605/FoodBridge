package com.foodbridge.service;

import com.foodbridge.dto.MaterialDonationDto;
import com.foodbridge.entity.MaterialDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.ItemCondition;
import com.foodbridge.entity.enums.MaterialCategory;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.MaterialDonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MaterialDonationService {

    private final MaterialDonationRepository materialDonationRepository;
    private final AuditService auditService;

    public MaterialDonationService(MaterialDonationRepository materialDonationRepository, AuditService auditService) {
        this.materialDonationRepository = materialDonationRepository;
        this.auditService = auditService;
    }

    public MaterialDonation createMaterialDonation(User donor, MaterialDonationDto dto) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be at least 1.");
        }
        if (dto.getExpiryDate() != null && dto.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Item expiry date cannot be in the past.");
        }

        MaterialDonation mat = new MaterialDonation();
        mat.setDonor(donor);
        mat.setItemName(dto.getItemName().trim());
        mat.setCategory(dto.getCategory());
        mat.setDescription(dto.getDescription());
        mat.setQuantity(dto.getQuantity());
        mat.setRemainingQuantity(dto.getQuantity());
        mat.setUnit(dto.getUnit().trim());
        mat.setConditionStatus(dto.getConditionStatus());
        mat.setExpiryDate(dto.getExpiryDate());
        mat.setPickupAddress(dto.getPickupAddress().trim());
        mat.setCity(dto.getCity().trim());
        mat.setPincode(dto.getPincode().trim());
        mat.setPreferredPickupDate(dto.getPreferredPickupDate());
        mat.setPickupInstructions(dto.getPickupInstructions());
        mat.setImagePath(dto.getImagePath());
        mat.setStatus(DonationStatus.AVAILABLE);

        MaterialDonation saved = materialDonationRepository.save(mat);
        auditService.logAction(donor.getEmail(), "CREATE_MATERIAL_DONATION", "MaterialDonation", saved.getId(),
                "Created material listing: " + saved.getItemName() + " (" + saved.getQuantity() + " " + saved.getUnit() + ")");
        return saved;
    }

    public MaterialDonation updateMaterialDonation(Long id, User donor, MaterialDonationDto dto) {
        MaterialDonation mat = getMaterialDonationById(id);
        if (!mat.getDonor().getId().equals(donor.getId())) {
            throw new UnauthorizedException("You are not authorized to edit this material listing.");
        }
        if (mat.getStatus() != DonationStatus.AVAILABLE) {
            throw new BadRequestException("Only available material listings can be modified.");
        }

        mat.setItemName(dto.getItemName().trim());
        mat.setCategory(dto.getCategory());
        mat.setDescription(dto.getDescription());
        mat.setQuantity(dto.getQuantity());
        mat.setRemainingQuantity(dto.getQuantity());
        mat.setUnit(dto.getUnit().trim());
        mat.setConditionStatus(dto.getConditionStatus());
        mat.setExpiryDate(dto.getExpiryDate());
        mat.setPickupAddress(dto.getPickupAddress().trim());
        mat.setCity(dto.getCity().trim());
        mat.setPincode(dto.getPincode().trim());
        mat.setPreferredPickupDate(dto.getPreferredPickupDate());
        mat.setPickupInstructions(dto.getPickupInstructions());

        MaterialDonation updated = materialDonationRepository.save(mat);
        auditService.logAction(donor.getEmail(), "UPDATE_MATERIAL_DONATION", "MaterialDonation", id, "Updated listing");
        return updated;
    }

    public void withdrawMaterialDonation(Long id, User donor) {
        MaterialDonation mat = getMaterialDonationById(id);
        if (!mat.getDonor().getId().equals(donor.getId())) {
            throw new UnauthorizedException("You can only withdraw your own material listings.");
        }
        if (mat.getStatus() != DonationStatus.AVAILABLE) {
            throw new BadRequestException("Cannot withdraw a listing that is not currently AVAILABLE.");
        }
        mat.setStatus(DonationStatus.WITHDRAWN);
        materialDonationRepository.save(mat);
        auditService.logAction(donor.getEmail(), "WITHDRAW_MATERIAL_DONATION", "MaterialDonation", id, "Withdrew listing");
    }

    @Transactional(readOnly = true)
    public MaterialDonation getMaterialDonationById(Long id) {
        return materialDonationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material donation not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MaterialDonation> getAvailableMaterials(MaterialCategory category, ItemCondition condition,
                                                         String city, String pincode, String keyword) {
        String c = (city != null && !city.isBlank()) ? city.trim() : null;
        String p = (pincode != null && !pincode.isBlank()) ? pincode.trim() : null;
        String k = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        return materialDonationRepository.searchMaterials(
                DonationStatus.AVAILABLE, category, condition, c, p, k);
    }

    @Transactional(readOnly = true)
    public List<MaterialDonation> getFeaturedMaterials() {
        return materialDonationRepository.findTop6ByStatusOrderByCreatedAtDesc(DonationStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<MaterialDonation> getListingsByDonor(User donor) {
        return materialDonationRepository.findByDonorOrderByCreatedAtDesc(donor);
    }
}
