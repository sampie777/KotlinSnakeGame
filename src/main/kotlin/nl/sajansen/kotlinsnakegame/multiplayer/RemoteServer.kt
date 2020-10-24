package nl.sajansen.kotlinsnakegame.multiplayer


import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.multiplayer.json.GameDataJson
import nl.sajansen.kotlinsnakegame.objects.player.HumanPlayer
import nl.sajansen.kotlinsnakegame.objects.player.Player
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

    override fun handleReceivedCommand(command: Commands, client: RemoteClient) {
        when (command) {
            Commands.OK -> processOkCommand()
            Commands.ECHO -> send(Commands.ECHO)
            else -> return
        }
    }

    private fun processOkCommand() {
        when (connectionState) {
            ConnectionState.CONNECTING -> finalizeConnection()
            else -> return
        }
    }

    override fun handleReceivedMessage(message: String, client: RemoteClient) {}

    override fun handleReceivedObject(data: Any, client: RemoteClient) {
        when (data) {
            is GameDataJson -> processGameData(data)
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
        print("Players: ")
        println(getPlayersFromData(data))

        sendPlayerData()
    }

    private fun getPlayersFromData(data: GameDataJson): List<Player> {
        return data.players
            .map {
                val objectClass = Class.forName(it.className)
                val instance = objectClass.newInstance() as Player
                instance.fromPlayerDataJson(it)
            }
    }

    fun sendPlayerData() {
        val data = HumanPlayer().toPlayerDataJson()

        send(data)
    }

    private fun send(obj: Any): Boolean = send(obj, remoteClient)
}