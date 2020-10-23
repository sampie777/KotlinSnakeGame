package nl.sajansen.kotlinsnakegame.multiplayer.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.logging.Logger

val logger = Logger.getLogger("utils")

internal fun jsonBuilder(prettyPrint: Boolean = true): Gson {
    val builder = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .serializeNulls()

    if (prettyPrint) {
        builder.setPrettyPrinting()
    }

    return builder.create()
}

fun getObjectFromMessage(message: JsonMessage): Any? {
    if (message.obj == null) {
        logger.info("Message object is null, cannot do something with this")
        return null
    }

    if (message.objClassName == null || message.objClassName.isEmpty()) {
        logger.info("Message object has no class name specified, cannot do something with this")
        return null
    }

    val objectClass = Class.forName(message.objClassName)
    val objString = jsonBuilder().toJson(message.obj)
    return Gson().fromJson(objString, objectClass)
}