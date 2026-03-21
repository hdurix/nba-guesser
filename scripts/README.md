# Scripts utilitaires

Scripts Python pour récupérer et analyser l'historique des quiz Telegram.

## Setup

```bash
sudo apt install python3.12-venv  # si nécessaire
python3 -m venv scripts/.venv
source scripts/.venv/bin/activate
pip install -r scripts/requirements.txt
```

Créer `scripts/.env` avec :

```
TELEGRAM_API_ID=...
TELEGRAM_API_HASH=...
TELEGRAM_CHAT_ID=...
TELEGRAM_PHONE=+33...
```

Credentials API à récupérer sur https://my.telegram.org ("API development tools").

## Scripts

### `fetch_poll_history.py`

Récupère l'historique complet des quiz via le Client API Telegram (MTProto).
Écrit les votes individuels (qui a voté quoi) dans `results.json`.

```bash
python3 scripts/fetch_poll_history.py
```

Ctrl+C sauvegarde les données collectées. La première exécution demande une authentification interactive (numéro + code).

### `send_stats.py`

Génère des statistiques à partir de `results.json` et les envoie dans les Saved Messages Telegram.

```bash
python3 scripts/send_stats.py           # preview
python3 scripts/send_stats.py --send    # envoi
```
