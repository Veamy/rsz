package com.ua.rsz.service;

import com.ua.rsz.domain.Book;
import com.ua.rsz.exception.BookNotFoundException;
import com.ua.rsz.repository.BookRepository;
import com.ua.rsz.service.mapper.BookMapper;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import com.ua.rsz.web.dto.BookRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/** Implements {@link BookService}. Spring passes the dependencies into the constructor. */
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;
    private final BookMapper mapper;

    public BookServiceImpl(BookRepository repository, BookMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<BookCardDto> findAll(String genre) {
        List<Book> books = (genre == null || genre.isBlank())
                ? repository.findAll()
                : repository.findByGenre(genre);
        return books.stream()
                .map(mapper::toCard)
                .toList();
    }

    @Override
    public BookDetailDto findById(Long id) {
        return mapper.toDetail(getExisting(id));
    }

    @Override
    public BookDetailDto create(BookRequest request) {
        Book book = mapper.toDomain(request);   // id is null, the repository will set it
        return mapper.toDetail(repository.save(book));
    }

    @Override
    public BookDetailDto update(Long id, BookRequest request) {
        getExisting(id);                        // 404 if the book does not exist
        Book book = mapper.toDomain(request);
        book.setId(id);                         // keep the same id
        return mapper.toDetail(repository.save(book));
    }

    @Override
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new BookNotFoundException(id);
        }
    }

    private Book getExisting(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }
}
