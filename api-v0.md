Base url: `/api/v0` // v0 is only for the MVP

# GET /status

either get status of current v0 lobby if there is one, or get last status of the previous v0 lobby if there is none atm.
if there is no current v0Lobby, returns default status but does NOT create new lobby.

playerXSubmitted will only be true if code for player X has been submitted

result will only be valid when gameState is `FINISHED`

playerXSubmitted will switch back to false once the gameState switches to `FINISHED`

## responses:
### 200:
```
{
    "playerASubmitted": boolean,
    "playerBSubmitted": boolean,
    "gameState": [NOT_STARTED, RUNNING, FINISHED], // Enum
    "result": {
        "winner": [A, B, UNDECIDED], // Enum
    }
}
```

# POST /code/{player}
stores the data in current v0Lobby, creates that lobby if there is no current v0Lobby

## body:
```text
the code in plain text
```

## responses:
## 201:
The game will automatically start once the second player has submitted.

## 400:
Game is in `RUNNING` state or a player still has to submit code.
The message field contains an explanation of what went wrong.
```
{
    "message": String,
}
```


