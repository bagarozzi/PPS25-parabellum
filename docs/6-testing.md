# Testing
Il testing è stato effettuato tramite la libreria ScalaTest e mira a verificare i comportamenti specifici di alcuni
componenti critici del gioco.

Sono stati scritti test per verificare il funzionamento del parser, in particolare un test per ciascuna funzione elementare e per qualche combinazione di parentesi, tutto ciò in stile BDD.

```Scala
"A parser" should "parse a sine function" in {
    val func: Function = FunctionParser.parse("sin(x) + 3") match
        case Right(func) => func
        case Left(e) => Function(x => x)
    assert(func(2) === (math.sin(2) + 3))
}
```

I test di unità sono stati utili per verificare la correttezza nel rilevamento delle collisioni ed i loro casi particolari. 
Producendo un insieme di eventi, risulta facile compararli con gli eventi attesi da una specifica situazione.
Oltre ai casi di impatto, sono stati verificati anche alcuni casi con Power-Up, che producono eventi alternativi.

```Scala
"Collisions" should "detect a projectile with piecing powerUp hitting a figure" in {
    val projectile = Projectile.createProjectile(Position(0, 0), Function(x => x), Positive, normalImpactEffect(), Some(Piercing(Position(0,0))))
    val obs = Obstacle(Position(1, 0), Circle(Position(1, 0), 1))
    val secondObs = Obstacle(Position(10, 10), Circle(Position(10, 10), 1))
    assert(CollisionDetector.detectCollision(projectile, Set(obs, secondObs))
    .map {
        case DamageObstacle(o, Circle(pos, 0.2)) if o == obs => true
        case DamageObstacle(o, _) if o != obs => false
        case DestroyProjectile() => true
        case _ => false
    }.forall(identity))
}
```

Infine sono stati creati test per la verifica del posizionamento durante la generazione della mappa.
___
___
[**&larr; Implementazione** ](./5-implementazione.md) | **Testing** | [ **Retrospettiva &rarr;**](./7-retrospettiva.md)
<br>
[**Torna alla home**](../index.md)