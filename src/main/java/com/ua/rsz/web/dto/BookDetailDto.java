package com.ua.rsz.web.dto;

import java.math.BigDecimal;

/** Full book info for the book page. */
public record BookDetailDto(
        Long id,
        String title,
        String author,
        BigDecimal price,
        int publishedYear,
        String genre,
        int pages,
        String coverUrl,
        String description
) {
}
