package com.foodbridge.repository;

import com.foodbridge.entity.DonationRequest;
import com.foodbridge.entity.Pickup;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.PickupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PickupRepository extends JpaRepository<Pickup, Long> {

    List<Pickup> findByDonorOrderByScheduledDateTimeDesc(User donor);

    List<Pickup> findByRecipientOrgOrderByScheduledDateTimeDesc(User recipientOrg);

    List<Pickup> findByStatusOrderByScheduledDateTimeDesc(PickupStatus status);

    Optional<Pickup> findByDonationRequest(DonationRequest donationRequest);

    long countByStatus(PickupStatus status);
}
