# Sprint Backlog - Sprint 4

**Sprint goal**: adding powerups, boosting map generation and global refactoring

**Duration/Deadline**: 31/08/2026

### Sprint planning meeting (26/08/2026)
Ora che il gioco ha preso la sua forma finale possiamo concentrarci sulle piccole migliorie, refactoring
e feature opzionali. In questo sprint faremo: 
- Check della generazione della mappa tramite Prolog
- Refactoring della generazione della mappa per dargli un aspetto funzionale: poligoni regolari (e loro dimensioni), generazione
delle posizioni dei soldati in maniera **sensata**
- Abbellimenti alla GUI: i soldati ancora non scompaiono quando muoiono, migliorare il meccanismo di render della GUI, 
il soldato che deve sparare viene evidenziato, 
- Refactoring del meccanismo di inizializzazione del GameState
- Configurazione globale dei parametri di gioco (*tipo ereditati tramite given*)
- Parser: logaritmi e valori assoluti
- Refactor ed ordinamento di Shape

- I power-up: ostacoli che donano un effetto al giocatore (che duri una serie di tick o no...) positivi e negativi
    - Piercing: il prossimo colpo sparato passa attraverso agli ostacoli
    - Ricochet: il prossimo colpo sparato rimbalza X volte sui bounds
    - Random: la funzione del prossimo colpo viene moltiplicata per un numero tra -10 e 10
    - Burden: il prossimo colpo avrà una zavorra che lo trascina verso il basso (f(x) - kx^2)
- Ogni powerup ha un logo riconoscibile

### Tasks to be done
| Task ID | Task Description | Assignee | When it's done | Done |
| :--- | :--- | :--- | :--- | :---: |
| 05 | Write [**Processo**](../docs/1-processo.md) section of the report | Bagattoni | . | no |
| 06 | Write the Domain modeling sections of [**Requirements**](../docs/2-requisiti.md) | Venturini | . | no |
| 25 | Map generation using Prolog | Sbaraccani | when the map is correctly check before being generated | yes |
| 26a | Map generation refactoring | Sbaraccani | when functions are short and weird polygons are not generated | no |
| 26b | Soldier position generation | Sbaraccani | when soldiers are not in the same "zone" of the map | yes |
| 28a | GUI work: soldiers die | Sbaraccani | soldiers disappear when dead | no |
| 28b | GUI work: render | Sbaraccani | when the explosions are not overlapped on players and cartesian axes | no |
| 28c | GUI work: soldier highlight | Sbaraccani | when the soldiers who's shooting is highlighted in some way | no |
| 29 | GameState update refactoring | Bagattoni | . | no |
| 31 | Global Game paramethers | Sbaraccani-Bagattoni | When compile-time paramethers are changed through a unique file | no |
| 32 | Parser: adding logarithm and abs() functions | Bagattoni | this | yes |
| 34 | Shape refactoring | Venturini | this | yes |
| 35a | Powerups: Ricochet, Piercing, Random, Burden | Venturini | this | yes |
| 35b | Powerups in View | Sbaraccani-Bagattoni | when each powerup is recognizable and appears on the side of the screen for the player who has it | no |

Keep in mind: ***always merge working code***

### Sprint review meeting (31/08/2026)
___
___

[**&larr; Torna al Product Backlog**](./product_backlog.md) | [**Torna alla Home**](../index.md)
