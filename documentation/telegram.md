# Telegram integration

The `TelegramQuizzes` adapter sends quiz questions to a Telegram channel using the Telegram Bot API.

## Configuration

Two Spring properties are required at runtime:

| Spring property    | Description            |
| ------------------ | ---------------------- |
| `telegram.bot_id`  | Telegram bot token     |
| `telegram.chat_id` | Target chat/channel ID |

### Credentials via Maven `settings.xml`

Credentials are kept out of the repository by defining them in `~/.m2/settings.xml`:

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

The failsafe plugin maps these Maven properties to Spring system properties for integration tests:

```xml
<!-- pom.xml — maven-failsafe-plugin -->
<systemPropertyVariables>
  <telegram.bot_id>${nba-guesser-bot.telegram.bot-id}</telegram.bot_id>
  <telegram.chat_id>${nba-guesser-test.telegram.chat-id}</telegram.chat_id>
</systemPropertyVariables>
```

For running the app directly, pass them as system properties:

```bash
./mvnw -Dtelegram.bot_id=YOUR_BOT_TOKEN -Dtelegram.chat_id=YOUR_CHAT_ID
```

## Quiz flow

Each quiz consists of **two Telegram messages** sent sequentially:

### 1. Player photo (`sendPhoto`)

Sends the correct player's headshot with a caption:

```
GET /bot{bot_id}/sendPhoto
  ?chat_id={chat_id}
  &photo=https://cdn.nba.com/headshots/nba/latest/1040x760/{playerId}.png
  &caption=🏀 Let's play! 🧠
```

### 2. Quiz poll (`sendPoll`)

Sends a quiz-type poll with 10 player names:

```
GET /bot{bot_id}/sendPoll
  ?chat_id={chat_id}
  &question=Who is this player?
  &options=["Player 1","Player 2",...,"Player 10"]
  &correct_option_id={index}
  &is_anonymous=false
  &type=quiz
```

The `correct_option_id` is the zero-based index of the correct player in the options list.

## Creating a Telegram bot

1. Message [@BotFather](https://t.me/BotFather) on Telegram
2. Send `/newbot` and follow the instructions
3. Copy the bot token → `telegram.bot_id`
4. Add the bot to a channel/group as admin
5. Get the chat ID → `telegram.chat_id`
