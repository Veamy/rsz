package com.ua.rsz.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Data the client sends to create or update a book.
 * There is no id here: the server picks the id.
 * The rules below are checked by @Valid in the controller; a broken rule gives 400.
 */
public record BookRequest(
        @NotBlank(message = "назва обов'язкова")
        String title,

        @NotBlank(message = "автор обов'язковий")
        String author,

        @NotNull(message = "ціна обов'язкова")
        @Positive(message = "ціна має бути додатною")
        BigDecimal price,

        @Positive(message = "рік видання має бути додатним")
        int publishedYear,

        @NotBlank(message = "жанр обов'язковий")
        String genre,

        @Positive(message = "кількість сторінок має бути додатною")
        int pages,

        String coverUrl,
        String description
) {
}
