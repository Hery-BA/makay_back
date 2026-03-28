FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Set encoding environment variables
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

COPY . .

# Make mvnw executable
RUN chmod +x ./mvnw

# Fix line endings and remove BOM from properties files
RUN apk add --no-cache dos2unix && \
    find . -name "*.properties" -type f -exec dos2unix {} \; && \
    find . -name "*.properties" -type f -exec sed -i '1s/^\xEF\xBB\xBF//' {} \;

# Build the application
RUN ./mvnw clean package -DskipTests \
    -Dproject.build.sourceEncoding=UTF-8 \
    -Dmaven.resources.encoding=UTF-8

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

COPY --from=build /app/target/makay-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]