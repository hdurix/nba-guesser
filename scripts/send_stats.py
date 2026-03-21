#!/usr/bin/env python3
"""Send NBA Guesser stats to Telegram chat."""

import asyncio
import json
import os
import sys
from collections import defaultdict

from telethon import TelegramClient

SESSION_FILE = os.path.join(os.path.dirname(__file__), "nba_guesser_session")
RESULTS_FILE = os.path.join(os.path.dirname(__file__), "..", "results.json")


def build_messages(data):
    quizzes = data["quizzes"]
    rankings = data["rankings"]
    first = quizzes[0]["date"][:10]
    last = quizzes[-1]["date"][:10]

    # Message 1: Highlights
    medals = ["🥇", "🥈", "🥉", "4.", "5."]
    lines = [
        "🏀 **NBA GUESSER — CLASSEMENT ALL-TIME** 🏀",
        "",
        f"📊 {len(quizzes)} quizzes du {first} au {last}",
        "",
    ]
    for i, (name, s) in enumerate(rankings.items()):
        bar = "▓" * int(s["accuracy"] / 5) + "░" * (20 - int(s["accuracy"] / 5))
        lines.append(f"{medals[i]} **{name}** — {s['correct']}/{s['total']} ({s['accuracy']}%)")
        lines.append(f"    {bar}")
    lines.append("")

    # Participation
    lines.append("📋 **Participation**")
    for name, s in rankings.items():
        pct = round(s["total"] / len(quizzes) * 100, 1)
        lines.append(f"  • {name}: {s['total']}/{len(quizzes)} ({pct}%)")

    msg1 = "\n".join(lines)

    # Message 2: Monthly evolution
    monthly = defaultdict(lambda: defaultdict(lambda: {"correct": 0, "total": 0}))
    for q in quizzes:
        month = q["date"][:7]
        for v in q["votes"]:
            name = v["user_name"] or str(v["user_id"])
            monthly[month][name]["total"] += 1
            if v["option_index"] == q["correct_option_index"]:
                monthly[month][name]["correct"] += 1

    players = list(rankings.keys())
    initials = [p[0] for p in players]
    # Handle duplicate initials
    seen = {}
    for i, ini in enumerate(initials):
        if ini in seen:
            initials[seen[ini]] = players[seen[ini]].split()[0][:3]
            initials[i] = players[i].split()[0][:3]
        seen[ini] = i

    lines2 = ["📈 **ÉVOLUTION MENSUELLE**", "", "```"]
    header = "     " + " ".join(f"{n:>5}" for n in initials)
    lines2.append(header)
    for month in sorted(monthly.keys()):
        m = month[2:4] + "/" + month[5:]
        vals = []
        for p in players:
            s = monthly[month][p]
            if s["total"] > 0:
                vals.append(f"{s['correct']/s['total']*100:>4.0f}%")
            else:
                vals.append(f"{'—':>5}")
        lines2.append(f"{m:<5}" + " ".join(vals))
    lines2.append("```")

    msg2 = "\n".join(lines2)

    # Message 3: NBA players recognition
    nba_stats = defaultdict(lambda: {"correct": 0, "total": 0})
    for q in quizzes:
        ans = q["correct_answer"]
        if ans == "?":
            continue
        correct_votes = sum(1 for v in q["votes"] if v["option_index"] == q["correct_option_index"])
        total_votes = len(q["votes"])
        nba_stats[ans]["correct"] += correct_votes
        nba_stats[ans]["total"] += total_votes

    filtered = [(n, s) for n, s in nba_stats.items() if s["total"] >= 2]
    easiest = sorted(filtered, key=lambda x: x[1]["correct"] / x[1]["total"], reverse=True)[:10]
    hardest = sorted(filtered, key=lambda x: x[1]["correct"] / x[1]["total"])[:10]

    lines3 = ["🎯 **JOUEURS NBA**", "", "✅ **Les plus reconnus**"]
    for n, s in easiest:
        pct = s["correct"] / s["total"] * 100
        lines3.append(f"  {n} — {s['correct']}/{s['total']} ({pct:.0f}%)")
    lines3.append("")
    lines3.append("❌ **Les moins reconnus**")
    for n, s in hardest:
        pct = s["correct"] / s["total"] * 100
        lines3.append(f"  {n} — {s['correct']}/{s['total']} ({pct:.0f}%)")

    msg3 = "\n".join(lines3)

    return [msg1, msg2, msg3]


async def main():
    api_id = os.environ.get("TELEGRAM_API_ID")
    api_hash = os.environ.get("TELEGRAM_API_HASH")
    chat_id = os.environ.get("TELEGRAM_CHAT_ID")

    if not all([api_id, api_hash, chat_id]):
        print("Error: set TELEGRAM_API_ID, TELEGRAM_API_HASH, TELEGRAM_CHAT_ID", file=sys.stderr)
        sys.exit(1)

    with open(RESULTS_FILE) as f:
        data = json.load(f)

    messages = build_messages(data)

    # Preview
    for i, msg in enumerate(messages, 1):
        print(f"=== Message {i} ({len(msg)} chars) ===", file=sys.stderr)
        print(msg, file=sys.stderr)
        print(file=sys.stderr)

    if "--send" not in sys.argv:
        print("Preview mode. Add --send to actually send to Telegram.", file=sys.stderr)
        return

    client = TelegramClient(SESSION_FILE, int(api_id), api_hash)
    await client.start()
    me = await client.get_me()

    for i, msg in enumerate(messages, 1):
        await client.send_message(me, msg, parse_mode="md")
        print(f"Message {i} sent!", file=sys.stderr)
        await asyncio.sleep(1)

    await client.disconnect()
    print("Done!", file=sys.stderr)


if __name__ == "__main__":
    asyncio.run(main())
