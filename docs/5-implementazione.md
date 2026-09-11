# Implementazione

## Shape
La Shape è stata pensata come un predicato logico che astrae una figura geometrica. Tramite il metodo ```belongs```, la Shape verifica se una posizione ricade al suo interno, abilitando il rilevamento delle collisioni tra proiettili e Figure. Le implementazioni concrete di Shape: Circle per forme semplici, Polygon per forme arbitrarie e Difference per ostacoli danneggiati da esplosioni che consentono al sistema di collision detection di rimanere agnostico rispetto alla geometria specifica, mantenendo una logica uniforme e riusabile.

### Difference
Come menzionato sopra, un'implementazione di Shape è ```Difference```. Essa rappresenta la differenza tra una forma geometrica ed 
una o più forme, calcolata esclusivamente dal punto di vista logico e insiemistico.
```Difference``` permette di lavorare agilmente, anche con forme irregolari, senza ricorrere a complicate formule matematiche
(si immagini di rimuovere un cerchio da un rettangolo) bensì utilizzando solamente la formula di appartenenza alla figura.
Questo consente una composizione ricorsiva delle differnze tra forme geometriche ed anche la loro eventuale ricostruzione.
Ciò è utilizzato per aggiungere facilmente i fori delle esplosioni agli ostacoli: 
```Scala
override def addExplosion(s: Shape): Obstacle = shape match
    case Difference(a, b) => ObstacleImpl(pos, Difference(a, b + s))
    case _ => ObstacleImpl(pos, Difference(shape, Set(s)))
```

## Collisioni
Le collisioni vengono rilevate dal CollisionDetector, che ad ogni frame testa se la posizione del proiettile ricade all'interno di una qualsiasi Figure presente nel gioco utilizzando il metodo `belongs()`. Quando una collisione viene rilevata, il CollisionDetector genera un Set di ImpactEvent che descrivono l'esito dell'impatto. Questi eventi vengono successivamente gestiti dal GameState, che applica i cambiamenti allo stato del gioco in base alla natura degli impact event ricevuti: la logica specifica di come una collisione influenzi il gioco (danneggiare un ostacolo, eliminare un nemico, raccogliere un potenziamento) rimane incapsulata negli event stessi, mantenendo la separazione delle responsabilità tra rilevamento geometrico e logica di gioco.


[**&larr; Design di dettaglio** ](./4-dettaglio.md) | **Implementazione** | [ **Testing &rarr;**](./6-testing.md)
<br>
[**Torna alla home**](../index.md)