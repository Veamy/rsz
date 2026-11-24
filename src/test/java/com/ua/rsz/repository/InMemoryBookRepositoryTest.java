package com.ua.rsz.repository;

import com.ua.rsz.domain.Book;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Unit test of the in-memory storage: ids, genre filter, update and delete. */
class InMemoryBookRepositoryTest {

    private final InMemoryBookRepository repository = new InMemoryBookRepository();

    private Book book(String title, String genre) {
        return new Book(null, title, "Автор", new BigDecimal("100.00"), 2000, genre, 100, null, null);
    }

    @Test
    void save_newBooks_getIncreasingIds_andFindAllIsSortedById() {
        Book first = repository.save(book("A", "Роман"));
        Book second = repository.save(book("B", "Поезія"));

        assertThat(first.getId()).isEqualTo(1L);
        assertThat(second.getId()).isEqualTo(2L);
        assertThat(repository.findAll()).extracting(Book::getTitle).containsExactly("A", "B");
    }

    @Test
    void save_existingId_replacesBook() {
        Book saved = repository.save(book("Old", "Роман"));
        Book changed = book("New", "Роман");
        changed.setId(saved.getId());

        repository.save(changed);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findById(saved.getId())).get()
                .extracting(Book::getTitle).isEqualTo("New");
    }

    @Test
    void findByGenre_ignoresCase() {
        repository.save(book("A", "Роман"));
        repository.save(book("B", "Поезія"));
        repository.save(book("C", "роман"));

        assertThat(repository.findByGenre("РОМАН")).extracting(Book::getTitle)
                .containsExactly("A", "C");
    }

    @Test
    void deleteById_returnsTrueOnlyWhenBookExisted() {
        Book saved = repository.save(book("A", "Роман"));

        assertThat(repository.deleteById(saved.getId())).isTrue();
        assertThat(repository.deleteById(saved.getId())).isFalse();
        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
