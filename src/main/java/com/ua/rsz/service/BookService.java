package com.ua.rsz.service;

import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import com.ua.rsz.web.dto.BookRequest;

import java.util.List;

/**
 * Business logic for books. The controller uses this interface, not the class.
 * Methods that take an id throw {@link com.ua.rsz.exception.BookNotFoundException}
 * if there is no book with that id.
 */
public interface BookService {

    /** @param genre optional filter; null or blank means all books */
    List<BookCardDto> findAll(String genre);

    BookDetailDto findById(Long id);

    BookDetailDto create(BookRequest request);

    BookDetailDto update(Long id, BookRequest request);

    void delete(Long id);
}
