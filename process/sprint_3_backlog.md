# Sprint Backlog - Sprint 3

**Sprint goal**: aggiunta di 2 giocatori dallo stesso terminale, ostacoli e refactoring generale

**Duration/Deadline**: 16/08/2026

### Sprint planning meeting (17/08/2026)
Si punta ad aggiungere complessità al gioco:
- Ostacoli e generazione della mappa triviale ma sensata
- Abbozzare mappa con Prolog
- Implementazione di impact effect
- Refactoring e testing delle collisioni
- Possibilità per l'utente di inserire funzioni matematiche complesse (quindi il parsing di tali funzioni)
- Due giocatori giocano uno contro l'altro dallo stesso computer
- Soldati: ogni giocatore ha più soldati da cui può sparare, ad ogni turno un giocatore sparerà con un soldato diverso
- I proiettili lasciano dietro di se una linea (tratteggiata o continua) della loro traiettoria [tasked]
- Aggiungere test di unità ai moduli già (o parzialmente) completi

Per questo sprint non vengono assegnate le task, bensì ogni membro seleziona le task che vuole eseguire e appunta il suo nome
nella riga così che gli altri vedano chi (se) la sta già facendo. 

### Tasks to be done
| Task ID | Task Description | Assignee | When it's done | Done |
| :--- | :--- | :--- | :--- | :---: |
| 05 | Write [**Processo**](../docs/1-processo.md) section of the report | Bagattoni | . |
| 06 | Write the Domain modeling sections of [**Requirements**](../docs/2-requisiti.md) | Venturini | . | no |
| 12 | Line trail in ProjectileView | Sbaraccani | when projectiles leave a trail behind them while moving | yes |
| 15 | Smaller bullet | Sbaraccani | when the bullet is smaller from the View | yes |
| 13 | Obstacles basic implementation: rectangles and circles and inserted in GameState | Sbaraccani | when obstacles are implemented and tested | yes |
| 14 | Obstacles in View | Sbaraccani | when obstacles are correctly displayed | yes |
| 16 | Map generation: random placement of players and obstacles + Unit testing |  | when a fresh random is generated at each run | no |
| 17 | Implementation of ImpactEffect: obstacles have an impact effect when shot |  |  | no |
| 18a | New Functions in View | Bagattoni | when the user can type entire functions from the View | no |
| 18b | Parsing of New Functions | Bagattoni | when New Functions from the View are correctly parsed into Trajectories | no |
| 19 | Two players have names |  | when each player can choose his/her name and is displayed at their side of the screen | no |
| 20 | Soldiers |  | when each player has more than one soldier on the field and the turns are correctly managed | no |
| 21 | Unit testing of: Map |  | ... | no |
| 22 | Unit testing of: ImpactEffect |  | ... | no |
| 23 | Unit testing of: Trajectory and Projectile (_after task 18_) |  | ... | no |
| 24 | Unit testing of: CollisionDetector |  | ... | no |

To ease the workflow the tasks may be divided in the following groups (inside each group, ***task order matters***), and did in the following order:
1. **Adding players and soldiers**: keep the current View and architecture, just add soldiers, players and turn management
2. **Obstacles**: tasks **[13]**, **[14]**, **[17]** and **[16]** only add Obstacles to the Model and displays them correctly
3. **Functions**: tasks **[18]** (first a, then b) need to change Projectile and Trajectory implementation, also adding a component to parse functions and generate a general trajectory with (Double => Double) function.

**Independent tasks**: **[12]** - Line trail in Projectile, **[15]** - Smaller bullet 

Keep in mind: ***always merge working code***


### Sprint review meeting (24/08/2026)

___
___

[**&larr; Torna al Product Backlog**](./product_backlog.md) | [**Torna alla Home**](../index.md)
