package nl.sajansen.kotlinsnakegame.multiplayer


import nl.sajansen.kotlinsnakegame.config.Config
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.logging.Logger

abstract class MessagingServer {
    protected abstract val logger: Logger
    protected abstract val id: Int

    @Volatile
    var isListening = false
    protected var socket: DatagramSocket? = null
    private var listenThread: Thread? = null

    fun startListening() {
        logger.info("$id: Starting listening thread")
        listenThread = Thread { listen() }
        listenThread!!.start()
    }

    fun join() {
        logger.info("$id: Joining listen thread")
        listenThread?.join()
    }

    open fun stop() {
        logger.info("$id: Stopping socket")
        stopListening()
        socket?.close()
    }

    protected fun stopListening() {
        logger.info("$id: Stopping loop")
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
            logger.info("$id: Server is not running, can't receive data")
            isListening = false
            return
        }

        val buffer = ByteArray(Config.maxDataPacketLength)
        val packet = DatagramPacket(buffer, buffer.size)

        logger.info("$id: Waiting for message from clients...")

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

        logger.info("$id: Received packet from: IP=${packet.address}, port: ${packet.port}")
        handleReceivedData(packet.data.sliceArray((0 until packet.length)), RemoteClient(packet.address, packet.port))
    }

    abstract fun handleReceivedData(data: ByteArray, client: RemoteClient)

    fun send(data: String, client: RemoteClient): Boolean = send(data.toByteArray(), client)
    fun send(data: ByteArray, client: RemoteClient): Boolean = send(data, client.address, client.port)

    fun send(data: ByteArray, address: InetAddress, port: Int): Boolean {
        if (!isListening || socket == null || socket!!.isClosed) {
            logger.info("$id: Socket is not running, can't send data")
            isListening = false
            return false
        }

        logger.info("$id: Sending data to $address:$port")
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