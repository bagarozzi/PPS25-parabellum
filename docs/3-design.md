# Design architetturale
Per rispettare il principio di *Separation of Concerns* tra view e logica di business, favorendo l'immutabilità e uno sviluppo funzionale, viene adottato il pattern
Model-View-ViewModel (M-V-VM).

Questa scelta architetturale deriva dalla necessità di mantenere il più pulito ed astratto possibile il codice della logica di business, riferendosi al principio
**Functional Core, Imperative Shell**. L'architettura MVVM pone una barriera proprio tra View e Model, permettendo astrazione rispetto al livello di presentazione
ed evitando di *sporcare* il codice backend solo per necessità di convenienza, per esempio con dei *side-effect*s.

L'architettura è quindi divisa tra View (scritta tramite ScalaFX) ed un Model puramente funzionale connessi tramite un **Controller**
che si occupa dell'avvio del game-loop, in cui sono *confinati* i side-effects.




<br>
A questo fine viene sviluppato un componente **GameContext** che viene *osservato* dalla View per ricevere cambiamenti.

___
___
[**&larr; Analisi dei requisiti** ](./2-requisiti.md) | **Design del sistema** | [ **Design di dettaglio &rarr;**](./4-dettaglio.md)
<br>
[**Torna alla home**](../index.md)