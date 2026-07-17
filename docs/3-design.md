# Design architetturale
Per rispettare il principio di *Separation of Concerns* tra view e logica di business e favorire l'immutabilità e uno sviluppo funzionale, si è deciso di
adottare il pattern Observer per comunicare i cambiamenti del Model alla View in maniera distaccata.
In questo modo i *side-effects* vengono confinati solo alla parte di sistema che si relaziona con la View, lasciando la possibilità alla logica di business di poter
essere completamente funzionale e immutabile.
<br>
A questo fine viene sviluppato un componente **GameContext** che viene *osservato* dalla View per ricevere cambiamenti.

