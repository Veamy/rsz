package com.ua.rsz.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A book in the store.
 * Layer: domain. It is a JPA entity: Hibernate maps it to the "books" table.
 * It still does not know about the web layer.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor      // JPA needs a constructor without arguments
@AllArgsConstructor
public class Book {

    /** Primary key. IDENTITY: PostgreSQL gives the next number on insert. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    /** Money: exact decimal, up to 10 digits with 2 after the point. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Column published_year. The name "year" is avoided: it is a reserved word in SQL. */
    @Column(name = "published_year", nullable = false)
    private int publishedYear;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private int pages;

    /** Cover image, shown in the list and on the book page. */
    @Column(name = "cover_url")
    private String coverUrl;

    @Column(length = 2000)
    private String description;
}
