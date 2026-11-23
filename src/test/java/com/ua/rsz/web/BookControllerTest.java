package com.ua.rsz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.rsz.exception.BookNotFoundException;
import com.ua.rsz.service.BookService;
import com.ua.rsz.web.dto.BookCardDto;
import com.ua.rsz.web.dto.BookDetailDto;
import com.ua.rsz.web.dto.BookRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test of the web layer: @WebMvcTest starts only the controller,
 * JSON handling, validation and GlobalExceptionHandler. The service is a mock.
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private BookDetailDto sampleDetail() {
        return new BookDetailDto(1L, "Кобзар", "Тарас Шевченко", new BigDecimal("420.00"),
                1840, "Поезія", 704, "/images/covers/kobzar.svg", "Опис");
    }

    private BookRequest validRequest() {
        return new BookRequest("Кобзар", "Тарас Шевченко", new BigDecimal("420.00"),
                1840, "Поезія", 704, "/images/covers/kobzar.svg", "Опис");
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    // ---------- GET ----------

    @Test
    void list_returns200WithCards() throws Exception {
        when(bookService.findAll(null)).thenReturn(List.of(
                new BookCardDto(1L, "Кобзар", "Тарас Шевченко", new BigDecimal("420.00"), "/c.svg")));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Кобзар"))
                .andExpect(jsonPath("$[0].author").value("Тарас Шевченко"));
    }

    @Test
    void list_withGenre_passesFilterToService() throws Exception {
        when(bookService.findAll("Поезія")).thenReturn(List.of());

        mockMvc.perform(get("/api/books").param("genre", "Поезія"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(bookService).findAll("Поезія");
    }

    @Test
    void getOne_returns200WithDetails() throws Exception {
        when(bookService.findById(1L)).thenReturn(sampleDetail());

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.genre").value("Поезія"))
                .andExpect(jsonPath("$.pages").value(704));
    }

    @Test
    void getOne_missing_returns404WithErrorBody() throws Exception {
        when(bookService.findById(999L)).thenThrow(new BookNotFoundException(999L));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Книгу з id=999 не знайдено"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    void getOne_notANumber_returns400WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/books/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
        verifyNoInteractions(bookService);
    }

    // ---------- POST ----------

    @Test
    void create_valid_returns201() throws Exception {
        when(bookService.create(any(BookRequest.class))).thenReturn(sampleDetail());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Кобзар"));
    }

    @Test
    void create_invalid_returns400WithFieldDetails() throws Exception {
        String invalid = """
                {"title":"","author":"","price":-5,"publishedYear":0,"genre":"","pages":0}
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Помилка валідації вхідних даних"))
                .andExpect(jsonPath("$.details.length()").value(6))
                .andExpect(jsonPath("$.details", hasItem("price: ціна має бути додатною")))
                .andExpect(jsonPath("$.details", hasItem("title: назва обов'язкова")));
        verifyNoInteractions(bookService);
    }

    @Test
    void create_brokenJson_returns400WithErrorBody() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Некоректне тіло запиту (очікується JSON)"));
        verifyNoInteractions(bookService);
    }

    // ---------- PUT ----------

    @Test
    void update_existing_returns200() throws Exception {
        when(bookService.update(eq(1L), any(BookRequest.class))).thenReturn(sampleDetail());

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_missing_returns404() throws Exception {
        when(bookService.update(eq(999L), any(BookRequest.class)))
                .thenThrow(new BookNotFoundException(999L));

        mockMvc.perform(put("/api/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ---------- DELETE ----------

    @Test
    void delete_existing_returns204WithEmptyBody() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(bookService).delete(1L);
    }

    @Test
    void delete_missing_returns404() throws Exception {
        doThrow(new BookNotFoundException(999L)).when(bookService).delete(999L);

        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
