package nl.sajansen.kotlinsnakegame.multiplayer

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.multiplayer.json.GameDataJson
import nl.sajansen.kotlinsnakegame.multiplayer.json.PlayerDataJson
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.net.DatagramSocket
import java.util.logging.Logger

object Server : MessagingServer() {
    override val logger: Logger = Logger.getLogger(Server::class.java.name)

    private val clients: ArrayList<RemoteClient> = arrayListOf()

    override fun start() {
        logger.info("Creating server socket on port: ${Config.serverPort}")
        socket = DatagramSocket(Config.serverPort)

        super.start()
    }

    override fun handleReceivedCommand(command: Commands, client: RemoteClient) {
        when (command) {
            Commands.CONNECT -> addClient(client)
            Commands.DISCONNECT -> removeClient(client)
            Commands.ECHO -> send(Commands.ECHO, client)
            else -> return
        }
    }

    override fun handleReceivedMessage(message: String, client: RemoteClient) {}

    override fun handleReceivedObject(data: Any, client: RemoteClient) {
        when (data) {
            is PlayerDataJson -> processPlayerData(data, client)
        }
    }

    private fun addClient(client: RemoteClient) {
        logger.info("Registering client: $client")
        clients.add(client)
        send(Commands.OK, client)
    }

    private fun removeClient(client: RemoteClient) {
        logger.info("Removing client: $client")
        clients.removeIf { it.address == client.address && it.port == client.port }
        send(Commands.OK, client)
    }

    private fun processPlayerData(data: PlayerDataJson, client: RemoteClient) {
        println("Player data name: " + data.name)
    }

    fun sendGameData() {
        val players = Game.players.map {
            PlayerDataJson(
                name = it.name
            )
        }
        val data = GameDataJson(
            isEnded = false,
            players = players
        )

        clients.forEach { send(data, it) }
    }
}