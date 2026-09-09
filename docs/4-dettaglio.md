# Design di dettaglio
## MapGenerator
Il modulo MapGenerator gestisce la creazione procedurale della mappa adottando un pattern architetturale basato sull'evoluzione dello stato (`GameState => GameState`). Abbandonando l'uso di variabili globali o stati mutabili, l'algoritmo mappa il posizionamento spaziale come un flusso di dati continuo. La generazione multipla delle entità (ostacoli, power-up e soldati) è orchestrata tramite operazioni di `foldLeft`, che propagano esplicitamente lo stato aggiornato e immutabile da una fase di generazione alla successiva. Le dimensioni fisiche dell'area di gioco vengono totalmente disaccoppiate dalla logica di posizionamento tramite l'iniezione implicita della `BoundingBox` (`using border`), garantendo scalabilità su mappe di qualsiasi proporzione.

### Propagazione dello Spazio e Validazione Incrementale
Per evitare compenetrazioni (es. power-up generati sopra ostacoli o soldati), la gestione delle collisioni si affida a una mappatura dinamica degli ingombri fisici. Prima di ogni singolo spawn, il metodo `getOccupiedSpaces` interroga lo snapshot del `GameState` corrente per estrarre il footprint spaziale (coordinate X, Y e Raggio) di tutte le entità già posizionate.

Questo tracciato storico viene passato al motore logico esterno (`PrologMapChecker`), il quale esegue una validazione spaziale incrementale. Questo approccio risolve i colli di bottiglia tipici degli algoritmi "Dart Throwing" tradizionali: anziché calcolare e validare l'intera mappa in blocco (con il rischio di doverla scartare e ricalcolare interamente per una singola sovrapposizione), il sistema interroga Prolog per singola coordinata. Se viene rilevata un'intersezione, la funzione di spawn sfrutta la ricorsione in coda (`@tailrec`) per scartare e rigenerare esclusivamente quel singolo punto, azzerando i tempi morti di inizializzazione.

### Specializzazione delle Entità
La logica di istanziazione si adatta proceduralmente alle caratteristiche geometriche e tattiche delle singole categorie di entità:

Ostacoli: Generati in modo interamente casuale all'interno dei limiti della mappa, con una suddivisione statistica paritaria (50% cerchi, 50% poligoni). I poligoni generano dinamicamente dai 3 ai 7 vertici; a questi viene assegnato un raggio di circoscrizione calcolato in fase di spawn per coprire preventivamente l'intera area che andranno a occupare prima del check di validazione.

Power-up: Ripartiti in modo statisticamente omogeneo (25% di probabilità per tipologia: `Ricochet`, `Burden`, `Random`, `Piercing`) negli spazi vuoti residui della mappa. Il raggio di validazione spaziale non è hardcoded, ma viene estratto polimorficamente dalla `Shape` dell'istanza appena creata.

Giocatori e Squadre: La mappa viene divisa strategicamente in due segmenti laterali (destro e sinistro), mantenendo un margine centrale di sicurezza (`safeMargin`) per evitare il corpo a corpo al primo turno. Il posizionamento avviene sequenzialmente: l'algoritmo istanzia i soldati del primo team, integrandoli nel `GameState`, e successivamente utilizza i loro stessi ingombri per validare gli spazi disponibili al posizionamento della squadra avversaria. Il raggio di collisione di ogni unità viene letto dinamicamente dalla relativa proprietà `shape`.
___
___
[**&larr; Design del sistema** ](./3-design.md) | **Design di dettaglio** | [ **Implementazione &rarr;**](./5-implementazione.md)
<br>
[**Torna alla home**](../index.md)
