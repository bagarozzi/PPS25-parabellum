# Product Backlog

**Vision**: l'obbiettivo del progetto è creare un gioco di combattimento tramite funzioni matematiche.

### Features obbligatorie
- Supporto al gioco 1v1 tra giocatori dallo stesso computer
- Generazione della mappa: ostacoli, posizionamento dei giocatori, verifica della mappa
- Movimento dei proiettili
- Interfaccia grafica: casella per funzione, tasto per sparare

### Features opzionali
- Giocatori del computer
- Tutorial inziale/zona di allenamento
- Potenziamenti

### Tasks
Most important first.
| Task ID | Task Description | Assignee | When it's done | Done | First planned in |
| :--- | :--- | :--- | :--- | :---: | :---: |
| 05 | Write [**Processo**](../docs/1-processo.md) section of the report | Bagattoni | . | yes | [**Sprint 1**](./sprint_1_backlog.md) |
| 06 | Write the Domain modeling sections of [**Requirements**](../docs/2-requisiti.md) | Venturini | . | no | [**Sprint 1**](./sprint_1_backlog.md) |
| 37 | Write [**Design**](../docs/3-design.md) | unassigned | this | no | [**Sprint 5**](./sprint_5_backlog.md) |
| 38 | Write [**Detailed design**](../docs/4-dettaglio.md) | unassigned | this | no | [**Sprint 5**](./sprint_5_backlog.md) |
| 39 | Fix: Powerups not disappearing when shot | unassigned | this | no | [**Sprint 5**](./sprint_5_backlog.md) |
| 40 | Powerup: bug fix of Random | unassigned | this | no | [**Sprint 5**](./sprint_5_backlog.md) |
| 40b | Powerup: bug fix of Burded | unassigned | this | no | [**Sprint 5**](./sprint_5_backlog.md) |
| 40c | Powerup: bug fix of Ricochet | unassigned | this | yes | [**Sprint 5**](./sprint_5_backlog.md) |
| 41 | Parser: move parsing to controller | Bagattoni | when GameState receives a type Function from the Controller | yes | [**Sprint 5**](./sprint_5_backlog.md) |
| 41b | Parser: return a readable error | Bagattoni | when a custom error is returned by the parser | yes | [**Sprint 5**](./sprint_5_backlog.md) |
| 41c | Parser: return error to user | Bagattoni | when the user receives an understandable error if a malformed function is shot | yes | [**Sprint 5**](./sprint_5_backlog.md) |
| 42 | ImpactEffect refactoring | Venturini | this | no | [**Sprint 5**](./sprint_5_backlog.md) | 
| 43 | MapGenerator refactoring | Sbaraccani | complete overhaul of MapGenerator | no | [**Sprint 5**](./sprint_5_backlog.md) |
| 44 | Shooting range in the model | unassigned | when the shooting range is available as an alternative initialization of GameState | no | [**Sprint 5**](./sprint_5_backlog.md) |
| 45 | Shooting range in View | unassigned | when the user can select the Shooting range mode | no | [**Sprint 5**](./sprint_5_backlog.md) |

___
___

[Torna alla Home](../index.md)
