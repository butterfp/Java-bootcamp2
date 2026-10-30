// Файл: build.gradle.kts — сборочный скрипт backend-проекта (Gradle, Kotlin DSL).
// Здесь объявлены только те зависимости, которые нужны трём заданиям курса:
//   задание 1 — PostgreSQL + Spring Data JPA;
//   задание 2 — Spring Security (PasswordEncoder/SecurityFilterChain);
//   веб-слой  — Spring MVC (REST-контроллеры).
// Сторонние библиотеки сверх списка задания (JWT и т.п.) удалены: авторизация
// сделана на Basic-заголовке base64(login:password) и собственном AuthFilter.

plugins {
    id("java")                                        // компиляция Java-кода
    id("org.springframework.boot") version "3.3.4"     // плагин сборки executable jar/war
    id("io.spring.dependency-management") version "1.1.6" // подбор версий зависимостей Spring Boot
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Веб-слой: REST-контроллеры, встроенный Tomcat, Jackson для JSON
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Задание 1: Spring Data JPA (Hibernate + CrudRepository) — доступ к PostgreSQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Задание 2: Spring Security — SecurityFilterChain, PasswordEncoder (BCrypt)
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Драйвер PostgreSQL (нужен только во время выполнения, поэтому runtimeOnly)
    runtimeOnly("org.postgresql:postgresql")

    // Тесты
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Запуск тестов через платформу JUnit 5
tasks.test {
    useJUnitPlatform()
}

// Java-версия исходников и байт-кода (в проекте используются record и switch-выражения)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}