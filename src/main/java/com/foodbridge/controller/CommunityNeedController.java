package com.foodbridge.controller;

import com.foodbridge.dto.CommunityNeedDto;
import com.foodbridge.entity.CommunityNeed;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.NeedCategory;
import com.foodbridge.entity.enums.NeedPriority;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.CommunityNeedService;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/needs")
public class CommunityNeedController {

    private final CommunityNeedService communityNeedService;
    private final UserService userService;

    public CommunityNeedController(CommunityNeedService communityNeedService, UserService userService) {
        this.communityNeedService = communityNeedService;
        this.userService = userService;
    }

    @GetMapping
    public String listNeeds(@RequestParam(value = "category", required = false) NeedCategory category,
                            @RequestParam(value = "priority", required = false) NeedPriority priority,
                            @RequestParam(value = "city", required = false) String city,
                            @RequestParam(value = "pincode", required = false) String pincode,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("needs", communityNeedService.getOpenNeeds(category, priority, city, pincode, keyword));
        model.addAttribute("categories", NeedCategory.values());
        model.addAttribute("priorities", NeedPriority.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("city", city);
        model.addAttribute("pincode", pincode);
        model.addAttribute("keyword", keyword);
        return "needs/list";
    }

    @GetMapping("/details/{id}")
    public String needDetails(@PathVariable("id") Long id, Model model) {
        CommunityNeed need = communityNeedService.getNeedById(id);
        model.addAttribute("need", need);
        model.addAttribute("currentUser", userService.getCurrentUser());
        return "needs/details";
    }

    @GetMapping("/create")
    public String createNeedPage(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getRole() != Role.ROLE_RECIPIENT_ORG) {
            return "redirect:/error/403";
        }

        if (!model.containsAttribute("needDto")) {
            CommunityNeedDto dto = new CommunityNeedDto();
            dto.setCity(currentUser.getCity());
            dto.setPincode(currentUser.getPincode());
            model.addAttribute("needDto", dto);
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", NeedCategory.values());
        model.addAttribute("priorities", NeedPriority.values());
        return "needs/create";
    }

    @PostMapping("/create")
    public String createNeed(@Valid @ModelAttribute("needDto") CommunityNeedDto needDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", NeedCategory.values());
            model.addAttribute("priorities", NeedPriority.values());
            return "needs/create";
        }

        try {
            CommunityNeed saved = communityNeedService.createNeed(currentUser, needDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Community need '" + saved.getTitle() + "' published successfully!");
            return "redirect:/needs/details/" + saved.getId();
        } catch (BadRequestException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", NeedCategory.values());
            model.addAttribute("priorities", NeedPriority.values());
            return "needs/create";
        }
    }

    @PostMapping("/contribute/{id}")
    public String contributeToNeed(@PathVariable("id") Long id,
                                   @RequestParam("quantity") Double quantity,
                                   RedirectAttributes redirectAttributes) {
        try {
            communityNeedService.contributeToNeed(id, quantity);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thank you for pledging " + quantity + " units towards fulfilling this community need!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/needs/details/" + id;
    }
}
