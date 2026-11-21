# ---------- deps: Maven wrapper + залежності (кешується, поки не змінився pom.xml) ----------
FROM eclipse-temurin:21-jdk AS deps
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline

# ---------- dev: запуск із сирців, devtools + віддалений дебаг (див. compose.yaml) ----------
FROM deps AS dev
EXPOSE 8080 5005
CMD ["sh", "-c", "./mvnw -B spring-boot:run -Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]

# ---------- build: збірка jar ----------
FROM deps AS build
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests

# ---------- runtime: мінімальний образ лише з JRE ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
RUN useradd --system --uid 1001 spring
COPY --from=build /app/target/*.jar app.jar
USER spring
EXPOSE 8080
# Дебаг у готовому образі: docker run -e JAVA_TOOL_OPTIONS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005' -p 5005:5005 ...
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD curl -fs http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
