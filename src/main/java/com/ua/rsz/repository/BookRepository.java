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

    /** Books of one genre, case does not matter. */
    List<Book> findByGenre(String genre);

    Optional<Book> findById(Long id);

    /** Creates a new book (when id is null) or replaces an existing one. */
    Book save(Book book);

    /** @return true if the book existed and was removed */
    boolean deleteById(Long id);
}
