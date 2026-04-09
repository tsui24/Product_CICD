FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Chạy lệnh build của Maven
RUN mvn clean package -DskipTests

# Giai đoạn 2: Tạo image chạy thực tế
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Lấy file jar đã build xong từ Stage 1 sang Stage 2
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]