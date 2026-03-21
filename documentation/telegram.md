# Telegram integration

The `TelegramQuizzes` adapter sends quiz questions to a Telegram channel using the Telegram Bot API.

## Configuration

Two properties are required, configured via Maven `settings.xml`:

| Property           | Description            | Example          |
| ------------------ | ---------------------- | ---------------- |
| `telegram.bot_id`  | Telegram bot token     | `123456:ABC-...` |
| `telegram.chat_id` | Target chat/channel ID | `-100123456789`  |

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
