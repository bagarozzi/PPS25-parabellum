# Implementazione

___
___

## Collisioni
Le collisioni vengono rilevate dal CollisionDetector, che ad ogni frame testa se la posizione del proiettile ricade all'interno di una qualsiasi Figure presente nel gioco utilizzando il metodo `belongs()`. Quando una collisione viene rilevata, il CollisionDetector genera un Set di ImpactEvent che descrivono l'esito dell'impatto. Questi eventi vengono successivamente gestiti dal GameState, che applica i cambiamenti allo stato del gioco in base alla natura degli impact event ricevuti: la logica specifica di come una collisione influenzi il gioco (danneggiare un ostacolo, eliminare un nemico, raccogliere un potenziamento) rimane incapsulata negli event stessi, mantenendo la separazione delle responsabilità tra rilevamento geometrico e logica di gioco.


[**&larr; Design di dettaglio** ](./4-dettaglio.md) | **Implementazione** | [ **Testing &rarr;**](./6-testing.md)
<br>
[**Torna alla home**](../index.md)