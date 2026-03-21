# NBA Guesser

Quiz game that fetches NBA players from the NBA API and sends quiz questions to Telegram.

## Tech Stack

- **Backend**: Spring Boot 3.0.2, Java 17, Maven
- **Architecture**: Hexagonal (ports & adapters), DDD

## Architecture

```
com.github.hdurix.nbaguesser/
├── domain/           — Pure business logic, no framework dependencies
├── application/      — Use cases, orchestration
├── infrastructure/
│   └── secondary/    — Outbound adapters (NBA API, Telegram)
├── security/         — Cross-cutting (CORS)
├── error/            — Domain exceptions + REST error handlers
└── common/           — Shared utilities (Assert, etc.)
```

Bounded context markers: `@BusinessContext` / `@SharedKernel` annotations on `package-info.java`.

## Backend Conventions

- Value objects: Java records with `Assert` validation in compact constructor
- Domain interfaces: simple names (`Players`, `Quizzes`) — no suffix
- Application services: `@Service`, constructor-based DI
- External API DTOs: prefixed with `Api` (`ApiPlayer`, `ApiLeague`), mapped to domain objects
- Controllers: package-private
- All naming in English

## Testing

- **Coverage: 100% lines + branches**, enforced by JaCoCo in CI
- Test annotations: `@UnitTest`, `@IntegrationTest`, `@ComponentTest` (all use `ReplaceCamelCase` display name)
- Test fixtures: `*Fixture.java` classes for test data builders
- Architecture enforcement: ArchUnit
- Unit tests excluded from integration phase (surefire excludes `*IT`, `*IntTest`)
- Integration tests run via failsafe plugin

## Formatting

- Prettier (Java, XML, JSON, HTML, CSS, SCSS) — print width 140, single quotes, LF
- Husky + lint-staged: pre-commit hook runs Prettier on staged files
- **Always run Prettier before committing**

## Secrets & Leak Prevention

- **Sensitive config** (`telegram.bot_id`, `telegram.chat_id`, API keys) is injected via environment variables — **never** hardcode secrets in source or properties files
- Before any commit/push, verify no token, key, or credential has been introduced in the diff
- `.env` files, if used locally, must **not** be committed (add to `.gitignore`)

## Workflow

- **VCS**: GitHub (`git@github.com:hdurix/nba-guesser.git`), PRs to `main`
- **CI**: runs on GitHub Actions
- **Worktree setup**: after creating a new worktree, run `npm install` to get Prettier, Husky, and lint-staged working

## Commands

- **Run app**: `./mvnw` (default goal is `spring-boot:run`)
- **Unit tests**: `./mvnw test`
- **Full validation** (tests + coverage): `./mvnw clean verify`
- **Format check**: `npx prettier --check .`
- **Format fix**: `npx prettier --write .`
