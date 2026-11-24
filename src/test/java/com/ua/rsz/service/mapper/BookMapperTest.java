package com.ua.rsz.service.mapper;

import com.ua.rsz.domain.Book;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import com.ua.rsz.web.dto.BookRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Plain unit test: the mapper has no dependencies, so we just create it. */
class BookMapperTest {

    private final BookMapper mapper = new BookMapper();

    private Book sampleBook() {
        return new Book(3L, "Лісова пісня", "Леся Українка", new BigDecimal("199.00"),
                1911, "Драма-феєрія", 160, "/images/covers/lisova.svg", "Опис");
    }

    @Test
    void toCard_copiesOnlyShortFields() {
        BookCardDto card = mapper.toCard(sampleBook());

        assertThat(card).isEqualTo(new BookCardDto(3L, "Лісова пісня", "Леся Українка",
                new BigDecimal("199.00"), "/images/covers/lisova.svg"));
    }

    @Test
    void toDetail_copiesAllFields() {
        BookDetailDto detail = mapper.toDetail(sampleBook());

        assertThat(detail).isEqualTo(new BookDetailDto(3L, "Лісова пісня", "Леся Українка",
                new BigDecimal("199.00"), 1911, "Драма-феєрія", 160,
                "/images/covers/lisova.svg", "Опис"));
    }

    @Test
    void toDomain_buildsBookWithoutId() {
        BookRequest request = new BookRequest("Місто", "Валер'ян Підмогильний",
                new BigDecimal("310.00"), 1928, "Роман", 320, null, "Опис");

        Book book = mapper.toDomain(request);

        assertThat(book.getId()).isNull();
        assertThat(book.getTitle()).isEqualTo("Місто");
        assertThat(book.getPublishedYear()).isEqualTo(1928);
        assertThat(book.getCoverUrl()).isNull();
    }
}
