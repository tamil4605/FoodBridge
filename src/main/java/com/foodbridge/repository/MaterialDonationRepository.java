package com.foodbridge.repository;

import com.foodbridge.entity.MaterialDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.ItemCondition;
import com.foodbridge.entity.enums.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialDonationRepository extends JpaRepository<MaterialDonation, Long> {

    List<MaterialDonation> findByDonorOrderByCreatedAtDesc(User donor);

    List<MaterialDonation> findByStatusOrderByCreatedAtDesc(DonationStatus status);

    List<MaterialDonation> findTop6ByStatusOrderByCreatedAtDesc(DonationStatus status);

    long countByStatus(DonationStatus status);

    @Query("SELECT m FROM MaterialDonation m WHERE m.status = :status " +
           "AND (:category IS NULL OR m.category = :category) " +
           "AND (:condition IS NULL OR m.conditionStatus = :condition) " +
           "AND (:city IS NULL OR LOWER(m.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:pincode IS NULL OR m.pincode = :pincode) " +
           "AND (:keyword IS NULL OR LOWER(m.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.createdAt DESC")
    List<MaterialDonation> searchMaterials(
            @Param("status") DonationStatus status,
            @Param("category") MaterialCategory category,
            @Param("condition") ItemCondition condition,
            @Param("city") String city,
            @Param("pincode") String pincode,
            @Param("keyword") String keyword
    );
}
