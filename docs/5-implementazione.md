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
*realizzato il collaborazione con Pietro Sbaraccani*

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

Come menzionato durante la [fase di design]("./4-dettaglio.md), questo processo avviene 
interamente attraverso funzioni pure: 
1. `CollisionDetector` rileva le collisioni producendo un `Impact`
2. `ImpactEffect` del proiettile reagisce al tipo di impatto producendo una `Set` di `ImpactEvent`s
3. Gli `ImpactEvent`s sono applicati al `GameState`

Una volta generato il Set di ImpactEvent, il GameState accumula gli effetti sequenzialmente tramite 
`foldLeft()`, applicando il metodo `action()` di ogni evento:
```Scala
  private def resolveCollisions(g: GameState)(using border: BoundingBox): GameState = g
      .projectile
      .map(detectCollision(_, g.manager.enemies ++ g.obstacles ++ g.powerUps))
      .map(_.foldLeft(g)((g,e) => e.action(g)))
      .getOrElse(g)
```
Ogni `action()` trasforma il GameState in modo immutabile, garantendo che gli effetti si propaghino in ordine definito. Questo approccio rende il sistema completamente estensibile: aggiungere un nuovo tipo di impatto (es. congelamento, teletrasporto, danno nel tempo) richiede solo di definire un nuovo ImpactEvent con il suo `action()`, senza modificare il motore di collision detection.

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

ImpactEffect definisce il comportamento che un proiettile deve avere quando avviene un impatto.
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
Il modulo `MapGenerator` gestisce la creazione procedurale della mappa adottando un pattern architetturale puramente funzionale basato sull'evoluzione dello stato (`GameState => GameState`). Abbandonando l'uso di variabili globali o stati mutabili, l'algoritmo mappa il posizionamento spaziale come un flusso di dati continuo. 

La generazione multipla delle entità (ostacoli, power-up e soldati) è orchestrata tramite operazioni di `foldLeft`, che propagano esplicitamente lo stato aggiornato e immutabile da una fase di generazione alla successiva. Le dimensioni fisiche dell'area di gioco vengono disaccoppiate dalla logica di posizionamento tramite l'iniezione implicita della `BoundingBox` (`using border`), garantendo scalabilità su mappe di qualsiasi proporzione.

```scala
def generateObstacles(count: Int, initialGameState: GameState)(using border: BoundingBox): GameState =
  (1 to count).foldLeft(initialGameState)((acc, _) => spawnObstacle(acc))
```

### Propagazione dello Spazio e Validazione Incrementale
Per evitare compenetrazioni (es. power-up generati sopra ostacoli o soldati), la gestione delle collisioni si affida a una mappatura dinamica degli ingombri fisici. Prima di ogni singolo spawn, il metodo `getOccupiedSpaces` interroga lo snapshot del `GameState` corrente per estrarre il footprint spaziale (coordinate X, Y e Raggio) di tutte le entità già posizionate.

Questo tracciato storico viene passato al motore logico esterno, il quale esegue una validazione spaziale incrementale. Se viene rilevata un'intersezione, la funzione di spawn sfrutta la ricorsione in coda (`@tailrec`) per scartare e rigenerare esclusivamente quel singolo punto, azzerando i tempi morti di inizializzazione ed evitando il ricalcolo dell'intera mappa tipico degli algoritmi "Dart Throwing".

```scala
@tailrec
def spawnPowerUp(g: GameState)(using border: BoundingBox): GameState =
  val pX = border.x0 + (border.x1 - border.x0) * math.random()
  val pY = border.y0 + (border.y1 - border.y0) * math.random()
  val pos = Position(pX, pY)
  
  // ... determinazione dinamica del raggio in base alla shape ...

  if PrologMapChecker.hasOverlap(pX, pY, radius, getOccupiedSpaces(g)) then
    spawnPowerUp(g)
  else
    GameState.addPowerUp(g, pu)
```

## PrologMapChecker
Per delegare e ottimizzare la logica di validazione spaziale, il sistema integra `PrologMapChecker`, un modulo che sfrutta la libreria `alice.tuprolog`. Questo componente valuta le collisioni risolvendo una teoria Prolog appositamente definita, garantendo un'interrogazione logica dichiarativa ed efficiente.

La teoria si basa su tre regole fondamentali:
- `distance/5`: Calcola la distanza euclidea tra due punti nello spazio 2D.
- `overlap/6`: Verifica se la distanza calcolata è strettamente minore della somma dei due raggi (`D < (R1 + R2)`), confermando l'intersezione geometrica.
- `check_overlap_list/4`: Itera ricorsivamente sulla lista degli elementi già presenti sulla mappa. Se il predicato `overlap` risulta vero per l'elemento corrente della testa della lista, l'operatore di cut (`!`) interrompe immediatamente la ricerca. Questo meccanismo ottimizza le prestazioni evitando di controllare il resto della lista non appena viene rilevata una collisione.

```prolog
distance(X1, Y1, X2, Y2, D) :- D is sqrt((X2 - X1)**2 + (Y2 - Y1)**2).
overlap(X1, Y1, R1, X2, Y2, R2) :- distance(X1, Y1, X2, Y2, D), D < (R1 + R2).
check_overlap_list(X, Y, R, [element(EX, EY, ER) | _]) :- overlap(X, Y, R, EX, EY, ER), !.
check_overlap_list(X, Y, R, [_ | Tail]) :- check_overlap_list(X, Y, R, Tail).
```

L'integrazione tra Scala e Prolog avviene in modo trasparente tramite il metodo `hasOverlap`. Se la sequenza delle entità esistenti non è vuota, il metodo converte la lista di tuple (X, Y, Raggio) in una stringa compatibile con la sintassi delle liste Prolog, formula la query `check_overlap_list` e restituisce un booleano interrogando `engine.solve(queryStr).isSuccess`.

## Obstacle
Il componente `Obstacle` modella gli ostacoli fisici del campo di battaglia. È progettato seguendo il pattern Factory tramite il suo companion object, nascondendo l'implementazione concreta (`ObstacleImpl`) ed esponendo metodi costruttori semantici come `setCircle` e `setPolygon` per facilitarne la generazione procedurale.

La caratteristica principale di questa entità è la gestione della distruzione parziale tramite il metodo `addExplosion(s: Shape)`. Invece di ricalcolare i vertici o i bordi geometrici dopo un impatto, l'ostacolo sfrutta l'implementazione logica e insiemistica di `Difference` (definita nel modulo `Shape` e discussa in precedenza).

Quando un'esplosione colpisce l'ostacolo:
- Se l'ostacolo ha già subito danni in precedenza (la sua shape è già un'istanza di `Difference`), la nuova esplosione viene semplicemente aggiunta all'insieme delle forme sottratte (`a, b + s`).
- Se l'ostacolo è integro (un cerchio o un poligono puro), viene avvolto in una nuova istanza di `Difference` tra la forma originale e il set contenente la nuova esplosione.

```scala
override def addExplosion(s: Shape): Obstacle = shape match
  case Difference(a, b) => ObstacleImpl(pos, Difference(a, b + s))
  case _ => ObstacleImpl(pos, Difference(shape, Set(s)))
```

Questo approccio delega interamente il rilevamento delle collisioni e la manipolazione della geometria complessa alla logica di appartenenza (`belongs`) della forma stessa, restituendo a ogni impatto una nuova istanza immutabile dell'ostacolo aggiornato.
## View e Interfaccia Grafica

Il sottosistema grafico è stato sviluppato utilizzando **ScalaFX** (un wrapper idiomatico in Scala per JavaFX) ed è progettato per reagire in modo passivo ai cambiamenti di stato del modello, garantendo una rigida separazione architetturale (pattern MVC/MVP).

### MainGUI e Pipeline di Rendering
Il fulcro dell'aggiornamento visivo è la classe `MainGUI`, che funge da orchestratore del rendering. Ogni volta che il Game Loop genera un nuovo `GameState`, la GUI viene aggiornata tramite il metodo `render(state: GameState)`. 
Per garantire la thread-safety e prevenire eccezioni di concorrenza, tutte le mutazioni al grafo della scena (Scene Graph) vengono instradate sul JavaFX Application Thread tramite `Platform.runLater`.

La sincronizzazione tra le entità immutabili del dominio e i nodi mutabili a schermo (es. `PlayerView`, `ObstacleView`, `ProjectileView`) avviene tramite mappe di tracciamento (`Map[Entity, Node]`). Ad ogni frame, il sistema:
1. Aggiorna la posizione delle viste esistenti convertendo le coordinate logiche in coordinate a schermo tramite `GeometryHelper`.
2. Istanzia nuove viste per le entità appena create.
3. Smaltisce le viste delle entità non più presenti nello stato.

### Garbage Collection Visiva
Per evitare duplicazione di codice durante la fase di smaltimento di ostacoli distrutti, soldati eliminati o power-up raccolti, è stato implementato un helper generico per la "Garbage Collection visiva". 
Il metodo `removeStaleViews` confronta le chiavi delle entità attualmente attive nel dominio con quelle tracciate dalla vista, rimuovendo dal pannello di gioco (`GameView`) i nodi orfani. L'uso di un upper type bound (`V <: Node`) garantisce la type-safety a tempo di compilazione.

```scala
private def removeStaleViews[K, V <: Node](activeKeys: Set[K], views: Map[K, V]): Map[K, V] =
  val staleKeys = views.keys.toSet.diff(activeKeys)
  staleKeys.foreach(k => gameView.removeElement(views(k)))
  views -- staleKeys
```

### Gestione delle Texture e Interoperabilità JavaFX
Per modernizzare l'estetica del gioco, le primitive geometriche sono state arricchite con texture basate su immagini. 
Poiché ScalaFX non fornisce un wrapper diretto per tutte le classi di painting avanzate, è stata sfruttata l'interoperabilità diretta con le API native di JavaFX. Ad esempio, in `ObstacleView` e `PlayerView`, il riempimento delle forme non utilizza tinte unite ma la classe `javafx.scene.paint.ImagePattern`. 

Per far comunicare l'immagine caricata in ScalaFX con il pattern di JavaFX, è stata utilizzata la proprietà `.delegate`, che estrae l'oggetto Java sottostante:

```scala
import javafx.scene.paint.ImagePattern
import scalafx.scene.image.Image

private val textureImage = new Image(getClass.getResource("/ObstacleTexture.jpeg").toExternalForm)
private val obstaclePattern = new ImagePattern(textureImage.delegate)

val circleShape = new Circle:
  radius = rad
  fill = obstaclePattern
```

Questo stesso meccanismo è stato impiegato per assegnare texture differenti ai soldati in base alla fazione di appartenenza (`Team1.jpeg` e `Team2.jpeg`), calcolando dinamicamente il `teamIndex` durante la fase di spawn in `MainGUI`.

### Rendering delle Geometrie Complesse (Difference)
Le interazioni distruttive del proiettile con gli ostacoli (che nel Modello generano forme di tipo `Difference`) vengono tradotte graficamente in `ObstacleView` mediante sovrapposizione. 
Il motore di rendering disegna prima la forma base (Cerchio o Poligono) mappandone i vertici convertiti, dopodiché itera sull'insieme delle forme sottratte (i crateri dell'esplosione) istanziando per ciascuna un `HoleView`. Questo approccio visivo stratificato mantiene le performance elevate, in quanto delega il ritaglio al compositing dell'interfaccia anziché ricalcolare mesh poligonali complesse a runtime.


[**&larr; Design di dettaglio** ](./4-dettaglio.md) | **Implementazione** | [ **Testing &rarr;**](./6-testing.md)
<br>
[**Torna alla home**](../index.md)
