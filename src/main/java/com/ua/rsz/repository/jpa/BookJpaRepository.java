package com.ua.rsz.repository.jpa;

import com.ua.rsz.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository. Spring writes the code for us at startup:
 * save, findById, findAll, deleteById come from JpaRepository,
 * and findByGenreIgnoreCaseOrderById is built from the method name.
 */
public interface BookJpaRepository extends JpaRepository<Book, Long> {

    List<Book> findByGenreIgnoreCaseOrderById(String genre);
}
