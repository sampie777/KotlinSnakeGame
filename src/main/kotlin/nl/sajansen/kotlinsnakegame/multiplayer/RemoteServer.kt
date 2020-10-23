package nl.sajansen.kotlinsnakegame.multiplayer


import nl.sajansen.kotlinsnakegame.config.Config
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.logging.Logger

class RemoteServer(
    val address: InetAddress = InetAddress.getByName("localhost"),
    val port: Int = Config.serverPort
) :
    MessagingServer() {

    override val logger: Logger = Logger.getLogger(RemoteServer::class.java.name)
    override val id: Int
        get() = 1

    init {
        connect()
    }

    fun connect() {
        socket = DatagramSocket()
        isListening = socket != null && !socket!!.isClosed

        startListening()
    }

    fun disconnect() {
        stop()
    }

    override fun handleReceivedData(data: ByteArray, client: RemoteClient) {
        val string = String(data)
        logger.info("Message: $string")

        when (string) {
            "stop" -> stopListening()
            "echo" -> send("echo1".toByteArray())
        }
    }

    fun send(data: String): Boolean = send(data.toByteArray())
    fun send(data: ByteArray): Boolean = send(data, address, port)
}