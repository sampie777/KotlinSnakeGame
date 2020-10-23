package nl.sajansen.kotlinsnakegame.multiplayer


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.multiplayer.json.GameDataJson
import nl.sajansen.kotlinsnakegame.multiplayer.json.JsonMessage
import nl.sajansen.kotlinsnakegame.multiplayer.json.PlayerDataJson
import nl.sajansen.kotlinsnakegame.multiplayer.json.getObjectFromMessage
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.logging.Logger

class RemoteServer(
    address: InetAddress = InetAddress.getByName("localhost"),
    port: Int = Config.serverPort
) :
    MessagingServer() {

    override val logger: Logger = Logger.getLogger(RemoteServer::class.java.name)

    private val remoteClient = RemoteClient(address, port)
    private var isConnected = false

    init {
        start()
    }

    override fun start() {
        socket = DatagramSocket()
        super.start()

        requestConnection()
    }

    override fun stop() {
        send(Commands.DISCONNECT)
        super.stop()
        isConnected = false
    }
    override fun handleReceivedCommand(message: JsonMessage, client: RemoteClient) {
        when (message.command) {
            Commands.OK -> finalizeConnection()
            Commands.ECHO -> send(Commands.ECHO)
            else -> return
        }
    }

    override fun handleReceivedMessage(message: JsonMessage, client: RemoteClient) {}

    override fun handleReceivedObject(message: JsonMessage, client: RemoteClient) {
        val obj = getObjectFromMessage(message) ?: return

        when (obj) {
            is GameDataJson -> processGameData(obj)
        }
    }

    private fun requestConnection() {
        logger.info("Sending connection request")
        send(Commands.CONNECT)
    }

    private fun finalizeConnection() {
        logger.info("Connected to the server")
        isConnected = true
    }

    private fun processGameData(data: GameDataJson) {
        //..
        sendPlayerData()
    }

    fun sendPlayerData() {
        val data = PlayerDataJson("Henk")

        sendObject(data)
    }

    fun send(command: Commands, message: String = ""): Boolean =
        send(JsonMessage(command = command, message = message), remoteClient)
    fun send(message: String): Boolean = send(message, remoteClient)
    fun sendObject(obj: Any?): Boolean = sendObject(obj, remoteClient)
}