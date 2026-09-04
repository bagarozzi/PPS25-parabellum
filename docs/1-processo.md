# Processo di sviluppo
In questa sezione è descritto il processo di sviluppo adottato per il progetto insieme agli strumenti di CI/CD, automazione
e relative regole.

## SCRUM
Il processo di sviluppo adottato per il progetto è SCRUM, un framework Agile che porta un approccio iterativo ed incrementale.

Il lavoro è stato diviso in 6 *sprint* di una settimana ciascuno (alcuni variati a causa di assenze o imprevisti), al termine di
cui è stato creato un nuovo artefatto con le feature implementate durante la settimana.

### Ruoli
I ruoli sono come segue:
- Product Owner e Developer: il gruppo intero (F. Bagattoni, L. Venturini, P. Sbaraccani) 
- Scrum Master: Bagattoni Federico

### Documentazione redatta
Per ogni sprint viene creata l'[opportuna documentazione](../process/sprint_4_backlog.md).
In particolare, la documentazione viene redatta in markdown tramite l'uso di tabelle in cui sono elencate le task del backlog.
Ogni task è numerata unicamente, corredata di *definition of done*, personale assegnato e descrizione sommaria; un'esempio di questo può essere trovato in [uno degli sprint](../process/sprint_4_backlog.md).

La redazione di una documentazione di processo efficace permette di pianificare accuratamente
e migliorare l'efficienza durante il lavoro.

### Creazione e divisione in itinere dei lavori
Le feature vengono decise da tutti i membri del gruppo in quanto tutti *Product Owner*s, per poi essere trasformate in
task dallo Scrum Master. In questo modo vengono prodotte task il più indipendenti possibili le une con le altre.
Le task con forti interdipendenze sono state eseguite in gruppo o in coppia.

A partire dal [terzo sprint](../process/sprint_3_backlog.md) è stato deciso di non assegnare direttamente le task, ma 
dare autonomia ai developer di prendere in carico la task che più li aggradava.

Sebbene questa possa sembrare una pratica polarizzante e che porta alla *specializzazione* dei developer per un particolare
componente del sistema (la *cross-functionality* è molto importante in Agile), per un progetto di questa dimensione sarebbe
stata, in parte, inevitabile questa circostanza.

Nel caso specifico, gli sprint sono stati divisi in macro-argomenti (*milestones*), come segue:
- Sprint 1-3: desing, implementazione dell'architettura e versione di base del gioco
- Sprint 3-5: implementazione di feature avanzate, refactoring e bug-fixing in itinere 
- Sprint 5-6: risoluzione di bug, refactoring finale e redazione della documentazione

### Interazioni pianificate
Le interazioni pianificate sono quelle di **Sprint planning**, **Sprint review** e frequenti interazioni durante lo sprint 
come ***pair programming***. Molto spesso il daily scrum è stato svolto per mezzo di messaggistica, visti gli impegni dei 
membri del team.

Inoltre, non sempre ciò che era stato detto durante lo sprint planning era sufficientemente dettagliato per implementare una nuova feature; le interazioni giornaliere sono state altrettanto importanti per questo.  

## Considerazioni sul processo di sviluppo
L'adozione di questo framework è stata particolarmente utile per lo sviluppo, in quanto da la possibilità di familiarizzare
parecchio col prodotto, notare bug, criticità e potenziali refactoring in anticipo, permettendo di gestire il carico di lavoro
e l'implementazione di nuove feature avendo sempre una copia funzionante dell'artefatto.

Sebbene l'analisi dei requisiti sia stata in gran parte eseguita in precedenza, soprattutto per quanto riguarda l'architettura,
la modalità incrementale lascia molto spazio di manovra per l'implementazione di feature future; permettendo di concentrarsi sulle
feature attuali e basandosi solo su ciò che si conosce.

## Strumenti di CI/CD
Il build tool utilizzato è Sbt mentre per il testing è stato scelto scalatest.

Il controllo versione è affidato a Git e GitHub. In particolare viene creata una branch per ogni task del product backlog,
di cui viene fatto il merge tramite Pull-Request sul branch principale.

L'utilizzo delle Pull-Request è utile al fine di tracciare lo stato di avanzamento e generare i changelog corretti tra
una versione e l'altra del prodotto.

Alla fine di ogni sprint viene rilasciato un tag per indicare la nuova versione e creata, tramite GitHub Actions, una release con changelog.

La documentazione è invece redatta in markdown e rilasciata sotto forma di pagina web statica sull'indirizzo GitHub Pages del repository.

___
___
[**&larr; Introduzione** ](0-introduzione.md) | **Processo di sviluppo** | [ **Analisi dei requisiti &rarr;**](2-requisiti.md)
<br>
[**Torna alla home**](../index.md)