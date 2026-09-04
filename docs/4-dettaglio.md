# Design di dettaglio
## MapGenerator
Il modulo MapGenerator gestisce la creazione procedurale della mappa trattando le coordinate occupate come un flusso di dati immutabile, passato esplicitamente tra le fasi di generazione senza ricorrere a stati globali. Le dimensioni dell'area di gioco (	`BoundingBox`) vengono fornite implicitamente ai metodi tramite la clausola using, disaccoppiando l'algoritmo di posizionamento dalla grandezza specifica del livello.

### generateEntities
Astrae il loop di posizionamento e validazione spaziale. Interroga 	`PrologMapChecker` per ogni coordinata: se rileva una collisione ricalcola il punto, altrimenti salva l'entità e il suo ingombro.

### Propagazione dello Spazio
Per evitare compenetrazioni (es. power-up generati sopra i soldati), ogni fase restituisce gli oggetti creati e la lista aggiornata delle coordinate occupate (X, Y, Raggio). Questo storico fa da base per la fase di posizionamento successiva.

### Specializzazione delle Entità

- Ostacoli: Spawn casuale all'interno dei confini della mappa (50% cerchio, 50% poligono). Ai poligoni è assegnato un raggio di circoscrizione calcolato per coprire l'intera area dei vertici.

- Giocatori: Suddivisione in due segmenti laterali con margine centrale. Il posizionamento avviene in sequenza: l'ingombro del primo team viene trasmesso al calcolo del secondo. Il raggio di collisione viene letto dinamicamente dalla proprietà shape del singolo soldato.

- Power-up: Ripartizione statistica omogenea (25% per tipologia) negli spazi vuoti residui. Il raggio di validazione viene estratto dall'istanza appena creata.

### Validazione Incrementale (Prolog)
L'interrogazione elemento per elemento aggira i colli di bottiglia del Dart Throwing. Invece di validare l'intera mappa in blocco — con il rischio di doverla rigettare per una singola sovrapposizione — il sistema ricalcola unicamente la coordinata non valida, minimizzando i tempi di inizializzazione.
___
___
[**&larr; Design del sistema** ](./3-design.md) | **Design di dettaglio** | [ **Implementazione &rarr;**](./5-implementazione.md)
<br>
[**Torna alla home**](../index.md)
