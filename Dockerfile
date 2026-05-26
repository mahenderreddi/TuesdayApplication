# Step 1 - Use Java 17 as base image
FROM eclipse-temurin:17-jdk-alpine

# Step 2 - Set working directory inside container
WORKDIR /app

# Step 3 - Copy maven files first (for caching)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Step 4 - Download dependencies
RUN ./mvnw dependency:go-offline

# Step 5 - Copy source code
COPY src ./src

# Step 6 - Build the application
RUN ./mvnw clean package -DskipTests

# Step 7 - Expose port
EXPOSE 8080

# Step 8 - Run the application
ENTRYPOINT ["java", "-jar", "target/*.jar"]