# Parabellum
The project for Development and Programming Paradigms for a.y. 2025-2026

## Documentation
The documentation for this project is available on [this page](https://bagarozzi.github.io/PPS25-parabellum/), here you can view both the documentation
and the development process.

## Starting the application
### Prerequisites
- Java 21
- Scala 3.3
- sbt 2.0.1

### From the releases
1. Download a release from the [Release page](https://github.com/bagarozzi/PPS25-parabellum/releases)
2. Start the ```.jar``` file

### Compiling from source
1. Clone the repository
2. Run ```sbt clean client/assembly```, you will find the executable ```Parabellum.jar``` inside the ```target``` directory

### Running from sbt
- ```sbt client/run``` starts the game
- ```sbt test``` runs all the tests
- ```sbt server/run``` starts the (work in progress) server: two nodes of a local Pekko cluster in the same JVM. Pass the ports as arguments for more nodes, e.g. ```sbt "server/run 2551 2552 2553"```

## Project structure
The build is split in three sbt modules:

| Module | Content |
| --- | --- |
| `core` | Game model and rules (`model`, `util`, `GameState`, `TeamManager`) the `snapshot` of a match, plain data that a client can draw, and the `protocol`, the JSON messages exchanged between a client and the server. It has no user interface dependencies, so it can run headless |
| `client` | ScalaFX user interface, the session that plays a match on this machine and the entry point of the application |
| `server` | Headless server (work in progress): the matches are entities of an Apache Pekko cluster and use only `core`; the gateway lets the clients play them over HTTP and WebSocket |

## Playing online
1. Start the server: ```sbt server/run``` (it opens a gateway on http://127.0.0.1:8080 and one on 8081).
2. Start the game, choose **Play online**, write the address of the server (```127.0.0.1:8080```), a username and a password, and choose **Register**. Every player needs an account.
3. One player chooses **Create and join**, typing the name of a match and the usernames of the players, for example ```anna,luca```, and plays as the first one.
4. The other players start their own game, choose **Play online**, write the name of the same match, their username and password, and choose **Join**.

Usernames have from 3 to 32 lowercase letters, digits, `-` or `_` (uppercase letters are accepted and lowercased); passwords have from 8 to 128 characters.

### Accounts and the database
The accounts are entities of the cluster that remember their state by event sourcing: what happens is written to a database, and an account survives the restart of the server (and, in a cluster, the loss of a node).
- By default the server uses an **H2 database in a file** (```server/data/parabellum*```; add ```data/``` to ```.gitignore```). Several processes on the same machine can share it. H2 is meant for development.
- A real deployment uses **PostgreSQL**: set ```DB_PROFILE=slick.jdbc.PostgresProfile$```, ```DB_DRIVER=org.postgresql.Driver```, ```DB_URL```, ```DB_USER``` and ```DB_PASSWORD```.
- The tokens that prove who a user is are signed with ```TOKEN_SECRET```: **set it** (the same value on every node) before using the server for real, otherwise a public development secret is used and the server says so in the log.
- Passwords are never stored: only a salted PBKDF2 hash. The tokens and passwords travel in the clear unless the connection is protected (HTTPS is not set up yet).
