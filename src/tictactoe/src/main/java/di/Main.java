package di;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Файл: di/Main.java
 *
 * Назначение файла: точка входа backend-приложения (задание 1 — переход на PostgreSQL).
 *
 * Почему аннотации перечислены явно: пакеты проекта лежат не под корневым пакетом
 * одного имени (di, domain, datasource, security, web), поэтому стандартного
 * «сканируй соседние пакеты» недостаточно. Указываем границы вручную:
 *
 * @SpringBootApplication        — включает автоконфигурации Spring Boot
 *                                 (в т.ч. DataSource по spring.datasource.* из application.properties);
 * @ComponentScan                — какие пакеты искать на @Component/@Service/@RestController/@Configuration
 *                                 (domain, datasource, security, web);
 * @EnableJpaRepositories        — включает генерацию репозиториев Spring Data JPA
 *                                 для интерфейсов, наследующих CrudRepository (datasource/repository);
 * @EntityScan                   — где искать классы-сущности с @Entity (datasource/model),
 *                                 чтобы Hibernate создал/обновил таблицы в PostgreSQL.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"di", "domain", "datasource", "security", "web"})
@EnableJpaRepositories(basePackages = "datasource.repository")
@EntityScan(basePackages = "datasource.model")
public class Main {

    /**
     * Стандартный main: поднимает контекст Spring Boot и встроенный веб-сервер
     * (порт берётся из application.properties, по умолчанию 8080).
     *
     * @param args аргументы командной строки, передаются Spring Boot как есть
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
