
Game loop:
1. `Game()`: Increase game time
1. `Board()`: Spawn stars if any
1. `Board()`: `step()` each Player
1. `Board()`: `step()` each entity
1. `Board()`: Check collisions and call `collidedWith()` if needed

Players may extend the Entity interface. In the game loop `step()` and `reset()` (shared functions between Player and Entity interface) are only called once, for the Player interface.

In this way, a Player can extend a Sprite without extra hassle.

For each CollidableEntities, collisions are checked with any other Entity except for itself.