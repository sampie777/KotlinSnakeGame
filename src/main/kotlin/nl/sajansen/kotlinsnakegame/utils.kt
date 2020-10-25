package nl.sajansen.kotlinsnakegame

import nl.sajansen.kotlinsnakegame.config.Config
import nl.sajansen.kotlinsnakegame.events.EventHub
import nl.sajansen.kotlinsnakegame.gui.mainFrame.MainFrame
import java.awt.Color
import java.awt.event.KeyEvent
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import kotlin.system.exitProcess

private val logger: Logger = Logger.getLogger("utils")

fun getTimeAsClock(seconds: Long): String {
    var positiveValue = seconds

    var signString = ""
    if (seconds < 0) {
        positiveValue *= -1
        signString = "-"
    }

    val timerHours = positiveValue / 3600
    val timerMinutes = (positiveValue - timerHours * 3600) / 60
    val timerSeconds = positiveValue - timerHours * 3600 - timerMinutes * 60
    return String.format("%s%d:%02d:%02d", signString, timerHours, timerMinutes, timerSeconds)
}

@Throws(UnsupportedEncodingException::class)
fun getCurrentJarDirectory(caller: Any): File {
    val url = caller::class.java.protectionDomain.codeSource.location
    val jarPath = URLDecoder.decode(url.file, "UTF-8")
    return File(jarPath).parentFile
}

fun isAddressLocalhost(address: String): Boolean {
    return address.contains("localhost") || address.contains("127.0.0.1")
}

fun exitApplication() {
    logger.info("Shutting down application")

    EventHub.onShutDown()

    Config.save()

    logger.info("Shutdown finished")
    exitProcess(0)
}

fun brightness(color: Color): Double {
    return 0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue
}

fun decodeURI(uri: String): String {
    return URLDecoder.decode(uri, StandardCharsets.UTF_8.name())
}

fun getReadableFileSize(file: File): String {
    return when {
        file.length() > 1024 * 1024 -> {
            val fileSize = file.length().toDouble() / (1024 * 1024)
            String.format("%.2f MB", fileSize)
        }
        file.length() > 1024 -> {
            val fileSize = file.length().toDouble() / 1024
            String.format("%.2f kB", fileSize)
        }
        else -> {
            String.format("%d bytes", file.length())
        }
    }
}

fun getFileNameWithoutExtension(file: File): String {
    return file.name.substring(0, file.name.lastIndexOf('.'))
}

fun getFileExtension(file: File): String {
    return file.name.substring(file.name.lastIndexOf('.') + 1)
}

fun Date.format(format: String): String? = SimpleDateFormat(format).format(this)


fun createKeyEvent(keyCode: Int, ctrl: Boolean = false, shift: Boolean = false, alt: Boolean = false): KeyEvent {
    var modifiers = 0
    if (ctrl) {
        modifiers = modifiers.or(KeyEvent.CTRL_MASK)
    }
    if (shift) {
        modifiers = modifiers.or(KeyEvent.SHIFT_MASK)
    }
    if (alt) {
        modifiers = modifiers.or(KeyEvent.ALT_MASK)
    }
    return KeyEvent(MainFrame.getInstance()!!, 0, 0, modifiers, keyCode, '0')
}


fun keyEventToString(e: KeyEvent?): String {
    if (e == null) {
        return ""
    }

    return listOf(
        KeyEvent.getKeyModifiersText(e.modifiers),
        KeyEvent.getKeyText(e.keyCode)
    )
        .filter { !it.isBlank() }
        .joinToString("+")
}
