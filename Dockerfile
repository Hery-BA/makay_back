FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

COPY pom.xml .
RUN sed -i 's/<name\/>/<name>makay<\/name>/g' pom.xml && \
    sed -i 's/<description\/>/<description>Makay Adventures Application<\/description>/g' pom.xml && \
    sed -i 's/<url\/>/<url>https:\/\/makay-adventures.com<\/url>/g' pom.xml

COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copie tout le source D'ABORD
COPY src src

# Puis REMPLACE application.properties par la version propre
RUN cat > src/main/resources/application.properties << 'EOF'
spring.application.name=makay
server.port=${PORT:8080}

spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${PGUSER}
spring.datasource.password=${PGPASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.defer-datasource-initialization=true

spring.flyway.enabled=false
spring.flyway.locations=classpath:db/migration

spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

app.jwt.secret=${JWT_SECRET:makay-super-secret-key-dev-only-change-in-prod-minimum-256-bits}
app.jwt.expiration-ms=86400000

supabase.url=${SUPABASE_URL}
supabase.service-role-key=${SUPABASE_SERVICE_ROLE_KEY}
supabase.storage.bucket=${SUPERBASE_BUCKET:makay-media}

whatsapp.phone.admin=${ADMIN_PHONE:261340000000}

resend.api-key=${RESEND_API_KEY:your_resend_key}
resend.from-email=contact@makay-adventures.com
resend.to-email=${ADMIN_EMAIL:admin@makay-adventures.com}

cors.allowed-origins=${FRONTEND_URL:http://localhost:3000}
EOF

RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --from=build --chown=appuser:appgroup /app/target/makay-*.jar app.jar

EXPOSE 8080
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]