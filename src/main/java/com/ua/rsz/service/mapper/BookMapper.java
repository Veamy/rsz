package com.ua.rsz.service.mapper;

import com.ua.rsz.domain.Book;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import com.ua.rsz.web.dto.BookRequest;
import org.springframework.stereotype.Component;

/** Turns a {@link Book} into DTOs for the client, and a request back into a {@link Book}. */
@Component
public class BookMapper {

    public BookCardDto toCard(Book book) {
        return new BookCardDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getCoverUrl()
        );
    }

    public BookDetailDto toDetail(Book book) {
        return new BookDetailDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getPublishedYear(),
                book.getGenre(),
                book.getPages(),
                book.getCoverUrl(),
                book.getDescription()
        );
    }

    /** Builds a new book from the request. The id stays null: the service sets it. */
    public Book toDomain(BookRequest request) {
        return new Book(
                null,
                request.title(),
                request.author(),
                request.price(),
                request.publishedYear(),
                request.genre(),
                request.pages(),
                request.coverUrl(),
                request.description()
        );
    }
}
