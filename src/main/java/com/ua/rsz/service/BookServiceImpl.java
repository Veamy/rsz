package com.ua.rsz.service;

import com.ua.rsz.domain.Book;
import com.ua.rsz.exception.BookNotFoundException;
import com.ua.rsz.repository.BookRepository;
import com.ua.rsz.service.mapper.BookMapper;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
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
    public List<BookCardDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toCard)
                .toList();
    }

    @Override
    public BookDetailDto findById(Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return mapper.toDetail(book);
    }
}
