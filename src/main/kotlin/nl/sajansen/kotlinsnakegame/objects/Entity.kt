package nl.sajansen.kotlinsnakegame.objects

import nl.sajansen.kotlinsnakegame.objects.game.Game

interface Entity {
    fun reset()
    fun step()
    fun destroy() {
        Game.board.entities.remove(this)
    }
}