package nl.sajansen.kotlinsnakegame.objects.player

interface Player {
    var name: String
    var score: Int

    fun reset()
    fun step()
}