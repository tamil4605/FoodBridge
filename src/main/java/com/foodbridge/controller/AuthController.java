package com.foodbridge.controller;

import com.foodbridge.dto.PasswordChangeDto;
import com.foodbridge.dto.UserProfileDto;
import com.foodbridge.dto.UserRegisterDto;
import com.foodbridge.entity.User;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        if (userService.getCurrentUser() != null) {
            return "redirect:/dashboard";
        }
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email address or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Registration successful! You can now log in with your credentials.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (userService.getCurrentUser() != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new UserRegisterDto());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") UserRegisterDto registerDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(registerDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please log in to access your FoodBridge dashboard.");
            return "redirect:/auth/login?registered=true";
        } catch (BadRequestException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setName(currentUser.getName());
        profileDto.setPhone(currentUser.getPhone());
        profileDto.setAddress(currentUser.getAddress());
        profileDto.setCity(currentUser.getCity());
        profileDto.setState(currentUser.getState());
        profileDto.setPincode(currentUser.getPincode());
        profileDto.setOrganizationName(currentUser.getOrganizationName());
        profileDto.setOrganizationType(currentUser.getOrganizationType());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileDto", profileDto);
        model.addAttribute("passwordDto", new PasswordChangeDto());
        return "auth/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("profileDto") UserProfileDto profileDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("passwordDto", new PasswordChangeDto());
            return "auth/profile";
        }

        try {
            userService.updateProfile(currentUser, profileDto);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
            return "redirect:/auth/profile";
        } catch (Exception ex) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("passwordDto", new PasswordChangeDto());
            model.addAttribute("errorMessage", "Error updating profile: " + ex.getMessage());
            return "auth/profile";
        }
    }

    @PostMapping("/profile/password")
    public String changePassword(@Valid @ModelAttribute("passwordDto") PasswordChangeDto passwordDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            UserProfileDto profileDto = new UserProfileDto();
            profileDto.setName(currentUser.getName());
            profileDto.setPhone(currentUser.getPhone());
            profileDto.setAddress(currentUser.getAddress());
            profileDto.setCity(currentUser.getCity());
            profileDto.setState(currentUser.getState());
            profileDto.setPincode(currentUser.getPincode());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("profileDto", profileDto);
            return "auth/profile";
        }

        try {
            userService.changePassword(currentUser, passwordDto);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
            return "redirect:/auth/profile";
        } catch (BadRequestException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/auth/profile";
        }
    }
}
