package nl.sajansen.kotlinsnakegame.objects.game


import nl.sajansen.kotlinsnakegame.events.EventHub
import java.util.logging.Logger

class GameState {
    private val logger = Logger.getLogger(GameState::class.java.name)

    var runningState: GameRunningState = GameRunningState.RESET
        set(value) {
            field = value
            EventHub.runningStateChanged()
        }
}

enum class GameRunningState {
    RESET,
    STARTED,
    PAUSED,
    ENDED
}