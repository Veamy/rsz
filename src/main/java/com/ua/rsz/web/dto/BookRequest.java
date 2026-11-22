package com.ua.rsz.web.dto;

import java.math.BigDecimal;

/**
 * Data the client sends to create or update a book.
 * There is no id here: the server picks the id.
 */
public record BookRequest(
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
