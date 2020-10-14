package nl.sajansen.kotlinsnakegame.objects.game

import nl.sajansen.kotlinsnakegame.config.Config
import java.util.*
import java.util.logging.Logger

object GameStepTimer {
    private val logger = Logger.getLogger(GameStepTimer::class.java.name)

    private var timer = Timer()

    @Volatile
    private var isUpdating = false

    fun restart() {
        stop()
        start()
    }

    private fun start() {
        logger.info("Scheduling GameStepTimer every ${1000 / Config.stepPerSeconds} milliseconds")
        timer.schedule(object : TimerTask() {
            override fun run() {
                updateTimerStep()
            }
        }, 0, 1000 / Config.stepPerSeconds)
    }

    private fun updateTimerStep() {
        if (isUpdating) {
            logger.info("GameStepTimer is still updating...")
            return
        }

        isUpdating = true
        Game.step()
        isUpdating = false
    }

    fun stop() {
        try {
            logger.info("Trying to cancel GameStepTimer")
            timer.cancel()
            timer = Timer()
            logger.info("GameStepTimer canceled")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}