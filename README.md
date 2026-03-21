# NBA Guesser

Quiz game that fetches NBA players and sends quiz questions to a Telegram channel.

The application runs as a **Spring Boot CommandLineRunner (CLR)**: it boots, generates one quiz, sends it to Telegram, and exits.

## How it works

1. Fetches all active NBA players from an external JSON source
2. Randomly picks 10 players as quiz options
3. Selects one as the correct answer
4. Sends the player's headshot photo to a Telegram channel
5. Sends a poll with the 10 player names (quiz mode, with the correct answer marked)

## Prerequisites

- Java 17
- Maven 3.6+
- Node.js (for Prettier formatting)
- Telegram bot credentials (`telegram.bot_id` and `telegram.chat_id`)

## Setup

Install Node.js dependencies (for formatting only):

```bash
npm install
```

Configure Telegram credentials in your Maven `~/.m2/settings.xml` (never committed):

```xml
<profiles>
  <profile>
    <id>nba-guesser</id>
    <properties>
      <nba-guesser-bot.telegram.bot-id>YOUR_BOT_TOKEN</nba-guesser-bot.telegram.bot-id>
      <nba-guesser-test.telegram.chat-id>YOUR_CHAT_ID</nba-guesser-test.telegram.chat-id>
    </properties>
  </profile>
</profiles>
<activeProfiles>
  <activeProfile>nba-guesser</activeProfile>
</activeProfiles>
```

These Maven properties are mapped to Spring's `telegram.bot_id` / `telegram.chat_id` via the failsafe plugin (see [Telegram integration](documentation/telegram.md)).

## Commands

```bash
./mvnw                    # Run the app (generates and sends a quiz)
./mvnw test               # Unit tests only
./mvnw clean verify       # Full validation (tests + 100% coverage check)
npx prettier --check .    # Check formatting
npx prettier --write .    # Fix formatting
```

## Documentation

- [Architecture](documentation/architecture.md)
- [NBA API integration](documentation/nba-api.md)
- [Telegram integration](documentation/telegram.md)
- [Assertions](documentation/assertions.md)
- [Package types](documentation/package-types.md)
- [LogsSpy](documentation/logs-spy.md)

<!-- jhipster-needle-documentation -->
