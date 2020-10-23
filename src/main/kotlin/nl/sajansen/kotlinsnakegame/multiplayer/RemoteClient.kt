package nl.sajansen.kotlinsnakegame.multiplayer


import java.net.InetAddress
import java.util.logging.Logger

data class RemoteClient(val address: InetAddress, val port: Int) {
    private val logger = Logger.getLogger(RemoteClient::class.java.name)

    override fun toString(): String {
        return "ServerClient(address=$address, port=$port)"
    }
}