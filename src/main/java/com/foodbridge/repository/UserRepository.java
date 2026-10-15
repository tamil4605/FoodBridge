package com.foodbridge.repository;

import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.AccountType;
import com.foodbridge.entity.enums.OrgVerificationStatus;
import com.foodbridge.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByAccountType(AccountType accountType);

    List<User> findByRole(Role role);

    List<User> findByVerificationStatus(OrgVerificationStatus status);

    Page<User> findByAccountType(AccountType accountType, Pageable pageable);

    long countByAccountType(AccountType accountType);

    long countByVerificationStatus(OrgVerificationStatus status);
}
