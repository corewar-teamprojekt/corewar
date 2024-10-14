Base url: `/api/v0` // v0 is only for the MVP

# GET /status

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


