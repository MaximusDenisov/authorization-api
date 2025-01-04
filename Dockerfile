# Используем образ OpenJDK
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /authorization-api

# Копируем JAR-файл в контейнер
COPY target/authorization-api-0.1.jar app.jar

# Открываем порт, на котором работает приложение (например, 8080)
EXPOSE 4323

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]