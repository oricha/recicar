package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.MarketRegionDto;
import com.recicar.marketplace.web.ux.ClientPreferencesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
public class RegionsApiController {

    private final ClientPreferencesService clientPreferencesService;

    public RegionsApiController(ClientPreferencesService clientPreferencesService) {
        this.clientPreferencesService = clientPreferencesService;
    }

    @GetMapping
    public List<MarketRegionDto> listRegions() {
        return clientPreferencesService.getProperties().getRegions().stream()
                .map(region -> new MarketRegionDto(
                        region.getCode() == null ? "" : region.getCode().toUpperCase(),
                        region.getName() == null ? "" : region.getName()
                ))
                .toList();
    }
}
