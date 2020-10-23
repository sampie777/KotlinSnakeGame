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

    enum class ConnectionState {
        NOT_CONNECTED,
        CONNECTING,
        CONNECTED
    }

    private var connectionState = ConnectionState.NOT_CONNECTED

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
        connectionState = ConnectionState.NOT_CONNECTED
    }
    override fun handleReceivedCommand(message: JsonMessage, client: RemoteClient) {
        when (message.command) {
            Commands.OK -> processOkCommand(message)
            Commands.ECHO -> send(Commands.ECHO)
            else -> return
        }
    }

    private fun processOkCommand(message: JsonMessage) {
        when (connectionState) {
            ConnectionState.CONNECTING -> finalizeConnection()
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
        connectionState = ConnectionState.CONNECTING
        send(Commands.CONNECT)
    }

    private fun finalizeConnection() {
        logger.info("Connected to the server")
        connectionState = ConnectionState.CONNECTED
    }

    private fun processGameData(data: GameDataJson) {
        //..
        println("Game is ended: ${data.isEnded}")
        print("Other players: ")
        val otherPlayers = data.players.filter { it.name != "Henk" }
        println(otherPlayers)

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