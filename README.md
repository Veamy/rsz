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
