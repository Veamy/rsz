package com.ua.rsz.config;

import com.ua.rsz.domain.Book;
import com.ua.rsz.repository.BookRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adds starter books when the app starts, but only if there are no books yet.
 * The data lives in PostgreSQL now, so without this check every restart would add them again.
 * Kept apart from the repository: the repository stores books, the seeder fills it.
 */
@Component
public class BookSeeder implements ApplicationRunner {

    private final BookRepository repository;

    public BookSeeder(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!repository.findAll().isEmpty()) {
            return;
        }
        repository.save(new Book(null, "Кобзар", "Тарас Шевченко",
                new BigDecimal("420.00"), 1840, "Поезія", 704,
                "/images/covers/kobzar.svg",
                "Головна поетична збірка Шевченка — вірші та поеми, що стали основою української літератури."));

        repository.save(new Book(null, "Тіні забутих предків", "Михайло Коцюбинський",
                new BigDecimal("235.50"), 1911, "Повість", 128,
                "/images/covers/tini.svg",
                "Історія кохання Івана та Марічки на тлі гуцульських Карпат, їхніх звичаїв і вірувань."));

        repository.save(new Book(null, "Лісова пісня", "Леся Українка",
                new BigDecimal("199.00"), 1911, "Драма-феєрія", 160,
                "/images/covers/lisova.svg",
                "Драма про мавку Лісову й Лукаша — зіткнення світу природи і світу людей."));

        repository.save(new Book(null, "Місто", "Валер'ян Підмогильний",
                new BigDecimal("310.00"), 1928, "Роман", 320,
                "/images/covers/misto.svg",
                "Перший український урбаністичний роман: сільський юнак Степан Радченко підкорює Київ."));

        repository.save(new Book(null, "Ворошиловград", "Сергій Жадан",
                new BigDecimal("385.00"), 2010, "Роман", 448,
                "/images/covers/voroshylovhrad.svg",
                "Герман повертається на Схід, щоб розібратися з бензоколонкою брата, і залишається її захищати."));
    }
}
