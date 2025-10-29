package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.PasswordChangeRequest;
import com.recicar.marketplace.dto.ProfileUpdateRequest;
import com.recicar.marketplace.dto.StoreInfoUpdateRequest;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.VendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my-profile")
public class MyProfileController {

    private final VendorService vendorService;

    public MyProfileController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public String getMyProfile(Model model) {
        // TODO: Get vendor from authenticated user
        Long vendorId = 1L; // Placeholder - replace with actual authenticated vendor ID
        
        Vendor vendor = vendorService.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        User user = vendor.getUser();
        
        model.addAttribute("vendor", vendor);
        model.addAttribute("user", user);
        
        // TODO: Get address from user's addresses
        String address = ""; // Placeholder
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            address = user.getAddresses().get(0).getFullAddress();
        }
        model.addAttribute("address", address);
        
        return "my-profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute ProfileUpdateRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            // TODO: Get vendor from authenticated user
            Long vendorId = 1L; // Placeholder
            
            vendorService.updateProfile(vendorId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/my-profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute PasswordChangeRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            // TODO: Get vendor from authenticated user
            Long vendorId = 1L; // Placeholder
            
            vendorService.changePassword(vendorId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
        }
        
        return "redirect:/my-profile";
    }

    @PostMapping("/update-store")
    public String updateStoreInfo(@ModelAttribute StoreInfoUpdateRequest request,
                                  @RequestParam(value = "logo", required = false) MultipartFile logo,
                                  @RequestParam(value = "banner", required = false) MultipartFile banner,
                                  RedirectAttributes redirectAttributes) {
        try {
            // TODO: Get vendor from authenticated user
            Long vendorId = 1L; // Placeholder
            
            vendorService.updateStoreInfo(vendorId, request, logo, banner);
            redirectAttributes.addFlashAttribute("successMessage", "Store information updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update store info: " + e.getMessage());
        }
        
        return "redirect:/my-profile";
    }
}
