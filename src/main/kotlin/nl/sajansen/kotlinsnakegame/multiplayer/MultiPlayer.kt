package nl.sajansen.kotlinsnakegame.multiplayer

import nl.sajansen.kotlinsnakegame.events.ApplicationEventListener
import nl.sajansen.kotlinsnakegame.events.EventHub

object MultiPlayer : ApplicationEventListener {
    var isServer = false
    var isRemote = false

    private var remoteServer: RemoteServer? = null

    init {
        EventHub.register(this)
    }

    fun step() {
        if (isServer) {
            Server.sendGameData()
        } else if (isRemote) {
            remoteServer?.sendPlayerData()
        }
    }

    fun startAsServer() {
        isServer = true
        Server.start()
    }

    override fun onShutDown() {
        if (isServer) {
            Server.stop()
            Server.join()
        }
        if (isRemote){
            remoteServer?.stop()
            remoteServer?.join()
        }
    }
}