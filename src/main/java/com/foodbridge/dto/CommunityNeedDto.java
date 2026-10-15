package com.foodbridge.dto;

import com.foodbridge.entity.enums.NeedCategory;
import com.foodbridge.entity.enums.NeedPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class CommunityNeedDto {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Category is required")
    private NeedCategory category;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Target quantity or amount is required")
    @DecimalMin(value = "0.5", message = "Target must be at least 0.5")
    private Double targetQuantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Priority is required")
    private NeedPriority priority;

    @NotNull(message = "Target date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate targetDate;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    public CommunityNeedDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NeedCategory getCategory() {
        return category;
    }

    public void setCategory(NeedCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Double targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public NeedPriority getPriority() {
        return priority;
    }

    public void setPriority(NeedPriority priority) {
        this.priority = priority;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
