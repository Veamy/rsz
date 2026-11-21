package com.ua.rsz.web.dto;

import java.math.BigDecimal;

/** Short book info for the list (a card). */
public record BookCardDto(
        Long id,
        String title,
        String author,
        BigDecimal price,
        String coverUrl
) {
}
