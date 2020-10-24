package nl.sajansen.kotlinsnakegame.multiplayer


import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.multiplayer.json.JsonMessage
import java.io.EOFException
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
        handleReceivedData(
            packet.data.sliceArray((0 until packet.length)),
            RemoteClient(packet.address, packet.port)
        )
    }

    fun handleReceivedData(bytes: ByteArray, client: RemoteClient) {
        val json = String(bytes)
        logger.info("Received json message: $json")

        val message = try {
            Gson().fromJson(json, JsonMessage::class.java)
        } catch (e: EOFException) {
            logger.severe("Increase buffer size: Config.maxDataPacketLength")
            throw e
        } catch (e: JsonSyntaxException) {
            val exceptionMessage = e.message
            if (exceptionMessage != null && exceptionMessage.contains("EOFException")) {
                logger.severe("Increase buffer size: Config.maxDataPacketLength")
            }
            throw e
        }

        val data = getObjectFromMessage(message) ?: return

        when (data) {
            is Commands -> handleReceivedCommand(data, client)
            is String -> handleReceivedMessage(data, client)
            else -> handleReceivedObject(data, client)
        }
    }

    abstract fun handleReceivedCommand(command: Commands, client: RemoteClient)
    abstract fun handleReceivedMessage(message: String, client: RemoteClient)
    abstract fun handleReceivedObject(data: Any, client: RemoteClient)

    fun send(message: JsonMessage, client: RemoteClient): Boolean {
        val json = jsonBuilder().toJson(message)
        return sendRaw(json, client)
    }

    fun send(obj: Any, client: RemoteClient): Boolean =
        send(JsonMessage(data = obj, dataClass = obj::class.java.name), client)

    fun sendRaw(data: String, client: RemoteClient): Boolean = send(data.toByteArray(), client)

    fun send(data: ByteArray, client: RemoteClient): Boolean = send(data, client.address, client.port)

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