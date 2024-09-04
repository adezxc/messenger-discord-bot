package org.example

import kotlinx.serialization.*
import kotlinx.datetime.Instant

@Serializable
data class Dump(val participants: MutableList<String> , val messages: MutableList<Message>)

@Serializable
data class Message(val senderName: String, val timestampMs: kotlinx.datetime.Instant, val content: String, val isGeoblockedForViewer: Boolean) {
    init {
        require(senderName.isNotEmpty()) { "name cannot be empty" }
        require(content.isNotEmpty()) {"content cannot be empty" }
    }

}

fun main() {
    val test = Message(senderName = "abc", timestampMs = Instant.fromEpochMilliseconds(1701858790155), "test", false)
    println(test)
}