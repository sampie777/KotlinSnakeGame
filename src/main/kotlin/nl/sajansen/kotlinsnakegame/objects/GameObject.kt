package nl.sajansen.kotlinsnakegame.objects

import java.awt.image.BufferedImage

interface GameObject {
    fun reset()
    fun step()
    fun paint(): BufferedImage
}