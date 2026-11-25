package com.ua.rsz.repository;

import com.ua.rsz.domain.Book;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Keeps books in memory, without a database.
 * Used only with the "inmemory" profile: --spring.profiles.active=inmemory.
 * Normally the app uses JpaBookRepositoryAdapter (PostgreSQL).
 */
@Repository
@Profile("inmemory")
public class InMemoryBookRepository implements BookRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Book> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Book::getId))
                .toList();
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return findAll().stream()
                .filter(book -> book.getGenre() != null && book.getGenre().equalsIgnoreCase(genre))
                .toList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(sequence.incrementAndGet());
        }
        storage.put(book.getId(), book);
        return book;
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}
