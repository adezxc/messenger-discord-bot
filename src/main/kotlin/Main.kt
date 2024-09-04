package org.example

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json


fun main() {
    val test = Message(senderName = "abc", timestampMs = Instant.fromEpochMilliseconds(1701858790155), "test", false)
    val testInput = """
    {
      "sender_name": "Test User",
      "timestamp_ms": 1705783232267,
      "content": "\u00c4\u00aesivaikint tave gal\u00c4\u0097t\u00c5\u00b3",
      "is_geoblocked_for_viewer": false
    }    """.trimIndent()

    val message = Json.encodeToString(MessageSerializer, test)

    println(message)
    val decodedMessage = Json.decodeFromString(Message.serializer(), testInput)
    println(decodedMessage)
}