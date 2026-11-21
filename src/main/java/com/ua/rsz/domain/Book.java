package com.ua.rsz.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A book in the store.
 * Layer: domain. No Spring here: it does not know about the web or how data is stored.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private int publishedYear;
    private String genre;
    private int pages;
    /** Cover image, shown in the list and on the book page. */
    private String coverUrl;
    private String description;
}
