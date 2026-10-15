package com.foodbridge.repository;

import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DietaryType;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodDonationRepository extends JpaRepository<FoodDonation, Long> {

    List<FoodDonation> findByDonorOrderByCreatedAtDesc(User donor);

    List<FoodDonation> findByStatusOrderByCreatedAtDesc(DonationStatus status);

    List<FoodDonation> findTop6ByStatusOrderByCreatedAtDesc(DonationStatus status);

    long countByStatus(DonationStatus status);

    @Query("SELECT f FROM FoodDonation f WHERE f.status = :status AND f.safeCollectionDeadline > :now " +
           "AND (:category IS NULL OR f.category = :category) " +
           "AND (:dietaryType IS NULL OR f.dietaryType = :dietaryType) " +
           "AND (:city IS NULL OR LOWER(f.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:pincode IS NULL OR f.pincode = :pincode) " +
           "AND (:keyword IS NULL OR LOWER(f.foodName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY f.safeCollectionDeadline ASC")
    List<FoodDonation> searchAvailableFood(
            @Param("status") DonationStatus status,
            @Param("now") LocalDateTime now,
            @Param("category") FoodCategory category,
            @Param("dietaryType") DietaryType dietaryType,
            @Param("city") String city,
            @Param("pincode") String pincode,
            @Param("keyword") String keyword
    );

    @Query("SELECT f FROM FoodDonation f WHERE f.status = 'AVAILABLE' AND f.safeCollectionDeadline <= :now")
    List<FoodDonation> findExpiredDonations(@Param("now") LocalDateTime now);
}
