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

### Формат помилок

Усі помилки проходять через `GlobalExceptionHandler` і мають один формат:

```json
{"status":400,"error":"Bad Request","message":"Помилка валідації вхідних даних",
 "details":["price: ціна має бути додатною","title: назва обов'язкова"]}
```
