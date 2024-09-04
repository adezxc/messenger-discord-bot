package org.example

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json


fun main() {
    val photos = mutableListOf(Photo("abc", Instant.fromEpochMilliseconds(180)))
    val reactions = mutableListOf(Reaction("abc", "test"))
    val test = Message(senderName = "abc", timestampMs = Instant.fromEpochMilliseconds(1701858790155), photos, "test", reactions, false)
    val testInput = """
    {
      "sender_name": "\u00c5\u00bdinys",
      "timestamp_ms": 1705781097791,
      "content": "Tai pakeisk j\u00c4\u00af - jis ir kat\u00c4\u0097m nealergi\u00c5\u00a1kas, ir dar gautum dovan\u00c5\u00b3",
      "reactions": [
        {
          "reaction": "\u00f0\u009f\u0098\u0086",
          "actor": "Test"
        },
        {
          "reaction": "\u00f0\u009f\u0098\u0086",
          "actor": "Test 2"
        },
        {
          "reaction": "\u00f0\u009f\u0098\u0086",
          "actor": "Abc"
        },
        {
          "reaction": "\u00f0\u009f\u0098\u0086",
          "actor": "Tasd"
        },
        {
          "reaction": "\u00f0\u009f\u0091\u008d",
          "actor": "Test"
        },
        {
          "reaction": "\u00f0\u009f\u0098\u0086",
          "actor": "Pa\u00c4\u0097"
        }
      ],
      "is_geoblocked_for_viewer": false
    }    """.trimIndent()

    val message = Json.encodeToString(MessageSerializer, test)

    println(message)
    val decodedMessage = Json.decodeFromString(Message.serializer(), testInput)
    println(decodedMessage)
}