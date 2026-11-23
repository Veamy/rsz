package com.ua.rsz.exception;

/** No book with this id. GlobalExceptionHandler turns it into HTTP 404. */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Книгу з id=" + id + " не знайдено");
    }
}
