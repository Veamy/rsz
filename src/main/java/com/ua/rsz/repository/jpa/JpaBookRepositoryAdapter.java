package com.ua.rsz.repository.jpa;

import com.ua.rsz.domain.Book;
import com.ua.rsz.repository.BookRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Our {@link BookRepository} interface, stored in PostgreSQL.
 * It only passes calls to {@link BookJpaRepository}.
 * The service still sees the same interface, so the service and the controller
 * did not change when we moved from memory to the database (DIP).
 * @Primary: if another BookRepository is present, Spring picks this one.
 */
@Repository
@Primary
public class JpaBookRepositoryAdapter implements BookRepository {

    private final BookJpaRepository jpa;

    public JpaBookRepositoryAdapter(BookJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Book> findAll() {
        return jpa.findAll(Sort.by("id"));
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return jpa.findByGenreIgnoreCaseOrderById(genre);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Book save(Book book) {
        return jpa.save(book);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }
}
