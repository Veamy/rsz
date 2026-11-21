package com.ua.rsz.service.mapper;

import com.ua.rsz.domain.Book;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import org.springframework.stereotype.Component;

/** Turns a {@link Book} into the DTOs we send to the client. */
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
}
