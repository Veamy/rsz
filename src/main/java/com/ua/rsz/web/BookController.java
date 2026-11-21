package com.ua.rsz.web;

import com.ua.rsz.service.BookService;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for books. It only handles HTTP: take the request, return a DTO.
 *   GET /api/books       → list of book cards
 *   GET /api/books/{id}  → one book with details (404 if not found)
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookCardDto> list() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDetailDto getOne(@PathVariable Long id) {
        return bookService.findById(id);
    }
}
