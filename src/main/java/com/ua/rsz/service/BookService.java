package com.ua.rsz.service;

import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;

import java.util.List;

/** Business logic for books. The controller uses this interface, not the class. */
public interface BookService {

    List<BookCardDto> findAll();

    /** @throws com.ua.rsz.exception.BookNotFoundException if the book does not exist */
    BookDetailDto findById(Long id);
}
