# Implementazione
In questa sezione vengono elencati gli aspetti implementativi significativi del progetto, eseguiti singolarmente o in gruppo.

## GameState
*realizzato da Federico Bagattoni e Luca Venturini*

Questo componente rappresenta lo stato globale del gioco: funge da contenitore per tutte le entità e permette l'aggiornamento di quest'ultime.
Il suo metodo ```update``` applica una serie di trasformazioni sul suo stato, restituendone uno aggiornato:

```Scala
def update(g: GameState, dt: Double, passedFunction: Option[Function])(using border: BoundingBox): GameState =
updateProjectile(g, dt).fold(g)(p => g.copy(projectile = Some(p)))
    .map(resolveCollisions)
    .map(processPendingInput(_, passedFunction))
    .map(spawnProjectile)
```

Come verrà menzionato più avanti, ogni trasformazione che interessa il gioco è stata implementata come funzione di tipo
```GameState => GameState``` in modo tale da poter essere applicate tramite ```map()```.
Generazione delle entità, movimento e collisioni sono tutte trasformazioni dello stato del gioco.

# Luca Venturini

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

### Rilevazione delle collisioni
Le collisioni vengono rilevate dal CollisionDetector, che ad ogni frame testa se la posizione del proiettile ricade all'interno di una qualsiasi Figure presente nel gioco utilizzando il metodo ```belongs()```. Quando una collisione viene rilevata, il CollisionDetector genera un Set di ImpactEvent che descrivono l'esito dell'impatto. Questi eventi vengono successivamente gestiti dal GameState, che applica i cambiamenti allo stato del gioco in base alla natura degli eventi ricevuti: la logica specifica di come una collisione influenzi il gioco (danneggiare un ostacolo, eliminare un nemico, raccogliere un potenziamento) rimane incapsulata negli event stessi, mantenendo la separazione delle responsabilità tra rilevamento geometrico e logica di gioco.

### Gestione degli impatti
Spiegazione di cosa è stato modellato con cosa.

# Federico Bagattoni

## Function
Le funzioni matematiche sono al cuore di questo gioco. Per garantire flessibilità nel loro utilizzo ma senza la verbosità di
funzioni ```Double => Double``` è stata implementata ```Function```.

```Scala
/**
 * A mathematical function.
 */
opaque type Function = Double => Double

object Function:

    val DERIVATIVE_H = 0.0001

    /**
     * Create a mathematical function from the one passed as argument
     * @param f the function to create
     * @return a Function
     */
    def apply(f: Double => Double): Function = f
```

L' utilizzo di ```opaque type``` permette di astrarre dal tipo sottostante e garantisce sicurezza nell'uso, impedendo *leaky abstractions*; mentre i metodi infissi come 
```+``` o ```-``` contribuiscono alla creazione di un mini-DSL che facilità la combinazione di più funzioni o la loro trasformazione. L'implementazione di ```apply()``` permette di estrarre il valore di y semplicemente chiamando function(x), come
in notazione matematica.

```Scala
  private val distortFunction: Function = Function(x => 0.05 * x * x)

  def trajectoryDistortion(function: Function): Function = function - distortFunction
```

Ad esempio, nel PowerUp *Burden*, la funzione che applica il *peso* alla traiettoria può essere sottratta alla prima.

Un metodo di estensione interessante è anche il calcolo della derivata in un punto tramite il metodo ```derive(x)```.

## ImpactEffect
*realizzato il collaborazione con Luca Venturini*

ImpactEffect racchiude il comportamento di un proiettile, definisce il comportamento che un proiettile deve avere quando avviene un impatto.
Questo viene realizzato definendo una funzione ```applyEffect``` che, passato un impatto ```Impact```, torna un insieme di 
```ImpactEvent```s, i quali possono essere applicati allo stato del gioco per modificarlo.

```Scala
def normalImpactEffect(): ImpactEffect = {
    case FigureImpact(pos, obs: Obstacle) => Set(DamageObstacle(obs, normalExplosion(pos)), DestroyProjectile())
    case FigureImpact(pos, sld: Soldier) => Set(KillSoldier(sld))
    case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
    case BorderImpact(_) => Set(DestroyProjectile())
    case FigureImpact(Position(_, _), _) => Set()
}

def shootingRangeImpactEffect(): ImpactEffect = {
    case FigureImpact(pos, obs: Obstacle) => Set(DestroyObstacle(obs), SpawnNewObstacle())
    case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
    case i => normalImpactEffect().applyEffect(i)
}
```

In questo modo si possono creare estensioni, combinare assieme più comportamenti tramite la definizione di una funzione.

Questo è stato particolarmente utile nella creazione della schermata *Shooting Range* in cui il proiettile, oltre a
distruggere un ostacolo, provocava la comparsa di un altro ostacolo.

# Pietro Sbaraccani

## MapGenerator
Il modulo MapGenerator gestisce la creazione procedurale della mappa adottando un pattern architetturale basato sull'evoluzione dello stato (`GameState => GameState`). Abbandonando l'uso di variabili globali o stati mutabili, l'algoritmo mappa il posizionamento spaziale come un flusso di dati continuo. La generazione multipla delle entità (ostacoli, power-up e soldati) è orchestrata tramite operazioni di `foldLeft`, che propagano esplicitamente lo stato aggiornato e immutabile da una fase di generazione alla successiva. Le dimensioni fisiche dell'area di gioco vengono totalmente disaccoppiate dalla logica di posizionamento tramite l'iniezione implicita della `BoundingBox` (`using border`), garantendo scalabilità su mappe di qualsiasi proporzione.

### Propagazione dello Spazio e Validazione Incrementale
Per evitare compenetrazioni (es. power-up generati sopra ostacoli o soldati), la gestione delle collisioni si affida a una mappatura dinamica degli ingombri fisici. Prima di ogni singolo spawn, il metodo `getOccupiedSpaces` interroga lo snapshot del `GameState` corrente per estrarre il footprint spaziale (coordinate X, Y e Raggio) di tutte le entità già posizionate.

Questo tracciato storico viene passato al motore logico esterno (`PrologMapChecker`), il quale esegue una validazione spaziale incrementale. Questo approccio risolve i colli di bottiglia degli algoritmi "Dart Throwing": anziché calcolare e validare l'intera mappa in blocco (con il rischio di doverla scartare e ricalcolare interamente per una singola sovrapposizione), il sistema interroga Prolog per singola coordinata. Se viene rilevata un'intersezione, la funzione di spawn sfrutta la ricorsione in coda (`@tailrec`) per scartare e rigenerare esclusivamente quel singolo punto, azzerando i tempi morti di inizializzazione.

[**&larr; Design di dettaglio** ](./4-dettaglio.md) | **Implementazione** | [ **Testing &rarr;**](./6-testing.md)
<br>
[**Torna alla home**](../index.md)