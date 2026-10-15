package com.foodbridge.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {

    private long totalFoodListings;
    private long totalMaterialListings;
    private long activeFoodAvailable;
    private long activeMaterialsAvailable;
    private long totalRequests;
    private long completedPickups;
    private long activeCampaigns;
    private BigDecimal totalFundsRaised;
    private long totalUsers;
    private long pendingOrgVerifications;
    private long pendingCampaignApprovals;

    public DashboardStatsDto() {
        this.totalFundsRaised = BigDecimal.ZERO;
    }

    public long getTotalFoodListings() {
        return totalFoodListings;
    }

    public void setTotalFoodListings(long totalFoodListings) {
        this.totalFoodListings = totalFoodListings;
    }

    public long getTotalMaterialListings() {
        return totalMaterialListings;
    }

    public void setTotalMaterialListings(long totalMaterialListings) {
        this.totalMaterialListings = totalMaterialListings;
    }

    public long getActiveFoodAvailable() {
        return activeFoodAvailable;
    }

    public void setActiveFoodAvailable(long activeFoodAvailable) {
        this.activeFoodAvailable = activeFoodAvailable;
    }

    public long getActiveMaterialsAvailable() {
        return activeMaterialsAvailable;
    }

    public void setActiveMaterialsAvailable(long activeMaterialsAvailable) {
        this.activeMaterialsAvailable = activeMaterialsAvailable;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getCompletedPickups() {
        return completedPickups;
    }

    public void setCompletedPickups(long completedPickups) {
        this.completedPickups = completedPickups;
    }

    public long getActiveCampaigns() {
        return activeCampaigns;
    }

    public void setActiveCampaigns(long activeCampaigns) {
        this.activeCampaigns = activeCampaigns;
    }

    public BigDecimal getTotalFundsRaised() {
        return totalFundsRaised;
    }

    public void setTotalFundsRaised(BigDecimal totalFundsRaised) {
        this.totalFundsRaised = totalFundsRaised;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getPendingOrgVerifications() {
        return pendingOrgVerifications;
    }

    public void setPendingOrgVerifications(long pendingOrgVerifications) {
        this.pendingOrgVerifications = pendingOrgVerifications;
    }

    public long getPendingCampaignApprovals() {
        return pendingCampaignApprovals;
    }

    public void setPendingCampaignApprovals(long pendingCampaignApprovals) {
        this.pendingCampaignApprovals = pendingCampaignApprovals;
    }
}
