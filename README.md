# rsz

Навчальний інтернет-магазин (книгарня) на **Spring Boot 3.4 · Java 21 · Maven · PostgreSQL 16**.
API: `/api/books`, приклади запитів у [requests.md](requests.md).

## Локальний запуск

Застосунку потрібен PostgreSQL. Найпростіше підняти лише БД у Docker, а застосунок запускати з IDE:

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

Без БД, зі сховищем у пам'яті: `./mvnw spring-boot:run -Dspring-boot.run.profiles=inmemory`.

Тестам Postgres не потрібен: інтеграційні тести самі піднімають його через Testcontainers
(потрібен лише запущений Docker).

```bash
./mvnw test
```

## Docker

Усе описано в [docker-compose.yml](docker-compose.yml).

| Команда | Що робить | Порти |
|---|---|---|
| `docker compose up --build` | PostgreSQL + dev: код з хоста, devtools-перезапуск, дебаг | 5432, 8080, 5005 |
| `docker compose --profile prod up --build app-prod` | зібраний jar у JRE-образі | 8081 |
| `docker compose down` | зупинити; дані лишаються в томі `pgdata` | |
| `docker compose down -v` | зупинити й **видалити** дані | |

Подивитись таблицю напряму:

```bash
docker compose exec postgres psql -U bookstore -d bookstore -c "SELECT * FROM books;"
```

Перевірка: `curl localhost:8080/actuator/health` → `{"status":"UP"}`.

### Дебаг (IntelliJ IDEA)

`Run → Edit Configurations → + → Remote JVM Debug`, host `localhost`, port `5005` → Debug.

### Гарячий перезапуск

Після `Build → Build Project` (Ctrl+F9) IDE оновлює `target/classes`, devtools у контейнері
перезапускає застосунок автоматично. Якщо змінився `pom.xml` — `docker compose up --build`.

## Тести

```bash
./mvnw test
```

### Що покрито

**`BookServiceImplTest`** — модульний тест сервісу. Репозиторій підмінено `mock()`, Spring не стартує.

| Метод | Сценарії |
|---|---|
| `findAll` | без жанру → усі книги; порожній жанр → усі; з жанром → викликається `findByGenre` |
| `findById` | існуюча → DTO деталей; неіснуюча → `BookNotFoundException` |
| `create` | у репозиторій іде книга без id, повертається призначений id |
| `update` | існуюча → той самий id і нові поля; неіснуюча → виняток, `save` не викликається |
| `delete` | існуюча → видаляється; неіснуюча → виняток |

**`BookControllerTest`** — `@WebMvcTest` + `MockMvc` + `jsonPath`. Сервіс підмінено `@MockBean`.

| Запит | Сценарії |
|---|---|
| `GET /api/books` | `200` зі списком; параметр `genre` передається в сервіс |
| `GET /api/books/{id}` | `200` з деталями; `404` з тілом `ErrorResponse`; `/abc` → `400` |
| `POST /api/books` | валідний → `201`; невалідний → `400` з переліком полів у `details`; зламаний JSON → `400` |
| `PUT /api/books/{id}` | `200`; неіснуючий → `404` |
| `DELETE /api/books/{id}` | `204` з порожнім тілом; неіснуючий → `404` |

**`BookMapperTest`** — `toCard`, `toDetail` копіюють потрібні поля; `toDomain` створює книгу без id.

**`InMemoryBookRepositoryTest`** — id видаються по черзі, `findAll` сортує за id, `save` з існуючим id
замінює книгу, `findByGenre` ігнорує регістр, `deleteById` повертає `true` лише для існуючої книги.

### Матриця покриття (заняття 1.5)

| Сценарій | Тип тесту | Очікується | Покрито |
|---|---|---|---|
| `findAll` → список | unit + integration | `200`, масив | ✅ |
| `findById` існуючий | unit + integration | `200`, деталі | ✅ |
| `findById` неіснуючий | unit + integration | `404` + тіло помилки | ✅ |
| `create` | unit + integration | `201` | ✅ |
| `create` невалідний | integration | `400` + перелік проблем | ✅ |
| `update` / `delete` | unit + integration | `200` / `204`, `404` для неіснуючого | ✅ |
| Фільтр за жанром | unit + integration | лише книги жанру | ✅ |
| Зламаний JSON, `/api/books/abc` | integration | `400` + тіло помилки | ✅ |

**`BookApiIntegrationTest`** (заняття 2.1) — `@SpringBootTest` + Testcontainers: HTTP → контролер →
сервіс → JPA → справжній PostgreSQL `postgres:16-alpine`. Кожен тест очищає таблицю й сам створює
потрібні книги: запис реально з'являється в таблиці (перевірка через SQL), список і фільтр за
жанром, повний цикл create → read → update → delete, `404` для GET/PUT/DELETE, невалідний POST →
`400` і нічого не збережено.

### Що не покрито і чому

- **Міграції схеми**. Таблицю створює Hibernate (`ddl-auto: update`); міграції будуть на занятті 2.2.
- **`BookSeeder`**. Він лише кладе стартові дані; перевіряти константи не має сенсу.
- **Валідація `PUT`** окремо не перевіряється: там ті самі `@Valid` і `BookRequest`, що й у `POST`.
- **Одночасні запити** до in-memory сховища. `ConcurrentHashMap` і `AtomicLong` потокобезпечні,
  а сховище все одно буде замінене на БД.
- **Помилки 500** (непередбачені винятки). Окремого обробника немає, тож Spring віддає свій
  стандартний формат.

### Формат помилок

Усі помилки проходять через `GlobalExceptionHandler` і мають один формат:

```json
{"status":400,"error":"Bad Request","message":"Помилка валідації вхідних даних",
 "details":["price: ціна має бути додатною","title: назва обов'язкова"]}
```

## Заняття 2.1 — PostgreSQL і JPA

### Що змінилось

| Було | Стало |
|---|---|
| `InMemoryBookRepository` (`Map` у пам'яті) | `JpaBookRepositoryAdapter` → `BookJpaRepository` (Spring Data) → PostgreSQL |
| `Book` — звичайний клас | `Book` — `@Entity`, таблиця `books` |
| дані зникають після перезапуску | дані в томі `pgdata` переживають перезапуск |

**Сервіс і контролер не змінились** (DIP): вони залежать від інтерфейсу `BookRepository`,
а не від його реалізації. Змінилась лише реалізація репозиторію. In-memory варіант лишився
під профілем `inmemory`, а `@Primary` на адаптері гарантує, що за замовчуванням Spring
обере JPA.

Єдина правка в сервісі — анотації `@Transactional`, без зміни логіки:
- клас позначено `@Transactional(readOnly = true)`, тож читання йде в транзакції лише для читання;
- `create`, `update`, `delete` мають `@Transactional`, тому «перевірити, що книга є → записати»
  виконується як одне ціле.

`LazyInitializationException` тут не виникає, бо в `Book` немає лінивих колекцій
(`@ElementCollection`, `@OneToMany`): усі поля прості й читаються одним `SELECT`. OSIV вимкнено
(`spring.jpa.open-in-view: false`).

Сидер тепер заповнює таблицю лише тоді, коли вона порожня, інакше кожен перезапуск додавав би
книги повторно.

### Доказ персистентності

Перевірено так: `docker compose up --build` → `POST /api/books` («Інтернат») →
`docker compose restart app` → книга на місці; далі `docker compose down` (без `-v`) і знову
`up` → книга теж на місці. Рядок у PostgreSQL:

```
$ docker compose exec postgres psql -U bookstore -d bookstore -c "SELECT id, title, author, price, published_year, genre FROM books ORDER BY id;"
 id |        title         |        author         | price  | published_year |    genre
----+----------------------+-----------------------+--------+----------------+--------------
  1 | Кобзар               | Тарас Шевченко        | 420.00 |           1840 | Поезія
  2 | Тіні забутих предків | Михайло Коцюбинський  | 235.50 |           1911 | Повість
  3 | Лісова пісня         | Леся Українка         | 199.00 |           1911 | Драма-феєрія
  4 | Місто                | Валер'ян Підмогильний | 310.00 |           1928 | Роман
  5 | Ворошиловград        | Сергій Жадан          | 385.00 |           2010 | Роман
  6 | Інтернат             | Сергій Жадан          | 360.00 |           2017 | Роман
(6 rows)
```
