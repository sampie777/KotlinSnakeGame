package nl.sajansen.kotlinsnakegame.objects.player

import nl.sajansen.kotlinsnakegame.objects.Entity

interface Player : Entity {
    var name: String
    var score: Int
}