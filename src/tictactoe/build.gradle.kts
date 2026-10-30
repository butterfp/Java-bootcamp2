//ЕСЛИ ЧТО ВДРУГ НАДО ОЬНОВИТЬ Gradle, иначе IDEA не найдет Jwts.builder()!!!

plugins {
    id("java")
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Драйвер PostgreSQL
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // (опционально) для миграций БД
    // implementation("org.liquibase:liquibase-core")
    // или
    // implementation("org.flywaydb:flyway-core")
}

tasks.test {
    useJUnitPlatform()
}