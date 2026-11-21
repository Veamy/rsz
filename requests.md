# Приклади запитів до API книгарні

Запуск: `docker compose up --build` (або `./mvnw spring-boot:run`). База: `http://localhost:8080`.

## Перелік книг

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

## Одна книга з деталями

```bash
curl -s http://localhost:8080/api/books/2
```

Відповідь `200`:

```json
{"id":2,"title":"Тіні забутих предків","author":"Михайло Коцюбинський","price":235.50,
 "publishedYear":1911,"genre":"Повість","pages":128,"coverUrl":"/images/covers/tini.svg",
 "description":"Історія кохання Івана та Марічки на тлі гуцульських Карпат, їхніх звичаїв і вірувань."}
```

## Неіснуюча книга → 404

```bash
curl -i http://localhost:8080/api/books/999
```

Відповідь: `HTTP/1.1 404`.

## Обкладинка

```bash
curl -I http://localhost:8080/images/covers/kobzar.svg
```
