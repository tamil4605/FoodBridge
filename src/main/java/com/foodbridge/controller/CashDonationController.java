package com.foodbridge.controller;

import com.foodbridge.dto.CashCampaignDto;
import com.foodbridge.dto.CashContributionDto;
import com.foodbridge.entity.CashCampaign;
import com.foodbridge.entity.CashTransaction;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.CauseCategory;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.CashCampaignService;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cash")
public class CashDonationController {

    private final CashCampaignService cashCampaignService;
    private final UserService userService;

    public CashDonationController(CashCampaignService cashCampaignService, UserService userService) {
        this.cashCampaignService = cashCampaignService;
        this.userService = userService;
    }

    @GetMapping
    public String listCampaigns(@RequestParam(value = "category", required = false) CauseCategory category,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                Model model) {
        model.addAttribute("currentUser", userService.getCurrentUser());
        model.addAttribute("campaigns", cashCampaignService.getApprovedActiveCampaigns(category, keyword));
        model.addAttribute("categories", CauseCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);
        return "cash/campaigns";
    }

    @GetMapping("/details/{id}")
    public String campaignDetails(@PathVariable("id") Long id, Model model) {
        CashCampaign campaign = cashCampaignService.getCampaignById(id);
        User currentUser = userService.getCurrentUser();

        model.addAttribute("campaign", campaign);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("transactions", cashCampaignService.getTransactionsForCampaign(campaign));

        CashContributionDto contributionDto = new CashContributionDto();
        contributionDto.setCampaignId(campaign.getId());
        if (currentUser != null) {
            contributionDto.setDonorName(currentUser.getName());
            contributionDto.setDonorEmail(currentUser.getEmail());
        }
        model.addAttribute("contributionDto", contributionDto);

        return "cash/details";
    }

    @GetMapping("/create")
    public String createCampaignPage(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getRole() != Role.ROLE_RECIPIENT_ORG) {
            return "redirect:/error/403";
        }

        if (!model.containsAttribute("campaignDto")) {
            model.addAttribute("campaignDto", new CashCampaignDto());
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("categories", CauseCategory.values());
        return "cash/create";
    }

    @PostMapping("/create")
    public String createCampaign(@Valid @ModelAttribute("campaignDto") CashCampaignDto campaignDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", CauseCategory.values());
            return "cash/create";
        }

        try {
            CashCampaign saved = cashCampaignService.createCampaign(currentUser, campaignDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Campaign '" + saved.getTitle() + "' submitted successfully! It is pending administrator verification before appearing publicly.");
            return "redirect:/dashboard/organization";
        } catch (BadRequestException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("categories", CauseCategory.values());
            return "cash/create";
        }
    }

    @PostMapping("/donate")
    public String processDonation(@Valid @ModelAttribute("contributionDto") CashContributionDto contributionDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide all required donor details and a valid contribution amount.");
            return "redirect:/cash/details/" + contributionDto.getCampaignId();
        }

        try {
            CashTransaction txn = cashCampaignService.processDemoContribution(contributionDto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Simulated donation of ₹" + txn.getAmount() + " processed successfully! Reference ID: " + txn.getTransactionReference());
            return "redirect:/cash/receipt/" + txn.getId();
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/cash/details/" + contributionDto.getCampaignId();
        }
    }

    @GetMapping("/receipt/{txnId}")
    public String viewReceipt(@PathVariable("txnId") Long txnId, Model model) {
        CashTransaction txn = cashCampaignService.getTransactionById(txnId);
        model.addAttribute("transaction", txn);
        model.addAttribute("currentUser", userService.getCurrentUser());
        return "cash/receipt";
    }
}
