package com.ua.rsz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** No book with this id → HTTP 404. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Книгу з id=" + id + " не знайдено");
    }
}
