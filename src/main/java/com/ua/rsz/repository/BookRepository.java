package com.ua.rsz.repository;

import com.ua.rsz.domain.Book;

import java.util.List;
import java.util.Optional;

/**
 * Where books are stored.
 * Layer: repository. The service uses this interface, not a concrete class.
 */
public interface BookRepository {

    List<Book> findAll();

    Optional<Book> findById(Long id);

    Book save(Book book);
}
