# Приклади запитів до API книгарні

Запуск: `docker compose up --build` (або `./mvnw spring-boot:run`). База: `http://localhost:8080`.

| Операція | Метод і шлях | Успіх | Помилка |
|---|---|---|---|
| Перелік (фільтр за жанром) | `GET /api/books?genre=...` | `200` | — |
| Одна книга | `GET /api/books/{id}` | `200` | `404` |
| Створити | `POST /api/books` | `201` | — |
| Оновити | `PUT /api/books/{id}` | `200` | `404` |
| Видалити | `DELETE /api/books/{id}` | `204` | `404` |

## 1. Перелік книг

```bash
curl -s http://localhost:8080/api/books
```

Відповідь `200` — масив карток:

```json
[
  {"id":1,"title":"Кобзар","author":"Тарас Шевченко","price":420.00,"coverUrl":"/images/covers/kobzar.svg"},
  {"id":2,"title":"Тіні забутих предків","author":"Михайло Коцюбинський","price":235.50,"coverUrl":"/images/covers/tini.svg"}
]
```

## 2. Фільтр за жанром

Регістр не важливий. Кирилицю в URL треба кодувати, тому `-G --data-urlencode`:

```bash
curl -s -G --data-urlencode "genre=роман" http://localhost:8080/api/books
```

Відповідь `200`: лише «Місто» і «Ворошиловград».

## 3. Одна книга з деталями

```bash
curl -s http://localhost:8080/api/books/2
```

Відповідь `200`:

```json
{"id":2,"title":"Тіні забутих предків","author":"Михайло Коцюбинський","price":235.50,
 "publishedYear":1911,"genre":"Повість","pages":128,"coverUrl":"/images/covers/tini.svg",
 "description":"Історія кохання Івана та Марічки на тлі гуцульських Карпат, їхніх звичаїв і вірувань."}
```

Неіснуюча книга:

```bash
curl -i http://localhost:8080/api/books/999
```

Відповідь: `HTTP/1.1 404`.

## 4. Створити книгу

```bash
curl -i -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Інтернат","author":"Сергій Жадан","price":360.00,"publishedYear":2017,
       "genre":"Роман","pages":336,"coverUrl":null,
       "description":"Вчитель Паша їде через охоплене війною місто по племінника."}'
```

Відповідь `201` — створена книга з новим `id` (наприклад `6`).

## 5. Оновити книгу

```bash
curl -i -X PUT http://localhost:8080/api/books/6 \
  -H "Content-Type: application/json" \
  -d '{"title":"Інтернат","author":"Сергій Жадан","price":299.00,"publishedYear":2017,
       "genre":"Роман","pages":336}'
```

Відповідь `200` — оновлена книга. Для неіснуючого id (`/api/books/999`) — `404`.

## 6. Видалити книгу

```bash
curl -i -X DELETE http://localhost:8080/api/books/6
```

Відповідь `204` без тіла. Повторне видалення або неіснуючий id — `404`.

## Обкладинка

```bash
curl -I http://localhost:8080/images/covers/kobzar.svg
```
