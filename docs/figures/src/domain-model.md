---
config:
  theme: redux
---
classDiagram
direction TB
    class Entity {
	    +Position pos 
    }

    class Shape{
        +List 
    }
    class Function{
        +Pair coords
        +expression
    }
    class Trajectory{
        +Function func
    }

    class Figure {
	    +Shape shape
	    +belongs(Position)
    }

    class Bullet {
    }

    class Obstacle {
        +Set holes
    }

    class Player {
	    +String name
    }

    class PowerUp {
	    +effect
        +int duration
    }

    class Position {
	    +Int x
	    +Int y
    }

    Entity <|-- Figure
    Entity <|-- Bullet
    Figure <|-- Obstacle
    Figure <|-- Player
    Figure <|-- PowerUp
    Function <|-- Trajectory
