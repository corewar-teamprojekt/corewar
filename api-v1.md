Base url: `/api/v1`

# apiV1 - Parallel games / Lobby system
NOTE: We kicked of  the lobby system by always initializing a "default" lobby
A goal of these endpoints is to **GET RID OF** the default lobby.
Keep that in mind when working on this system.

# POST /lobby

Endpoint to create a new lobby.
Response with an id that identifies the lobby uniquely.

## body:
```json
{
    "playerName": String,
}
```

The body must contain the desired playerName of the player creating the lobby.

## responses:
### 201:
```json
{
    "lobbyId": String,
}
```

# POST /lobby/{lobbyid}/join

Endpoint to join an existing lobby.

## body:
```json
{
    "playername": String,
}
```

The body must contain the desired playerName

## responses:
### 200:
No special response body, join was accepted.

### 404:
There is no lobby with that id.

### 409:
Someone already joined as that player. The slot is locked and the join operation is aborted.


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
    },
    "visualizationData": [ 
        {
            "playerId": One of [A, B],
            "programCounterBefore": number,
            "programCounterAfter": number,
            "programCountersOfOtherProcesses": [number],
            "memoryReads": [number], 
            "memoryWrites": [number],
            "processDied": boolean,
        }
    ]
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

# GET /lobby

Gets a list of all existing lobbies and their details.

playersJoined contains a list of all playerNames that joined the lobby. Joined means joined, not code submitted!

## responses:
### 200:

```json
{
  "lobbies": [
    {
      "id": number,
      "playersJoined": [string],
      "gameState": One of [NOT_STARTED, RUNNING, FINISHED],
    },
    ...
  ]
}
```

# POST /redcode/compile/errors
Retrieves the errors encountered during the compilation of the redcode.
If no errors were encountered during compilation, the provided error array is empty.

## Request Body:
```json
{
    "code": string,
}
```

## Responses:
### 200:
```json
{
    "errors": [
        {
            "line": number,
            "message": string,
            "columnStart": number,
            "columnEnd": number,
        },
    ]
}
```

# GET /lobby/{lobbyId}/settings
Gets the settings of a lobby.

## responses:
### 200:
```json
{
    "settings:": {
        "interpreterSettings": {
            "coreSize": number,
            "instructionLimit": number,
            "initialInstruction": String,
            "maximumTicks": number,
            "maximumProcessesPerPlayer": number,
            "readDistance": number,
            "writeDistance": number,
            "minimumSeparation": number,
            "separation": number,
            "randomSeparation": boolean,
        }
    }
}
```
### 404: Lobby does not exist
