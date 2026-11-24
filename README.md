# rsz

Навчальний інтернет-магазин на **Spring Boot 3.4 · Java 21 · Maven**.

## Локальний запуск

```bash
./mvnw spring-boot:run
./mvnw test
```

## Docker

| Команда | Що робить | Порти |
|---|---|---|
| `docker compose up --build` | dev: код з хоста, devtools-перезапуск, дебаг | 8080, 5005 |
| `docker compose --profile prod up --build app-prod` | зібраний jar у JRE-образі | 8081 |

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

### Що не покрито і чому

- **Повний ланцюжок усіх шарів разом** (контролер → справжній сервіс → сховище). `@WebMvcTest`
  підміняє сервіс, а модульні тести не піднімають HTTP. Повний прогін є лише як
  `contextLoads` і ручна перевірка через `requests.md`. Наскрізний тест з'явиться разом із
  реальною БД (заняття 2.1, Testcontainers).
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
