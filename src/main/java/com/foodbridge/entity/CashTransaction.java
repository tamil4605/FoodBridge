package com.foodbridge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_transactions")
public class CashTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CashCampaign campaign;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id")
    private User donor;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String donorName;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String donorEmail;

    @NotNull
    @DecimalMin("1.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    private String paymentMode = "DEMO_SIMULATION";

    @Column(nullable = false, unique = true, length = 80)
    private String transactionReference;

    @Column(length = 500)
    private String donorMessage;

    @Column(nullable = false)
    private boolean confirmed = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CashTransaction() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.transactionReference == null) {
            this.transactionReference = "FB-DEMO-" + System.currentTimeMillis();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CashCampaign getCampaign() {
        return campaign;
    }

    public void setCampaign(CashCampaign campaign) {
        this.campaign = campaign;
    }

    public User getDonor() {
        return donor;
    }

    public void setDonor(User donor) {
        this.donor = donor;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getDonorMessage() {
        return donorMessage;
    }

    public void setDonorMessage(String donorMessage) {
        this.donorMessage = donorMessage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
