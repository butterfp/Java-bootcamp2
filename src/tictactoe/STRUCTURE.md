# Структура backend-проекта «Крестики-нолики» (Spring Boot)

Проект собирается Gradle (Kotlin DSL), Java 17+.
Каждый слой приложения вынесен в отдельный пакет (задание 1 курса):

```
src/main/java
├── di/                      — слой зависимости внедрения (точка входа и конфигурация бинов)
│   ├── Main.java            — класс запуска Spring Boot (@SpringBootApplication)
│   └── GameConfig.java      — @Configuration с дополнительными бинами приложения
│
├── domain/                  — доменный слой (бизнес-модели и бизнес-логика, не знает про БД и HTTP)
│   ├── model/               — модели предметной области
│   │   ├── GameBoard.java   — игровое поле int[3][3] + правила проверки корректности хода
│   │   ├── GameSession.java — текущая игра: id, игроки, значки, чей ход, состояние, победитель
│   │   ├── GameState.java   — состояния игры (задание 3): WAITING/X_TURN/O_TURN/DRAW/WIN_X/WIN_O
│   │   ├── PlayerType.java  — тип участника (задание 3): USER (человек) или AI (компьютер)
│   │   ├── User.java        — пользователь: UUID, логин, пароль (задание 2)
│   │   └── SignUpRequest.java — DTO регистрации: логин + пароль (задание 2)
│   ├── repository/          — интерфейсы репозиториев (контракты для слоя datasource)
│   │   ├── GameRepository.java — контракт хранения игр
│   │   └── UserRepository.java — контракт хранения пользователей
│   └── service/             — сервисы (бизнес-логика)
│       ├── TicTacToeService.java / impl/TicTacToeServiceImpl.java — логика игры и алгоритм minimax
│       ├── UserService.java     / impl/UserServiceImpl.java       — CRUD пользователей
│       └── AuthService.java     / impl/AuthServiceImpl.java       — регистрация и авторизация (Basic)
│
├── datasource/              — слой работы с базой данных PostgreSQL (задание 1)
│   ├── model/               — JPA-сущности (@Entity), которые сохраняются в БД
│   │   ├── GameEntity.java    — таблица game_sessions (доска хранится как JSON-строка)
│   │   └── UserEntity.java    — таблица app_users (UUID, login, password)
│   ├── mapper/              — конвертеры «сущность БД <-> доменная модель»
│   │   ├── GameMapper.java    — GameSession <-> GameEntity
│   │   └── UserMapper.java    — User <-> UserEntity
│   └── repository/          — Spring Data репозитории, наследники CrudRepository
│       ├── GameCrudRepository.java  — extends CrudRepository<GameEntity, UUID>
│       └── UserCrudRepository.java  — extends CrudRepository<UserEntity, UUID>
│       └── impl/
│           ├── GameRepositoryImpl.java — реализация контракта domain поверх CrudRepository
│           └── UserRepositoryImpl.java  — реализация контракта domain поверх CrudRepository
│
├── security/                — слой безопасности (задание 2)
│   ├── AuthFilter.java      — GenericFilterBean: проверяет Basic-заголовок base64(login:password)
│   └── SecurityConfig.java  — @Configuration: SecurityFilterChain, публичные /auth/**, AuthFilter в цепочке
│
└── web/                     — веб-слой (HTTP API)
    ├── controller/
    │   ├── AuthController.java  — POST /auth/sign-up, POST /auth/sign-in (задание 2)
    │   ├── GameController.java  — игры: создание, список, подключение, ход, получение (задание 3)
    │   └── UserController.java  — GET /user/{id} — информация о пользователе по UUID (задание 3)
    ├── mapper/WebMapper.java    — доменные модели <-> DTO ответов/запросов
    ├── model/                   — DTO транспорта HTTP
    │   ├── GameRequestDTO.java   — тело запроса хода (обновлённая доска)
    │   ├── GameResponseDTO.java  — ответ: id, участники, доска, чей ход, состояние, победитель
    │   ├── PlayerInfoDTO.java    — участник игры (тип + UUID)
    │   └── UserInfoDTO.java      — ответ о пользователе (без пароля)
    └── exception/GlobalExceptionHandler.java — перевод ошибок в коды 400/401/404

src/main/resources
└── application.properties   — подключение к PostgreSQL (задание 1)
```

## Как слои связаны между собой (направление зависимостей однонаправленное)

```
HTTP-запрос
   → web (контроллеры, DTO)            — принимает запрос, отвечает JSON
   → security (AuthFilter)             — до контроллера: пропускает только авторизованных
   → domain (сервисы, модели)          — бизнес-правила игры и пользователей
   → domain.repository (интерфейсы)    — контракт «что нужно хранить»
   → datasource (JPA-сущности, CrudRepository, мапперы) — как именно это лежит в PostgreSQL
```

Класс-хранилище (in-memory `GameStorage`) удалён: все данные живут только в PostgreSQL.
