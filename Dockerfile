FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Set encoding
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

# Copy and fix pom.xml first
COPY pom.xml .
RUN sed -i 's/<name\/>/<name>makay<\/name>/g' pom.xml && \
    sed -i 's/<description\/>/<description>Makay Adventures Application<\/description>/g' pom.xml && \
    sed -i 's/<url\/>/<url>https:\/\/makay-adventures.com<\/url>/g' pom.xml

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Create a clean application.properties directly in the container
RUN mkdir -p src/main/resources && \
    cat > src/main/resources/application.properties << 'EOF'
# Application
spring.application.name=makay

# Server
server.port=${PORT:8080}

# Database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${PGUSER}
spring.datasource.password=${PGPASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true

# Flyway
spring.flyway.enabled=false
spring.flyway.locations=classpath:db/migration
logging.level.org.flywaydb=DEBUG
logging.level.org.springframework.boot.autoconfigure.flyway=DEBUG
logging.level.org.springframework.jdbc=DEBUG

whatsapp.phone.admin=${ADMIN_PHONE}

# File Upload
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# JWT
app.jwt.secret=${JWT_SECRET:makay-super-secret-key-dev-only-change-in-prod-minimum-256-bits}
app.jwt.expiration-ms=86400000

# Supabase
supabase.url=${SUPABASE_URL}
supabase.service-role-key=${SUPABASE_SERVICE_ROLE_KEY}
supabase.storage.bucket=${SUPERBASE_BUCKET}

# Resend Email
resend.api-key=${RESEND_API_KEY:your_resend_key}
resend.from-email=contact@makay-adventures.com
resend.to-email=${ADMIN_EMAIL:admin@makay-adventures.com}

# CORS
cors.allowed-origins=${FRONTEND_URL:http://localhost:3000}
EOF

# Copy source code (excluding application.properties to avoid conflicts)
COPY src src
RUN rm -f src/main/resources/application.properties

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Build the application
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --from=build --chown=appuser:appgroup /app/target/makay-*.jar app.jar

EXPOSE 8080

USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]