package com.foodbridge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FeedbackDto {

    @NotNull(message = "Pickup ID is required")
    private Long pickupId;

    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank(message = "Feedback comments are required")
    private String comment;

    public FeedbackDto() {
    }

    public Long getPickupId() {
        return pickupId;
    }

    public void setPickupId(Long pickupId) {
        this.pickupId = pickupId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
