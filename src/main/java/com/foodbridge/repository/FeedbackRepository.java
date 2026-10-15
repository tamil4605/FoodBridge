package com.foodbridge.repository;

import com.foodbridge.entity.Feedback;
import com.foodbridge.entity.Pickup;
import com.foodbridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByReceiverOrderByCreatedAtDesc(User receiver);

    List<Feedback> findByGiverOrderByCreatedAtDesc(User giver);

    List<Feedback> findByReportedTrueOrderByCreatedAtDesc();

    Optional<Feedback> findByPickupAndGiver(Pickup pickup, User giver);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.receiver = :receiver")
    Double getAverageRatingForUser(@Param("receiver") User receiver);

    long countByReportedTrue();
}
