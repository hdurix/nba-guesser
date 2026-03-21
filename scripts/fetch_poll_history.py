#!/usr/bin/env python3
"""Fetch historical NBA Guesser quiz results from Telegram.

Uses the Telegram Client API (MTProto) via Telethon to retrieve individual
votes on non-anonymous quiz polls — something the Bot API cannot do.

Prerequisites:
  1. pip install telethon
  2. Get API credentials from https://my.telegram.org ("API development tools")
  3. Set environment variables:
       TELEGRAM_API_ID    — your API ID (number)
       TELEGRAM_API_HASH  — your API hash (string)
       TELEGRAM_CHAT_ID   — the chat/group ID where quizzes are sent

Usage:
  python scripts/fetch_poll_history.py > results.json
"""

import asyncio
import json
import os
import sys
from collections import defaultdict
from datetime import datetime, timezone

from telethon import TelegramClient
from telethon.tl.functions.messages import GetPollVotesRequest
from telethon.tl.types import MessageMediaPoll, MessagePeerVote, MessagePeerVoteMultiple, PeerUser

SESSION_FILE = os.path.join(os.path.dirname(__file__), "nba_guesser_session")


def extract_text(value):
    """Handle both plain str and TextWithEntities objects."""
    if hasattr(value, "text"):
        return value.text
    return str(value)


async def get_votes(client, peer, msg_id, answers):
    """Fetch individual votes for a poll message."""
    votes = []
    offset = None

    while True:
        kwargs = {"peer": peer, "id": msg_id, "limit": 100}
        if offset:
            kwargs["offset"] = offset

        try:
            result = await client(GetPollVotesRequest(**kwargs))
        except Exception as e:
            print(f"  Warning: cannot fetch votes for msg {msg_id}: {e}", file=sys.stderr)
            return votes

        user_map = {u.id: u for u in result.users}

        for v in result.votes:
            uid = v.peer.user_id if isinstance(v.peer, PeerUser) else None
            user = user_map.get(uid) if uid else None
            name = ""
            if user:
                name = " ".join(filter(None, [user.first_name, user.last_name]))

            opt_idx = None
            if isinstance(v, MessagePeerVote):
                for i, a in enumerate(answers):
                    if a.option == v.option:
                        opt_idx = i
                        break
            elif isinstance(v, MessagePeerVoteMultiple):
                if v.options:
                    for i, a in enumerate(answers):
                        if a.option == v.options[0]:
                            opt_idx = i
                            break

            votes.append({"user_id": uid, "user_name": name, "option_index": opt_idx})

        if not getattr(result, "next_offset", None):
            break
        offset = result.next_offset
        await asyncio.sleep(0.3)

    return votes


async def main():
    api_id = os.environ.get("TELEGRAM_API_ID")
    api_hash = os.environ.get("TELEGRAM_API_HASH")
    chat_id = os.environ.get("TELEGRAM_CHAT_ID")

    phone = os.environ.get("TELEGRAM_PHONE")

    if not all([api_id, api_hash, chat_id]):
        print("Error: set TELEGRAM_API_ID, TELEGRAM_API_HASH, TELEGRAM_CHAT_ID", file=sys.stderr)
        print("Get API credentials at https://my.telegram.org", file=sys.stderr)
        sys.exit(1)

    print("Starting Telegram client...", file=sys.stderr, flush=True)
    client = TelegramClient(SESSION_FILE, int(api_id), api_hash)
    await client.start(phone=phone if phone else lambda: input("Phone number (e.g. +33...): "))

    try:
        chat = await client.get_entity(int(chat_id))
    except ValueError:
        chat = await client.get_entity(chat_id)

    print("Connected. Scanning chat history...", file=sys.stderr)

    quizzes = []
    n = 0

    async for msg in client.iter_messages(chat):
        if not isinstance(getattr(msg, "media", None), MessageMediaPoll):
            continue

        poll_media = msg.media
        poll = poll_media.poll
        question = extract_text(poll.question)

        if "who is this player" not in question.lower():
            continue

        n += 1
        options = [extract_text(a.text) for a in poll.answers]

        correct_idx = None
        if poll.correct_answers:
            for i, a in enumerate(poll.answers):
                if a.option in poll.correct_answers:
                    correct_idx = i
                    break

        correct_name = options[correct_idx] if correct_idx is not None else "?"
        print(f"  #{n} {msg.date.strftime('%Y-%m-%d')} - {correct_name}", file=sys.stderr)

        votes = await get_votes(client, chat, msg.id, poll.answers)

        quizzes.append(
            {
                "date": msg.date.isoformat(),
                "message_id": msg.id,
                "options": options,
                "correct_option_index": correct_idx,
                "correct_answer": correct_name,
                "total_voters": poll_media.results.total_voters if poll_media.results else 0,
                "votes": votes,
            }
        )

        await asyncio.sleep(0.5)

    quizzes.reverse()  # chronological order

    # Compute per-player stats
    player_stats = defaultdict(lambda: {"correct": 0, "total": 0})
    for q in quizzes:
        for v in q["votes"]:
            key = v["user_name"] or str(v["user_id"])
            player_stats[key]["total"] += 1
            if v["option_index"] == q["correct_option_index"]:
                player_stats[key]["correct"] += 1

    rankings = {
        name: {
            **s,
            "accuracy": round(s["correct"] / s["total"] * 100, 1) if s["total"] > 0 else 0,
        }
        for name, s in sorted(player_stats.items(), key=lambda x: x[1]["correct"], reverse=True)
    }

    output = {
        "exported_at": datetime.now(timezone.utc).isoformat(),
        "total_quizzes": len(quizzes),
        "rankings": rankings,
        "quizzes": quizzes,
    }

    print(json.dumps(output, indent=2, ensure_ascii=False))

    # Summary
    print(f"\n{'=' * 50}", file=sys.stderr)
    print(f"Total: {len(quizzes)} quizzes", file=sys.stderr)
    print("Rankings:", file=sys.stderr)
    for name, s in rankings.items():
        print(f"  {name}: {s['correct']}/{s['total']} ({s['accuracy']}%)", file=sys.stderr)

    await client.disconnect()


if __name__ == "__main__":
    asyncio.run(main())
