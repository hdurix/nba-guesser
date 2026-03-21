# NBA API integration

The `NbaApiPlayers` adapter fetches player data from an external JSON source and maps it to domain `Player` objects.

## Data source

Players are loaded from a static JSON hosted on GitHub Gist, containing NBA player data in the `resultSets[0].rowSet` format.

Each row is a `List<String>` where:

| Index | Field         | Example   |
| ----- | ------------- | --------- |
| 0     | Player ID     | `1629651` |
| 1     | Last name     | `Claxton` |
| 2     | First name    | `Nic`     |
| 19    | Roster status | `1.0`     |

Only **active players** (roster status = `1.0`) are included.

## Mapping chain

```
JSON response
  → ApiPlayersWrapper (root: resultSets)
    → ApiResultSet (rowSet: Collection<List<String>>)
      → Player (domain record)
```

1. `ApiPlayersWrapper.toPlayers()` delegates to the first result set
2. `ApiResultSet.toPlayers()` filters active players and maps each row to a `Player`
3. The `Player` record validates that `firstName` and `lastName` are not blank

## Player headshots

Player photos are loaded from the NBA CDN using the player ID:

```
https://cdn.nba.com/headshots/nba/latest/1040x760/{playerId}.png
```

This URL is used by the Telegram adapter when sending the quiz photo.
