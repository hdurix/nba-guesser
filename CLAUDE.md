# NBA Guesser

CommandLineRunner (CLR) quiz game: fetches NBA players, generates a quiz, sends it to Telegram, then exits.

## Tech Stack

- **Backend**: Spring Boot 4.0.4, Java 25, Maven
- **Mode**: CommandLineRunner (no web server — `spring.main.web-application-type=NONE`)
- **Architecture**: Hexagonal (ports & adapters), DDD

## Architecture

```
com.github.hdurix.nbaguesser/
├── domain/           — Pure business logic (Player, Players, Quizzes, QuizResult, QuizResults, QuizGenerator, Randomizer)
├── application/      — Use case orchestration (PlayersApplicationService)
├── infrastructure/
│   └── secondary/
│       ├── api/      — NBA player data adapter (NbaApiPlayers, ApiPlayersWrapper, ApiResultSet)
│       ├── dynamodb/ — Quiz results adapter (DynamoDbQuizResults)
│       └── telegram/ — Telegram quiz adapter (TelegramQuizzes)
├── error/            — Domain exceptions + REST error handlers
└── common/           — Shared utilities (Assert, Generated, etc.)
```

Bounded context markers: `@BusinessContext` / `@SharedKernel` annotations on `package-info.java`.

## Backend Conventions

- Value objects (≤3 fields): Java records with `Assert` validation in compact constructor
- Value objects (>3 fields or 2+ fields of same type): `final class` with private constructor + builder pattern (record-style accessors, no `get` prefix)
- Domain interfaces: simple names (`Players`, `Quizzes`) — no suffix
- Application services: `@Service`, constructor-based DI
- External API DTOs: prefixed with `Api` (`ApiPlayer`, `ApiResultSet`), mapped to domain objects
- Adapters: package-private
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
- **Rebase on main**: always `git fetch origin main && git rebase origin/main` before starting work — many PRs run in parallel so staying up-to-date avoids conflicts
- **Always create a PR**: after committing, push the branch and create a PR to `main` — don't wait to be asked

## AWS Deployment

The app runs on **AWS** (`eu-west-3` / Paris), account `301722318422`.

### How it works

Every day at **06:30 UTC** (08:30 Paris), an **EventBridge** cron rule triggers an **ECS Fargate** task:

1. EventBridge rule `nba-guesser` fires
2. ECS cluster `nba-guessers` starts a Fargate task (0.5 vCPU, 1 GB RAM)
3. The container (from ECR `nba-guesser-images:latest`) boots Spring Boot, fetches NBA players, sends a Telegram quiz, then exits

### AWS Resources

| Service             | Resource                                             | Purpose                                       |
| ------------------- | ---------------------------------------------------- | --------------------------------------------- |
| **ECR**             | `nba-guesser-images`                                 | Docker image repository                       |
| **ECS**             | Cluster `nba-guessers`, task def `nba-guesser:4`     | Runs the container on Fargate                 |
| **EventBridge**     | Rule `nba-guesser` (`cron(30 6 * * ? *)`)            | Daily scheduling (only active rule)           |
| **CloudWatch Logs** | `/ecs/nba-guessers`                                  | Container logs                                |
| **IAM**             | `ecsTaskExecutionRole`, `ecsEventsRole`              | ECS task execution + EventBridge trigger      |
| **DynamoDB**        | `nba-guesser-quizzes` (PK: `poll_id`)                | Quiz metadata (date, options, correct answer) |
| **DynamoDB**        | `nba-guesser-answers` (PK: `poll_id`, SK: `user_id`) | Player answers (user_name, option, timestamp) |

### Other EventBridge rules (all DISABLED)

- `nba-guesser-ippon` — `cron(0 7 * * ? *)` — targeted a different Telegram group
- `nba-guesser-safe` — `cron(45 5 * * ? *)` — targeted a different Telegram group
- `guesser-test` / `test` — test rules

### History

- **Apr 2023**: first ECS Fargate deployment with Docker images
- **Feb 2024**: tried a Lambda approach (`nba-guesser` function, Java 17, `LambdaHandler`) — abandoned (likely cold start / timeout issues)
- **Feb 2024**: returned to ECS Fargate with optimized images (~179 MB vs ~225 MB)

### CLI access

AWS CLI is configured with a named profile:

```bash
aws configure --profile perso   # region: eu-west-3
aws sts get-caller-identity --profile perso  # verify account
```

All commands must use `--profile perso --region eu-west-3`.

## Commands

- **Run app**: `./mvnw` (default goal is `spring-boot:run`)
- **Unit tests**: `./mvnw test`
- **Full validation** (tests + coverage): `./mvnw clean verify`
- **Format check**: `npx prettier --check .`
- **Format fix**: `npx prettier --write .`
- **Deploy ECS** (manual): `gh workflow run deploy.yml --ref main` (builds Docker image and pushes to ECR)
- **Deploy webhook** (manual): `gh workflow run deploy-webhook.yml --ref main` (builds Lambda image, pushes to ECR, updates Lambda)
