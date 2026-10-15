package com.foodbridge.controller;

import com.foodbridge.dto.DonationRequestDto;
import com.foodbridge.dto.MaterialDonationDto;
import com.foodbridge.entity.MaterialDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DonationType;
import com.foodbridge.entity.enums.ItemCondition;
import com.foodbridge.entity.enums.MaterialCategory;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.MaterialDonationService;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/materials")
public class MaterialDonationController {

    private final MaterialDonationService materialDonationService;
    private final UserService userService;

    public MaterialDonationController(MaterialDonationService materialDonationService, UserService userService) {
        this.materialDonationService = materialDonationService;
        this.userService = userService;
    }

    @GetMapping
    public String listMaterials(@RequestParam(value = "category", required = false) MaterialCategory category,
                                @RequestParam(value = "condition", required = false) ItemCondition condition,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "pincode", required = false) String pincode,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("materialListings", materialDonationService.getAvailableMaterials(category, condition, city, pincode, keyword));
        model.addAttribute("categories", MaterialCategory.values());
        model.addAttribute("conditions", ItemCondition.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedCondition", condition);
        model.addAttribute("city", city);
        model.addAttribute("pincode", pincode);
        model.addAttribute("keyword", keyword);
        return "materials/list";
    }

    @GetMapping("/details/{id}")
    public String materialDetails(@PathVariable("id") Long id, Model model) {
        MaterialDonation mat = materialDonationService.getMaterialDonationById(id);
        User currentUser = userService.getCurrentUser();
        model.addAttribute("material", mat);
        model.addAttribute("currentUser", currentUser);

        DonationRequestDto reqDto = new DonationRequestDto();
        reqDto.setDonationId(mat.getId());
        reqDto.setDonationType(DonationType.MATERIAL);
        reqDto.setRequestedQuantity((double) mat.getRemainingQuantity());
        model.addAttribute("requestDto", reqDto);

        return "materials/details";
    }

    @GetMapping("/create")
    public String createMaterialPage(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getRole() != Role.ROLE_INDIVIDUAL_DONOR && currentUser.getRole() != Role.ROLE_BUSINESS_DONOR) {
            return "redirect:/error/403";
        }

        if (!model.containsAttribute("materialDto")) {
            MaterialDonationDto dto = new MaterialDonationDto();
            dto.setPickupAddress(currentUser.getAddress());
            dto.setCity(currentUser.getCity());
            dto.setPincode(currentUser.getPincode());
            model.addAttribute("materialDto", dto);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", MaterialCategory.values());
        model.addAttribute("conditions", ItemCondition.values());
        return "materials/create";
    }

    @PostMapping("/create")
    public String createMaterial(@Valid @ModelAttribute("materialDto") MaterialDonationDto materialDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", MaterialCategory.values());
            model.addAttribute("conditions", ItemCondition.values());
            return "materials/create";
        }

        try {
            MaterialDonation saved = materialDonationService.createMaterialDonation(currentUser, materialDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Material listing '" + saved.getItemName() + "' has been published successfully!");
            return "redirect:/materials/details/" + saved.getId();
        } catch (BadRequestException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", MaterialCategory.values());
            model.addAttribute("conditions", ItemCondition.values());
            return "materials/create";
        }
    }

    @GetMapping("/my-listings")
    public String myListings(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("materialListings", materialDonationService.getListingsByDonor(currentUser));
        return "materials/my-listings";
    }

    @PostMapping("/withdraw/{id}")
    public String withdrawListing(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        try {
            materialDonationService.withdrawMaterialDonation(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Material listing withdrawn successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/materials/my-listings";
    }
}
