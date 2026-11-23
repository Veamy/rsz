package com.ua.rsz.web;

import com.ua.rsz.exception.BookNotFoundException;
import com.ua.rsz.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Turns exceptions from any controller into an {@link ErrorResponse}.
 * So all errors look the same, and controllers stay free of try/catch.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** No book with this id → 404. */
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(BookNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
    }

    /** @Valid failed → 400, with one line per broken field. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .sorted()
                .toList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Помилка валідації вхідних даних", details);
    }

    /** The body is not valid JSON → 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnreadable(HttpMessageNotReadableException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Некоректне тіло запиту (очікується JSON)");
    }

    /** A path or query value has the wrong type, e.g. /api/books/abc → 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Некоректне значення параметра '" + ex.getName() + "'");
    }
}
