package com.foodbridge.service;

import com.foodbridge.dto.CommunityNeedDto;
import com.foodbridge.entity.CommunityNeed;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.NeedCategory;
import com.foodbridge.entity.enums.NeedPriority;
import com.foodbridge.entity.enums.NeedStatus;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.CommunityNeedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CommunityNeedService {

    private final CommunityNeedRepository communityNeedRepository;
    private final AuditService auditService;

    public CommunityNeedService(CommunityNeedRepository communityNeedRepository, AuditService auditService) {
        this.communityNeedRepository = communityNeedRepository;
        this.auditService = auditService;
    }

    public CommunityNeed createNeed(User organization, CommunityNeedDto dto) {
        if (dto.getTargetQuantity() == null || dto.getTargetQuantity() <= 0) {
            throw new BadRequestException("Target quantity must be greater than zero.");
        }
        if (dto.getTargetDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Target date must be in the future.");
        }

        CommunityNeed need = new CommunityNeed();
        need.setOrganization(organization);
        need.setTitle(dto.getTitle().trim());
        need.setCategory(dto.getCategory());
        need.setDescription(dto.getDescription());
        need.setTargetQuantity(dto.getTargetQuantity());
        need.setFulfilledQuantity(0.0);
        need.setUnit(dto.getUnit().trim());
        need.setPriority(dto.getPriority());
        need.setTargetDate(dto.getTargetDate());
        need.setCity(dto.getCity().trim());
        need.setPincode(dto.getPincode().trim());
        need.setStatus(NeedStatus.OPEN);

        CommunityNeed saved = communityNeedRepository.save(need);
        auditService.logAction(organization.getEmail(), "CREATE_NEED", "CommunityNeed", saved.getId(),
                "Published community need: " + saved.getTitle());
        return saved;
    }

    public CommunityNeed updateNeed(Long id, User organization, CommunityNeedDto dto) {
        CommunityNeed need = getNeedById(id);
        if (!need.getOrganization().getId().equals(organization.getId())) {
            throw new UnauthorizedException("You can only edit your own organization's community needs.");
        }

        need.setTitle(dto.getTitle().trim());
        need.setCategory(dto.getCategory());
        need.setDescription(dto.getDescription());
        need.setTargetQuantity(dto.getTargetQuantity());
        need.setUnit(dto.getUnit().trim());
        need.setPriority(dto.getPriority());
        need.setTargetDate(dto.getTargetDate());
        need.setCity(dto.getCity().trim());
        need.setPincode(dto.getPincode().trim());

        return communityNeedRepository.save(need);
    }

    public void contributeToNeed(Long id, Double quantityContributed) {
        CommunityNeed need = getNeedById(id);
        if (need.getStatus() == NeedStatus.CLOSED) {
            throw new BadRequestException("This community need is already closed.");
        }
        double current = need.getFulfilledQuantity() != null ? need.getFulfilledQuantity() : 0.0;
        double updated = current + quantityContributed;
        need.setFulfilledQuantity(updated);

        if (updated >= need.getTargetQuantity()) {
            need.setStatus(NeedStatus.FULFILLED);
        } else {
            need.setStatus(NeedStatus.IN_PROGRESS);
        }
        communityNeedRepository.save(need);
    }

    @Transactional(readOnly = true)
    public CommunityNeed getNeedById(Long id) {
        return communityNeedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community need not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<CommunityNeed> getOpenNeeds(NeedCategory category, NeedPriority priority,
                                            String city, String pincode, String keyword) {
        String c = (city != null && !city.isBlank()) ? city.trim() : null;
        String p = (pincode != null && !pincode.isBlank()) ? pincode.trim() : null;
        String k = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        return communityNeedRepository.searchNeeds(NeedStatus.OPEN, category, priority, c, p, k);
    }

    @Transactional(readOnly = true)
    public List<CommunityNeed> getFeaturedNeeds() {
        return communityNeedRepository.findTop6ByStatusOrderByCreatedAtDesc(NeedStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<CommunityNeed> getNeedsByOrganization(User organization) {
        return communityNeedRepository.findByOrganizationOrderByCreatedAtDesc(organization);
    }
}
