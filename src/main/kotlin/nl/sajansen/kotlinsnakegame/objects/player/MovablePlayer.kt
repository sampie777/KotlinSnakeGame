package nl.sajansen.kotlinsnakegame.objects.player

interface MovablePlayer {
    var upKey: Int
    var rightKey: Int
    var downKey: Int
    var leftKey: Int

    fun setControls(up: Int, right: Int, down: Int, left: Int) {
        upKey = up
        rightKey = right
        downKey = down
        leftKey = left
    }
}