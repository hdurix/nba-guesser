# Architecture

NBA Guesser follows a **hexagonal architecture** (ports & adapters) with DDD principles.

## Overview

```
com.github.hdurix.nbaguesser/
├── domain/                          — Pure business logic, no framework dependencies
│   ├── Player                       — Value object (record)
│   ├── Players                      — Port: fetch players
│   ├── Quizzes                      — Port: send quizzes
│   ├── QuizzGenerator               — Core business logic
│   └── Randomizer                   — Random selection utility
├── application/
│   └── PlayersApplicationService    — Use case orchestration (@Service)
├── infrastructure/
│   └── secondary/
│       ├── api/                     — NBA player data adapter
│       │   ├── NbaApiPlayers        — Implements Players port
│       │   ├── ApiPlayersWrapper    — JSON deserialization
│       │   └── ApiResultSet         — Row-level mapping
│       └── telegram/                — Telegram quiz adapter
│           └── TelegramQuizzes      — Implements Quizzes port
├── error/domain/                    — Domain exceptions (Assert, MissingMandatoryValueException...)
├── security/                        — CORS filter configuration
└── common/domain/                   — Shared utilities (Generated, NbaGuesserCollections)
```

## Application flow (CLR)

The app implements `CommandLineRunner`. On startup:

1. Spring Boot wires `PlayersApplicationService` with the two adapters (`NbaApiPlayers`, `TelegramQuizzes`)
2. `run()` calls `PlayersApplicationService.generateQuizz()`
3. `QuizzGenerator.generate()` orchestrates the domain logic:
   - Calls `Players.listAll()` to fetch all active NBA players
   - Uses `Randomizer.tenOutOf()` to pick 10 random indices
   - Uses `Randomizer.oneOf()` to select the correct answer
   - Calls `Quizzes.send()` to deliver the quiz

The web server is disabled (`spring.main.web-application-type=NONE`).

## Ports (domain interfaces)

Domain interfaces use **simple names without suffixes**:

| Port      | Method                                                    | Role              |
| --------- | --------------------------------------------------------- | ----------------- |
| `Players` | `Collection<Player> listAll()`                            | Fetch all players |
| `Quizzes` | `void send(Collection<Player> suggested, Player correct)` | Deliver a quiz    |

## Conventions

- **Value objects**: Java records with `Assert` validation in compact constructor
- **Application services**: `@Service`, constructor-based DI
- **Adapters**: package-private classes, annotated `@Repository` or `@Service`
- **External API DTOs**: prefixed with `Api` (e.g., `ApiPlayer`, `ApiResultSet`)
- **Architecture enforcement**: ArchUnit tests in `HexagonalArchTest`
