package nl.sajansen.kotlinsnakegame.multiplayer


import com.google.gson.Gson
import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.multiplayer.json.JsonMessage
import nl.sajansen.kotlinsnakegame.multiplayer.json.jsonBuilder
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.logging.Logger

abstract class MessagingServer {
    protected abstract val logger: Logger

    @Volatile
    var isListening = false
    protected var socket: DatagramSocket? = null
    private var listenThread: Thread? = null

    open fun start() {
        isListening = socket != null && !socket!!.isClosed

        startListening()
    }

    fun startListening() {
        logger.info("Starting listening thread")
        listenThread = Thread { listen() }
        listenThread!!.start()
    }

    fun join() {
        logger.info("Joining listen thread")
        listenThread?.join()
    }

    open fun stop() {
        logger.info("Stopping socket")
        stopListening()
        socket?.close()
    }

    protected fun stopListening() {
        logger.info("Stopping loop")
        isListening = false
    }

    private fun listen() {
        isListening = true
        while (isListening) {
            loop()
        }

        stop()
    }

    protected open fun loop() {
        if (socket == null || socket!!.isClosed) {
            logger.info("Server is not running, can't receive data")
            isListening = false
            return
        }

        val buffer = ByteArray(Config.maxDataPacketLength)
        val packet = DatagramPacket(buffer, buffer.size)

        logger.info("Waiting for message from clients...")

        try {
            socket?.receive(packet)
        } catch (e: SocketException) {
            if (e.message == "Socket closed") {
                logger.info("Socket closed")
                return
            }

            logger.warning("Waiting for socket messages cancelled")
            e.printStackTrace()
            return
        }

        logger.info("Received packet from: IP=${packet.address}, port: ${packet.port}")
        handleReceivedData(packet.data.sliceArray((0 until packet.length)), RemoteClient(packet.address, packet.port))
    }

    fun handleReceivedData(data: ByteArray, client: RemoteClient) {
        val json = String(data)
        logger.info("Received json message: $json")

        val message = Gson().fromJson(json, JsonMessage::class.java)

        if (message.command != null) {
            handleReceivedCommand(message, client)
        } else if (message.obj != null && message.objClassName != null) {
            handleReceivedObject(message, client)
        } else if (message.message != null) {
            handleReceivedMessage(message, client)
        } else {
            logger.info("Can't do anything with an empty json message")
        }
    }

    abstract fun handleReceivedCommand(message: JsonMessage, client: RemoteClient)
    abstract fun handleReceivedMessage(message: JsonMessage, client: RemoteClient)
    abstract fun handleReceivedObject(message: JsonMessage, client: RemoteClient)

    fun sendRaw(data: String, client: RemoteClient): Boolean = send(data.toByteArray(), client)

    fun sendObject(obj: Any?, client: RemoteClient): Boolean {
        val jsonMessage = if (obj == null) {
            JsonMessage(obj = null)
        } else {
            JsonMessage(obj = obj, objClassName = obj::class.java.name)
        }
        return send(jsonMessage, client)
    }

    fun send(command: Commands, client: RemoteClient, message: String = ""): Boolean =
        send(JsonMessage(command = command, message = message), client)

    fun send(message: String, client: RemoteClient): Boolean = send(JsonMessage(message = message), client)
    fun send(data: ByteArray, client: RemoteClient): Boolean = send(data, client.address, client.port)

    fun send(message: JsonMessage, client: RemoteClient): Boolean {
        val json = jsonBuilder().toJson(message)
        return sendRaw(json, client)
    }

    fun send(data: ByteArray, address: InetAddress, port: Int): Boolean {
        if (!isListening || socket == null || socket!!.isClosed) {
            logger.info("Socket is not running, can't send data")
            isListening = false
            return false
        }

        logger.info("Sending data to $address:$port")
        val packet = DatagramPacket(data, data.size, address, port)

        try {
            socket!!.send(packet)
        } catch (e: Exception) {
            logger.severe("Failed to send package to $address:$port with data: ${String(data)}")
            return false
        }
        return true
    }
}