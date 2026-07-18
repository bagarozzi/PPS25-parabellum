# Design architetturale
Per rispettare il principio di *Separation of Concerns* tra view e logica di business e favorire l'immutabilità e uno sviluppo funzionale, si è deciso di
adottare il pattern Observer per comunicare i cambiamenti del Model alla View in maniera distaccata.
In questo modo i *side-effects* vengono confinati solo alla parte di sistema che si relaziona con la View, lasciando la possibilità alla logica di business di poter
essere completamente funzionale e immutabile.
<br>
A questo fine viene sviluppato un componente **GameContext** che viene *osservato* dalla View per ricevere cambiamenti.

___
___
[**&larr; Analisi dei requisiti** ](./2-requisiti.md) | **Design del sistema** | [ **Design di dettaglio &rarr;**](./4-dettaglio.md)
<br>
[**Torna alla home**](../index.md)