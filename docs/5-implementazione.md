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
La Shape è stata pensata come un predicato logico che astrae una figura geometrica. Tramite il metodo ```belongs```, la Shape verifica se una posizione ricade al suo interno, abilitando il rilevamento delle collisioni tra proiettili e Figure. Le implementazioni concrete di Shape consentono al sistema di collision detection di rimanere agnostico rispetto alla geometria specifica, mantenendo una logica uniforme e riusabile.

### Circle
Circle implementa ```belongs``` tramite la distanza euclidea dal centro: un punto appartiene al cerchio se la distanza dal centro è minore o uguale al raggio. Questa implementazione è semplice e efficiente, rendendo Circle ideale per entità mobili come i soldati e i potenziamenti.

### Polygon
Polygon rappresenta forme arbitrarie tramite una sequenza di vertici. L'implementazione del metodo `belongs` utilizza l'**algoritmo di ray casting**: proietta un raggio orizzontale dal punto di test e conta le intersezioni con i lati del poligono; se il numero di intersezioni è dispari, il punto è interno. Questa tecnica consente di testare l'appartenenza in O(n) dove n è il numero di vertici, senza necessità di formule geometriche complesse.
```Scala
  override def belongs: Position => Boolean =
    p =>
      edges.count { (a, b) =>
        ((a.y > p.y) != (b.y > p.y)) &&
          (p.x < (b.x - a.x) * (p.y - a.y) / (b.y - a.y) + a.x)
      } % 2 == 1
```    
La creazione di un Polygon avviene tramite il metodo `create()`, che utilizza l'**algoritmo di ordinamento polare** per garantire che i vertici siano disposti in senso antiorario (CCW): calcola il baricentro dei vertici e li ordina per angolo polare rispetto al centro. Ciò assicura che il poligono sia sempre ben-formato e che l'algoritmo di ray casting funzioni correttamente, indipendentemente dall'ordine iniziale dei vertici forniti dall'utente.
```Scala
private def sortVertices(vertices: Seq[Position]): Seq[Position] =
    val center = Position(
      vertices.map(_.x).sum / vertices.size,
      vertices.map(_.y).sum / vertices.size
    )
    vertices.sortBy { v =>
      math.atan2(
        v.y - center.y,
        v.x - center.x
      )
    }
```
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

Il sistema di risoluzione delle collisioni è costruito attorno a una pipeline di trasformazioni funzionali 
che mantiene la separazione tra rilevamento geometrico e logica di gioco.

Quando il CollisionDetector rileva una collisione, crea un Impact (che rappresenta l'impatto stesso della 
collisione: una posizione, una figura colpita, o un bordo toccato). Questo Impact viene quindi passato 
all'ImpactEffect associato al proiettile, il quale utilizza il pattern matching per trasformare l'Impact 
in un Set di ImpactEvent specializzati.

Questa trasformazione avviene interamente attraverso funzioni pure: l'ImpactEffect sa come reagire a diverse tipologie di impatto. Ad esempio, se un proiettile 
colpisce un ostacolo, l'ImpactEffect decide se l'ostacolo deve essere danneggiato. Ogni effetto 
può essere composto e riutilizzato.

Una volta generato il Set di ImpactEvent, il GameState accumula gli effetti sequenzialmente tramite 
`foldLeft()`, applicando il metodo `action()` di ogni evento:
```Scala
  private def resolveCollisions(g: GameState)(using border: BoundingBox): GameState = g
      .projectile
      .map(detectCollision(_, g.manager.enemies ++ g.obstacles ++ g.powerUps))
      .map(_.foldLeft(g)((g,e) => e.action(g)))
      .getOrElse(g)

```
Ogni action() trasforma il GameState in modo immutabile, garantendo che gli effetti si propaghino in ordine definito. Questo approccio rende il sistema completamente estensibile: aggiungere un nuovo tipo di impatto (es. congelamento, teletrasporto, danno nel tempo) richiede solo di definire un nuovo ImpactEvent con il suo action(), senza modificare il motore di collision detection.

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

## Trajectory
```Function``` viene usata in Trajectory. Questo componente viene usato in Projectile e ne rappresenta la traiettoria. Implementa
le proprietà matematiche che permettono al proiettile di muoversi lungo la funzione scelta dall'utente ed allo stesso tempo
allineando la traiettoria al soldato da cui è stata sparata.

Qui sono state implementati i metodi per la creazione del ricochet (che crea una retta a partire dalla derivata prima della funzione nel punto di impatto) e la regolazione dinamica della velocità per funzioni molto pendenti.

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