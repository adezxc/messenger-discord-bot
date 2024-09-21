package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File


@kotlinx.serialization.ExperimentalSerializationApi
fun main() {
    val format = Json { ignoreUnknownKeys = true; prettyPrint = true; explicitNulls = true}

    val messageDump = format.decodeFromStream<DumpFile>(File("data/message_1.json").inputStream())
    println("test")
    messageDump.participants?.forEach{ getConnection().insertParticipant(it) }
    messageDump.messages?.forEach { getConnection().insertMessage(it) }
}
