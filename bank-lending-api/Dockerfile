# Стадия сборки
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем POM и загружаем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Копируем артефакт из стадии сборки
COPY --from=build /app/target/*.jar app.jar

# Настройки окружения
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8088

EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]