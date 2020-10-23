package nl.sajansen.kotlinsnakegame.multiplayer

import nl.sajansen.kotlinsnakegame.config.Config
import java.net.DatagramSocket
import java.util.logging.Logger

object Server : MessagingServer() {
    override val logger: Logger = Logger.getLogger(Server::class.java.name)
    override val id: Int
        get() = 2

    fun start() {
        logger.info("Creating server socket on port: ${Config.serverPort}")
        socket = DatagramSocket(Config.serverPort)
        isListening = socket != null && !socket!!.isClosed

        startListening()
    }

    override fun handleReceivedData(data: ByteArray, client: RemoteClient) {
        val string = String(data)
        logger.info("Message: $string")

        when (string) {
            "stop" -> stopListening()
            "echo" -> send("echo".toByteArray(), client)
        }
    }
}