package com.recicar.marketplace.controller;

import com.recicar.marketplace.client.VehicleApiClient;
import com.recicar.marketplace.dto.VehicleInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequiredArgsConstructor
public class VrnSearchController {

    private final VehicleApiClient vehicleApiClient;

    @GetMapping("/vrn")
    public String handleVrnSearch(@RequestParam("reg") String reg,
                                  @RequestParam(value = "partType", required = false) String partType,
                                  RedirectAttributes redirectAttributes) {
        if (reg == null || reg.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter a valid registration number.");
            return "redirect:/";
        }

        VehicleInfo info = vehicleApiClient.lookupLicensePlate(reg.trim().toUpperCase());
        if (info == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't find that registration. Please check and try again.");
            return "redirect:/";
        }

        // Engine unknown from VRN mock; pass placeholder to satisfy validation
        String engine = "unknown";

        String redirectUrl = UriComponentsBuilder
                .fromPath("/products/vehicle")
                .queryParam("make", info.getMake())
                .queryParam("model", info.getModel())
                .queryParam("year", info.getYear())
                .queryParam("engine", engine)
                .build()
                .toUriString();

        return "redirect:" + redirectUrl;
    }
}

