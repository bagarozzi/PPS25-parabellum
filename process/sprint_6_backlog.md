# Sprint Backlog - Sprint 6

**Sprint goal**: refactoring and finishing touch in all the classes, bug fixing and documentation

**Duration/Deadline**: 13/09/2026

### Sprint planning meeting (07/09/2026)
L'obbiettivo di questo sprint è: 
- Completare la documentazione prima della consegna prevista il 13 settembre
- Rifattorizzare le classi rimanenti, pulendo codice e evitando ripetizioni
- Aggiungere commenti scaladoc a tutta la codebase
- Aggiungere unit-testing ai componenti che possono averla
- Aggiungere qualche miglioria grafica: ad esempio, quando viene ucciso l'ultimo soldato, la partita finisce subito senza neanche
far finire il proiettile
- Rimozione dei magic number e creazione di companion object per inizializzare le varie entità del gioco

Questo si traduce in: 
- Impact effect: package impact + separare impacteffect, impactevent e Impact in file separati, aggiungere commenti a tutto, potenazialmente togliere i sealed trait
+ refactoring di Companion object (tante ripetizioni di codice)
- Obstacle: rinominare i metodi del companion object così da essere più verbosi (*fromVertices, regPoly, irregPoly*)
- Projectile: refactoring del companion object
- Trajectory: utilizzare l'enum Direction ovunque
- Shape: tolgiere il sealed trait e spostare le case class che estendono fuori in altri file
- BoundingBox: commenti
- GUI: togliere ripetizione di codice dal render, sistemare le eccezioni

PER OGNI TASK: rimuovere le import inutili ed aggiungere commenti

Per quanto riguarda la documentazione, deve essere organizzata come segue:
Processo di sviluppo adottato (modalità di divisione in itinere dei task, meeting/interazioni pianificate, modalità di revisione in itinere dei task, scelta degli strumenti di test/build/continuous integration) 
- Requirement specification: 
    - 1 requisiti di business
    - 2 modello di dominio, 
    - 3 requisiti funzionali [ 3.1) utente, e 3.2) di sistema ], 4) requisiti non funzionali, 5) requisiti di implementazione)
- Design architetturale (architettura complessiva, descrizione di pattern architetturali usati, eventuali componenti del sistema distribuito, scelte tecnologiche cruciali ai fini architetturali -- corredato da pochi ma efficaci diagrammi)
- Design di dettaglio (scelte rilevanti di design, pattern di progettazione, organizzazione del codice -- corredato da pochi ma efficaci diagrammi)
- Implementazione (per ogni studente, una sotto-sezione descrittiva di cosa fatto/co-fatto e con chi, e descrizione di aspetti implementativi importanti non già presenti nel design, come ad esempio relativamente all'uso di meccanismi avanzati di Scala)
- Testing (tecnologie usate, grado di copertura, metodologia usata, esempi rilevanti, altri elementi utili)
- Restrospettiva (descrizione finale dettagliata dell'andamento dello sviluppo, del backlog, delle iterazioni; commenti finali)

ed a noi cosa manca?
- Da [**Requirements**](../docs/2-requisiti.md): versione aggiornata del diagramma del dominio, correzione e eventuali aggiunte pervenute in corso d'opera
- Da [**Design**](../docs/3-design.md): scrivere tutto, aggiunta e spiegazione di un diagramma architetturale decente. Spiegare l'architettura che abbiamo pensato a livello astratto
- Da [**Detailed design**](../docs/4-dettaglio.md): scrivere tutto. Ogni membro del gruppo scrive riguardo alla sua macro-sezione, aggiungendo qualche diagramma a testa (1-2) parla di come ha diviso il codice
e di come l'ha pensato. Si fa un introduzione per tutti su come, assieme, abbiamo pensato l'interazione delle entità.
- Da [**Implementation**](../docs/5-implementazione.md): per ognuno, si descrivono gli aspetti implementativi, soprattutto le funzionalità avanzate del linguaggio. Includendo, se è successo, le parti condivise e menzionando con chi le abbiamo fatte.
- Da [**Testing**](../docs/6-testing.md): si descrive il testing
- Da [**Retrospettiva**](../docs/7-retrospettiva.md): descrizione finale dettagliata dell'andamento dello sviluppo, del backlog, delle iterazioni; commenti finali

### Tasks to be done
| Task ID | Task Description | Assignee | When it's done | Done |
| :--- | :--- | :--- | :--- | :---: |
| 06 | Docs - correction of [**Requirements**](../docs/2-requisiti.md) | Venturini | . | no |
| 37 | Docs - [**Design**](../docs/3-design.md) | unassigned | this | no |
| 38 | Docs - [**Detailed design**](../docs/4-dettaglio.md) | unassigned | each member writes its own section | no |
| 42 | ImpactEffect refactoring | Venturini | separation in multiple files (Impact, ImpactEvent, ImpactEffect), Refactoring of ImpactEffect | no |
| 47 | Obstacle refactoring | Sbaraccani | Rename the companion object's methods | yes |
| 48 | Shape refactoring | Venturini | Separate the classes in multiple files, create companion object's method for easy creation of Explosions | no |
| 49 | Projectile refactoring | Bagattoni | Companion object's refactoring | no |
| 50 | Trajectory refactoring | Bagattoni | Use the "Direction" enum everywhere instead of Ints | no |
| 51 | Comments on BoundingBox | Bagattoni | this | no |
| 52 | GUI refactoring | Sbaraccani | Better reuse of code in render() method | yes |
| 53 | Docs - [**Implementation**](../docs/5-implementazione.md)| everyone | each member writes its own section | no |
| 54 | Docs - [**Testing**](../docs/6-testing.md) | someone | write Testing section | no |
| 55 | Docs - [**Retrospettiva**](../docs/7-retrospettiva.md) | someone | write Retrospettiva section | no |

Keep in mind: ***always merge working code***

### Sprint review meeting (06/08/2026)

___
___

[**&larr; Torna al Product Backlog**](./product_backlog.md) | [**Torna alla Home**](../index.md)
