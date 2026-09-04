# Sprint Backlog - Sprint 4

**Sprint goal**: refactoring and finishing touch in all the classes, bug fixing and documentation

**Duration/Deadline**: 06/09/2026

### Sprint planning meeting (01/09/2026)
Per i successivi due sprint si punta a non aggiungere nuove feature ma piuttosto a rifinire e rifattorizzare
i componenti esistenti per allinearsi ai principi insegnati nel corso ed utilizzare le funzionalità avanzate del linguaggio.

Le uniche feature che vengono aggiunte sono: 
- End game: schermata finale per vincitore e perdente
- Campo di tiro: schermata in cui un giocatore può testare le sue abilità matematiche da solo

Il campo di tiro ci permetterà di verificare anche la flessibilità dell'architettura del gioco nell'utilizzare
i componenti attuali dando loro un comportamento diverso per svolgere il compito.

Altri miglioramenti possono essere:
- ***Bug fixing di alcuni power-up***
- ***Spostamento del parsing*** delle funzioni da stringhe ad elemento del dominio (Function) a livello del Controller
per fornire a GameState solo tipi puri del gioco. In più il Parser deve ritornare un errore leggibile da poter trasmettere
all'utente
- ***Refactoring di ImpactEffect*** e separazione dei suoi contenuti in files e package separati. Creazione di ***companion objects***
per la creazione agevolata di Impact, ImpactEvents ed ImpactEffect
- Unit testing di altri componenti

Per quanto riguarda la documentazione vengono scritte le seguenti sezioni: 
- Descrizione del processo di sviluppo
- Completamento dei Requisiti di sviluppo
- Design
- Design di dettaglio

### Tasks to be done
| Task ID | Task Description | Assignee | When it's done | Done |
| :--- | :--- | :--- | :--- | :---: |
| 05 | Write [**Processo**](../docs/1-processo.md) section of the report | Bagattoni | . | yes |
| 06 | Write the Domain modeling sections of [**Requirements**](../docs/2-requisiti.md) | Venturini | . | no |
| 37 | Write [**Design**](../docs/3-design.md) | unassigned | this | no |
| 38 | Write [**Detailed design**](../docs/4-dettaglio.md) | unassigned | this | no |
| 39 | Fix: Powerups not disappearing when shot | unassigned | this | no |
| 40 | Powerup: bug fix of Random | unassigned | this | yes |
| 40b | Powerup: bug fix of Burded | Bagattoni | this | yes |
| 40c | Powerup: bug fix of Ricochet | Bagattoni | this | yes |
| 41 | Parser: move parsing to controller | Bagattoni | when GameState receives a type Function from the Controller | yes |
| 41b | Parser: return a readable error | Bagattoni | when a custom error is returned by the parser | yes |
| 41c | Parser: return error to user | Bagattoni | when the user receives an understandable error if a malformed function is shot | yes |
| 42 | ImpactEffect refactoring | Venturini | this | no |
| 43 | MapGenerator refactoring | Sbaraccani | complete overhaul of MapGenerator | no |
| 44 | Shooting range in the model | unassigned | when the shooting range is available as an alternative initialization of GameState | no |
| 45 | Shooting range in View | unassigned | when the user can select the Shooting range mode | no |


Keep in mind: ***always merge working code***

### Sprint review meeting (06/08/2026)

___
___

[**&larr; Torna al Product Backlog**](./product_backlog.md) | [**Torna alla Home**](../index.md)
