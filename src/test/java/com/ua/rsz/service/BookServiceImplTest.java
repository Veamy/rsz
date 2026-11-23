package com.ua.rsz.service;

import com.ua.rsz.domain.Book;
import com.ua.rsz.exception.BookNotFoundException;
import com.ua.rsz.repository.BookRepository;
import com.ua.rsz.service.mapper.BookMapper;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import com.ua.rsz.web.dto.BookRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test: only the service is real. The repository is a Mockito mock,
 * so no Spring and no storage are started. The mapper is simple, so we use the real one.
 */
class BookServiceImplTest {

    private final BookRepository repository = mock(BookRepository.class);
    private final BookMapper mapper = new BookMapper();
    private final BookService service = new BookServiceImpl(repository, mapper);

    private Book sampleBook() {
        return new Book(1L, "Кобзар", "Тарас Шевченко", new BigDecimal("420.00"),
                1840, "Поезія", 704, "/images/covers/kobzar.svg", "Опис");
    }

    private BookRequest sampleRequest() {
        return new BookRequest("Місто", "Валер'ян Підмогильний", new BigDecimal("310.00"),
                1928, "Роман", 320, "/images/covers/misto.svg", "Опис");
    }

    @Test
    void findAll_withoutGenre_returnsAllBooksAsCards() {
        when(repository.findAll()).thenReturn(List.of(sampleBook()));

        List<BookCardDto> cards = service.findAll(null);

        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).title()).isEqualTo("Кобзар");
        verify(repository, never()).findByGenre(any());
    }

    @Test
    void findAll_withBlankGenre_returnsAllBooks() {
        when(repository.findAll()).thenReturn(List.of(sampleBook()));

        assertThat(service.findAll("  ")).hasSize(1);
        verify(repository, never()).findByGenre(any());
    }

    @Test
    void findAll_withGenre_usesGenreFilter() {
        when(repository.findByGenre("Поезія")).thenReturn(List.of(sampleBook()));

        List<BookCardDto> cards = service.findAll("Поезія");

        assertThat(cards).extracting(BookCardDto::title).containsExactly("Кобзар");
        verify(repository).findByGenre("Поезія");
    }

    @Test
    void findById_existing_returnsDetails() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleBook()));

        BookDetailDto detail = service.findById(1L);

        assertThat(detail.id()).isEqualTo(1L);
        assertThat(detail.author()).isEqualTo("Тарас Шевченко");
        assertThat(detail.pages()).isEqualTo(704);
    }

    @Test
    void findById_missing_throwsNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_savesBookWithoutId_andReturnsAssignedId() {
        when(repository.save(any(Book.class))).thenAnswer(inv -> {
            Book book = inv.getArgument(0);
            book.setId(6L);                 // the repository picks the id
            return book;
        });

        BookDetailDto created = service.create(sampleRequest());

        ArgumentCaptor<Book> saved = ArgumentCaptor.forClass(Book.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getTitle()).isEqualTo("Місто");
        assertThat(created.id()).isEqualTo(6L);
    }

    @Test
    void update_existing_keepsIdAndChangesFields() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleBook()));
        when(repository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookDetailDto updated = service.update(1L, sampleRequest());

        assertThat(updated.id()).isEqualTo(1L);
        assertThat(updated.title()).isEqualTo("Місто");
    }

    @Test
    void update_missing_throwsNotFound_andSavesNothing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, sampleRequest()))
                .isInstanceOf(BookNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void delete_existing_removesBook() {
        when(repository.deleteById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_missing_throwsNotFound() {
        when(repository.deleteById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(BookNotFoundException.class);
    }
}
