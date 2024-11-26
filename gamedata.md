# Game data and visualization process
## Purpose
This document describes a high level overview of the collection of data about the game
and how it should be exposed.


## What is the objective?:
We want to finely-grained visualize what happens during a game, down to the memory reads and
writes that were performed by an instruction.


## What do we need for it?:
We need to collect the following data in the game:
- The player that is currently running something
- The program counter of the currently executing process / The process that will start executing next
- The program counters of the players other programs
- The memory reads that were performed while running the instruction
- The memory writes that were performed while running the instruction
- Whether the process died


## Rough Process:
#### Frontend / API
- Player(s) join the game and submit code
- The round / game is started

#### Interpreter
- The interpreter is provided with the data about players (Name, Code, etc.)
- The interpreter runs the game.
  - The game data is collected by the interpreter (duh).
    - The Setup (placing of the instructions etc.) will also be included.
    - The game data will be exposed as a sequential list of game data objects, each containing the data 
      about a single execution of a process, ordered by the order of execution.
  - The interpreter exposes some kind of way to get the game data after a / the round is done.

#### API
- After the interpreter is done simulating, the API retrieves the game data from the interpreter
  via the method exposed by the interpreter.
- The API exposes some kind of way to retrieve the data about a / the round.

#### Frontend
- The Frontend requests that data from the API and visualizes it.


## Extras
- The frontend aims to achieve ~30 TPS for the visualization, but it remains to be seen how
  viable that is in regard to speed and or practicality (how fast is too fast to view etc.)

