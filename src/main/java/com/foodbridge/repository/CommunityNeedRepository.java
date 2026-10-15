package com.foodbridge.repository;

import com.foodbridge.entity.CommunityNeed;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.NeedCategory;
import com.foodbridge.entity.enums.NeedPriority;
import com.foodbridge.entity.enums.NeedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommunityNeedRepository extends JpaRepository<CommunityNeed, Long> {

    List<CommunityNeed> findByOrganizationOrderByCreatedAtDesc(User organization);

    List<CommunityNeed> findByStatusOrderByCreatedAtDesc(NeedStatus status);

    List<CommunityNeed> findTop6ByStatusOrderByCreatedAtDesc(NeedStatus status);

    long countByStatus(NeedStatus status);

    @Query("SELECT n FROM CommunityNeed n WHERE n.status = :status " +
           "AND (:category IS NULL OR n.category = :category) " +
           "AND (:priority IS NULL OR n.priority = :priority) " +
           "AND (:city IS NULL OR LOWER(n.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:pincode IS NULL OR n.pincode = :pincode) " +
           "AND (:keyword IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY CASE n.priority WHEN 'URGENT' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 ELSE 4 END, n.targetDate ASC")
    List<CommunityNeed> searchNeeds(
            @Param("status") NeedStatus status,
            @Param("category") NeedCategory category,
            @Param("priority") NeedPriority priority,
            @Param("city") String city,
            @Param("pincode") String pincode,
            @Param("keyword") String keyword
    );
}
