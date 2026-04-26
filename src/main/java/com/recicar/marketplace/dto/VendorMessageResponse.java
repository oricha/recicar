package com.recicar.marketplace.dto;

import java.time.LocalDateTime;

public record VendorMessageResponse(
        Long id,
        Long senderUserId,
        String body,
        LocalDateTime createdAt
) { }
