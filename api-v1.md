Base url: `/api/v1`

# POST /lobby

Endpoint to create a new lobby.
Response with an id that identifies the lobby uniquely.

## responses:
### 201:
```json
{
    "lobbyId": String,
}
```


# GET /lobby/{lobbyId}/status

Gets the status of a lobby.

playerXSubmitted will only be true if code for player X has been submitted

Result will only be valid when gameState is `FINISHED`, beforehand it is undefined.

playerXSubmitted will switch back to false once the gameState switches to `FINISHED`

## responses:
### 200:
```json
{
    "playerASubmitted": boolean,
    "playerBSubmitted": boolean,
    "gameState": One of [NOT_STARTED, RUNNING, FINISHED], 
    "result": {
        "winner": One of [A, B, DRAW], 
    }
}
```

# POST /lobby/{lobbyId}/code/{player}
Player path variable has to be one of [A, B]

## body:
```json
{
    "code": String,
}
```

The body must contain the code that is to be submitted

## responses:
## 201
## 400: An incorrect player has been specified in the path/request.
```json
{
    "message": String,
}
```
## 404: Lobby does not exist


# GET /lobby/{lobbyId}/code/{player}
Player path variable has to be one of [A, B]


## responses:
## 200:
```json
{
    "code": String,
}
```
## 400: An incorrect player has been specified in the path/request.
```json
{
    "message": String,
}
```
## 404: No code has been submitted for the specified player yet or the lobby does not exist

