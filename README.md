
Game loop:
1. `Game()`: Increase game time
1. `Board()`: Spawn stars if any
1. `Board()`: `step()` each Player
1. `Board()`: `step()` each entity
1. `Board()`: Check collisions and call `collidedWith()` if needed

Players may extend the Entity interface. In the game loop `step()` and `reset()` (shared functions between Player and Entity interface) are only called once, for the Player interface.

In this way, a Player can extend a Sprite without extra hassle.

For each CollidableEntities, collisions are checked with any other Entity except for itself.

All child classes of Sprite must call `super.reset()` when overriding the `reset()` method. If they don't, sprite animation will be delayed when restarting the game.

Sprite images are automatically buffered when the sprite image is recalled. This is done globally. 