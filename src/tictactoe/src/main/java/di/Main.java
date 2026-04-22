package di;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("✅ Сервер запущен на http://localhost:8080");
    }

    @GetMapping("/")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    @GetMapping("/bye")
    public String bye() {
        return "POKA!";
    }
}