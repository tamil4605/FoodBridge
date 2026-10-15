package com.foodbridge.controller;

import com.foodbridge.dto.DonationRequestDto;
import com.foodbridge.dto.FoodDonationDto;
import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DietaryType;
import com.foodbridge.entity.enums.DonationType;
import com.foodbridge.entity.enums.FoodCategory;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.FoodDonationService;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/food")
public class FoodDonationController {

    private final FoodDonationService foodDonationService;
    private final UserService userService;

    public FoodDonationController(FoodDonationService foodDonationService, UserService userService) {
        this.foodDonationService = foodDonationService;
        this.userService = userService;
    }

    @GetMapping
    public String listFood(@RequestParam(value = "category", required = false) FoodCategory category,
                           @RequestParam(value = "dietaryType", required = false) DietaryType dietaryType,
                           @RequestParam(value = "city", required = false) String city,
                           @RequestParam(value = "pincode", required = false) String pincode,
                           @RequestParam(value = "keyword", required = false) String keyword,
                           Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("foodListings", foodDonationService.getAvailableFoodListings(category, dietaryType, city, pincode, keyword));
        model.addAttribute("categories", FoodCategory.values());
        model.addAttribute("dietaryTypes", DietaryType.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedDietaryType", dietaryType);
        model.addAttribute("city", city);
        model.addAttribute("pincode", pincode);
        model.addAttribute("keyword", keyword);
        return "food/list";
    }

    @GetMapping("/details/{id}")
    public String foodDetails(@PathVariable("id") Long id, Model model) {
        FoodDonation food = foodDonationService.getFoodDonationById(id);
        User currentUser = userService.getCurrentUser();
        model.addAttribute("food", food);
        model.addAttribute("currentUser", currentUser);

        DonationRequestDto reqDto = new DonationRequestDto();
        reqDto.setDonationId(food.getId());
        reqDto.setDonationType(DonationType.FOOD);
        reqDto.setRequestedQuantity(food.getRemainingQuantity());
        model.addAttribute("requestDto", reqDto);

        return "food/details";
    }

    @GetMapping("/create")
    public String createFoodPage(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getRole() != Role.ROLE_INDIVIDUAL_DONOR && currentUser.getRole() != Role.ROLE_BUSINESS_DONOR) {
            return "redirect:/error/403";
        }

        if (!model.containsAttribute("foodDto")) {
            FoodDonationDto dto = new FoodDonationDto();
            dto.setPickupAddress(currentUser.getAddress());
            dto.setCity(currentUser.getCity());
            dto.setPincode(currentUser.getPincode());
            model.addAttribute("foodDto", dto);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", FoodCategory.values());
        model.addAttribute("dietaryTypes", DietaryType.values());
        return "food/create";
    }

    @PostMapping("/create")
    public String createFood(@Valid @ModelAttribute("foodDto") FoodDonationDto foodDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", FoodCategory.values());
            model.addAttribute("dietaryTypes", DietaryType.values());
            return "food/create";
        }

        try {
            FoodDonation saved = foodDonationService.createFoodDonation(currentUser, foodDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Food listing '" + saved.getFoodName() + "' has been published successfully!");
            return "redirect:/food/details/" + saved.getId();
        } catch (BadRequestException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", FoodCategory.values());
            model.addAttribute("dietaryTypes", DietaryType.values());
            return "food/create";
        }
    }

    @GetMapping("/my-listings")
    public String myListings(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("foodListings", foodDonationService.getListingsByDonor(currentUser));
        return "food/my-listings";
    }

    @PostMapping("/withdraw/{id}")
    public String withdrawListing(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        try {
            foodDonationService.withdrawFoodDonation(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Listing withdrawn successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/food/my-listings";
    }
}
