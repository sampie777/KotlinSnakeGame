package nl.sajansen.kotlinsnakegame.objects.player

import nl.sajansen.kotlinsnakegame.multiplayer.json.PlayerDataJson

interface Player {
    var name: String
    var score: Int

    fun reset()
    fun step()

    fun <T:Player> changeTo(clazz: Class<T>): T {
        val newPlayer = clazz.newInstance()
        newPlayer.copyFrom(this)
        return newPlayer
    }

    fun copyFrom(player: Player) {
        name = player.name

        if (player is MovablePlayer && this is MovablePlayer) {
            upKey = player.upKey
            rightKey = player.rightKey
            downKey = player.downKey
            leftKey = player.leftKey
        }
    }

    fun toPlayerDataJson(): PlayerDataJson
    fun fromPlayerDataJson(data: PlayerDataJson): Player
}