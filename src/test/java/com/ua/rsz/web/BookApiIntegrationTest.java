package com.ua.rsz.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.rsz.TestcontainersConfig;
import com.ua.rsz.repository.jpa.BookJpaRepository;
import com.ua.rsz.web.dto.BookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end test through all layers: HTTP → controller → service → JPA → real PostgreSQL.
 * The database runs in Docker (Testcontainers, see TestcontainersConfig).
 * Each test starts with an empty table and creates the books it needs through the API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class BookApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookJpaRepository jpa;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void clearTable() {
        jpa.deleteAll();     // remove the seeded books, so tests do not depend on them
    }

    private BookRequest request(String title, String genre, String price) {
        return new BookRequest(title, "Автор", new BigDecimal(price), 2000, genre, 100, null, "Опис");
    }

    /** Creates a book through POST and returns its id from the database. */
    private long givenBook(String title, String genre) throws Exception {
        String body = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request(title, genre, "100.00"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void create_writesRowToPostgres() throws Exception {
        long id = givenBook("Кобзар", "Поезія");

        // check the row with plain SQL, past JPA
        String title = jdbc.queryForObject("SELECT title FROM books WHERE id = ?", String.class, id);
        assertThat(title).isEqualTo("Кобзар");
    }

    @Test
    void list_returnsExactlyTheBooksTheTestCreated() throws Exception {
        givenBook("A", "Роман");
        givenBook("B", "Поезія");

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("A"))
                .andExpect(jsonPath("$[1].title").value("B"));
    }

    @Test
    void list_whenEmpty_returnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void filterByGenre_ignoresCase_inRealDb() throws Exception {
        givenBook("Місто", "Роман");
        givenBook("Кобзар", "Поезія");
        givenBook("Ворошиловград", "Роман");

        mockMvc.perform(get("/api/books").param("genre", "РОМАН"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Місто"))
                .andExpect(jsonPath("$[1].title").value("Ворошиловград"));
    }

    @Test
    void fullCycle_create_read_update_delete() throws Exception {
        long id = givenBook("Інтернат", "Роман");

        mockMvc.perform(get("/api/books/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Інтернат"))
                .andExpect(jsonPath("$.price").value(100.00));

        mockMvc.perform(put("/api/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("Інтернат", "Роман", "299.00"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.price").value(299.00));

        mockMvc.perform(delete("/api/books/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/" + id))
                .andExpect(status().isNotFound());
        assertThat(jpa.existsById(id)).isFalse();
    }

    @Test
    void missingId_returns404WithErrorBody_forGetPutDelete() throws Exception {
        mockMvc.perform(get("/api/books/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));

        mockMvc.perform(put("/api/books/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("X", "Роман", "1.00"))))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/books/9999"))
                .andExpect(status().isNotFound());
        assertThat(jpa.count()).isZero();     // the failed PUT did not insert anything
    }

    @Test
    void create_invalid_returns400_andSavesNothing() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","author":"","price":-1,"publishedYear":0,"genre":"","pages":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
        assertThat(jpa.count()).isZero();
    }
}
