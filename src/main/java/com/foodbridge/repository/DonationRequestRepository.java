package com.foodbridge.repository;

import com.foodbridge.entity.DonationRequest;
import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.MaterialDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {

    List<DonationRequest> findByRequesterOrgOrderByCreatedAtDesc(User requesterOrg);

    @Query("SELECT r FROM DonationRequest r WHERE r.foodDonation.donor = :donor OR r.materialDonation.donor = :donor ORDER BY r.createdAt DESC")
    List<DonationRequest> findByDonorOrderByCreatedAtDesc(@Param("donor") User donor);

    @Query("SELECT r FROM DonationRequest r WHERE (r.foodDonation.donor = :donor OR r.materialDonation.donor = :donor) AND r.status = :status ORDER BY r.createdAt DESC")
    List<DonationRequest> findByDonorAndStatus(@Param("donor") User donor, @Param("status") RequestStatus status);

    Optional<DonationRequest> findByFoodDonationAndRequesterOrgAndStatusIn(
            FoodDonation foodDonation, User requesterOrg, List<RequestStatus> statuses);

    Optional<DonationRequest> findByMaterialDonationAndRequesterOrgAndStatusIn(
            MaterialDonation materialDonation, User requesterOrg, List<RequestStatus> statuses);

    List<DonationRequest> findByFoodDonation(FoodDonation foodDonation);

    List<DonationRequest> findByMaterialDonation(MaterialDonation materialDonation);

    long countByStatus(RequestStatus status);
}
