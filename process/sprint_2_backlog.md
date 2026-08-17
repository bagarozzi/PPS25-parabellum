# Sprint Backlog - Sprint 2

**Sprint goal**: consegnare un'applicazione minimale in cui si predispone l'architettura per gli sprint futuri

**Duration/Deadline**: 16/08/2026

### Sprint planning meeting (10/08/2026)
Visti i risultati dello sprint precedente, l'obbiettivo del seguente sarà quello di consegnare una versione funzionante e 
minimale dell'applicazione. 
A questo fine si decide di implementare solo certi aspetti dell'applicazione:
- Gettare le basi per l'architettura del gioco M-VM-V creando Engine, View e Business Logic
- Interfaccia grafica minimale che permetta di sparare, abbia giocatori ma senza ostacoli
- Il giocatore deve poter *sparare* una semplice retta nella direzione che vuole
- Collisioni contro i bordi funzionanti

Vengono create delle task a partire dall'elenco qui sopra.

In particolare si punta ad una versione minimale che predisponga all'aggiunta di componenti aggiuntivi e feature.


### Tasks to be done
| Task ID | Task Description | Assignee | When it's done | Done |
| :--- | :--- | :--- | :--- | :---: |
| 05 | Write [**Processo**](../docs/1-processo.md) section of the report | Bagattoni | . |
| 06 | Write the Domain modeling sections of [**Requirements**](../docs/2-requisiti.md) | Venturini | . | no |
| 07 | Interfaccia grafica di base | Sbaraccani | . | yes |
| 08 | Architettura di base: flusso di dati tra model, view, viewmodel | Everyone | . | yes |
| 09 | Inserimento (tramite ViewModel e Engine) e parsing dei parametri di una retta | Everyone | . | yes |
| 10 | Creazione dell'Engine per stato del gioco e game loop | Bagattoni | . | yes |
| 11 | Gestione delle collisioni dei proiettili | Venturini | . | yes |

### Sprint review meeting (17/08/2026)
Lo sprint ha avuto successo: le task sono state completate ed una versione preliminare del gioco è disponibile.

Oltre al completamento delle task sono state aggiunte delle classi che torneranno utili durante gli sviluppi futuri del progetto.

Le task di documentazione non sono state completate in quanto non sono necessarie al momento.
___
___

[**&larr; Torna al Product Backlog**](./product_backlog.md) | [**Torna alla Home**](../index.md)