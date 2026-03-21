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
    short_names = [p.split()[0][:3] for p in players]

    lines2 = ["📈 **ÉVOLUTION MENSUELLE**", "", "```"]
    header = "     " + " ".join(f"{n:>5}" for n in short_names)
    lines2.append(header)
    for month in sorted(monthly.keys()):
        m = month[5:] + "/" + month[2:4]
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

    # Message 4: Analysis
    players_list = list(rankings.items())
    sorted_months = sorted(monthly.keys())

    # Current streaks
    streaks = {}
    for name, _ in players_list:
        streak = 0
        for q in reversed(quizzes):
            vote = next((v for v in q["votes"] if v["user_name"] == name), None)
            if not vote:
                break
            if vote["option_index"] == q["correct_option_index"]:
                streak += 1
            else:
                break
        streaks[name] = streak

    # Best month per player
    best_months = {}
    for name, _ in players_list:
        best = None
        for m in sorted_months:
            s = monthly[m][name]
            if s["total"] >= 10:
                pct = s["correct"] / s["total"] * 100
                if best is None or pct > best[1]:
                    best = (m[5:] + "/" + m[2:4], pct)
        best_months[name] = best

    lines4 = ["💬 **ANALYSE**", ""]

    # Top player
    p1_name, p1 = players_list[0]
    lines4.append(f"👑 **{p1_name}** domine avec {p1['accuracy']}% de réussite "
                   f"et {round(p1['total']/len(quizzes)*100, 1)}% de participation.")
    if streaks[p1_name] >= 3:
        lines4.append(f"   En feu : {streaks[p1_name]} bonnes réponses d'affilée !")

    # Others
    p2_name, p2 = players_list[1]
    bm = best_months.get(p2_name)
    bm_str = f" Meilleur mois : {bm[0]} ({bm[1]:.0f}%)." if bm else ""
    lines4.append(f"💪 **{p2_name}** solide à {p2['accuracy']}%.{bm_str}")

    p3_name, p3 = players_list[2]
    # Check progression
    early = [m for m in sorted_months[:3]]
    late = [m for m in sorted_months[-3:]]
    early_avg = sum(monthly[m][p3_name]["correct"] / max(monthly[m][p3_name]["total"], 1) for m in early if monthly[m][p3_name]["total"] > 0) / max(sum(1 for m in early if monthly[m][p3_name]["total"] > 0), 1) * 100
    late_avg = sum(monthly[m][p3_name]["correct"] / max(monthly[m][p3_name]["total"], 1) for m in late if monthly[m][p3_name]["total"] > 0) / max(sum(1 for m in late if monthly[m][p3_name]["total"] > 0), 1) * 100
    if late_avg > early_avg + 5:
        lines4.append(f"📈 **{p3_name}** en progression : de ~{early_avg:.0f}% à ~{late_avg:.0f}% !")
    else:
        lines4.append(f"🎯 **{p3_name}** régulier à {p3['accuracy']}%.")

    if len(players_list) > 3:
        p4_name, p4 = players_list[3]
        part = round(p4["total"] / len(quizzes) * 100, 1)
        lines4.append(f"🫡 **{p4_name}** : {part}% de participation, {p4['accuracy']}% de réussite. Courageux !")

    # NBA fun facts
    lines4.append("")
    top3 = [n for n, _ in easiest[:3]]
    bot3 = [n for n, _ in hardest[:3]]
    lines4.append(f"Tout le monde reconnaît **{', '.join(top3[:2])}** ou **{top3[2]}**.")
    lines4.append(f"Personne ne reconnaît **{', '.join(bot3[:2])}** ou **{bot3[2]}**.")

    msg4 = "\n".join(lines4)

    return [msg1, msg2, msg3, msg4]


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
