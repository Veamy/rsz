package com.ua.rsz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/** Checks that the whole app starts, with a real PostgreSQL from Testcontainers. */
@SpringBootTest
@Import(TestcontainersConfig.class)
class RszApplicationTests {

    @Test
    void contextLoads() {
    }

}
