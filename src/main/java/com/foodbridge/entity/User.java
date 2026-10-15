package com.foodbridge.entity;

import com.foodbridge.entity.enums.AccountType;
import com.foodbridge.entity.enums.OrgType;
import com.foodbridge.entity.enums.OrgVerificationStatus;
import com.foodbridge.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AccountType accountType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Role role;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String pincode;

    // Organization Specific Fields
    @Column(length = 180)
    private String organizationName;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private OrgType organizationType;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private OrgVerificationStatus verificationStatus;

    @Column(columnDefinition = "TEXT")
    private String verificationNotes;

    private LocalDateTime verifiedAt;

    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String name, String email, String password, String phone,
                AccountType accountType, Role role, String address, String city,
                String state, String pincode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.accountType = accountType;
        this.role = role;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.verificationStatus == null && this.accountType == AccountType.RECIPIENT_ORG) {
            this.verificationStatus = OrgVerificationStatus.PENDING;
        } else if (this.verificationStatus == null) {
            this.verificationStatus = OrgVerificationStatus.VERIFIED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public OrgType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrgType organizationType) {
        this.organizationType = organizationType;
    }

    public OrgVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(OrgVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEffectiveName() {
        if (organizationName != null && !organizationName.isBlank()) {
            return organizationName;
        }
        return name;
    }
}
