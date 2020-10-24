package nl.sajansen.kotlinsnakegame.multiplayer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import nl.sajansen.kotlinsnakegame.multiplayer.json.JsonMessage
import java.util.logging.Logger

val logger = Logger.getLogger("utils")

internal fun jsonBuilder(prettyPrint: Boolean = false): Gson {
    val builder = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .serializeNulls()

    if (prettyPrint) {
        builder.setPrettyPrinting()
    }

    return builder.create()
}

fun getObjectFromMessage(message: JsonMessage): Any? {
    if (message.data == null) {
        logger.info("Message object is null, cannot do something with this")
        return null
    }

    if (message.dataClass.isEmpty()) {
        logger.info("Message object has no class name specified, cannot do something with this")
        return null
    }

    val objectClass = Class.forName(message.dataClass)
    val objString = jsonBuilder().toJson(message.data)
    return Gson().fromJson(objString, objectClass)
}