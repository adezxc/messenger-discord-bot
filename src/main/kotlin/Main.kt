package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File


@kotlinx.serialization.ExperimentalSerializationApi
fun main() {
    val format = Json { ignoreUnknownKeys = true; prettyPrint = true; coerceInputValues = true }

    val test = """
        {
        "messages" : [{
            "sender_name": "Adam Jasinski",
            "timestamp_ms": 1701858897578,
            "content": "faking fak",
            "is_geoblocked_for_viewer": false
        }]
        }
    """.trimIndent()

    val dump = format.decodeFromString<DumpFile>(test)

    println(format.encodeToString(dump))

    //println(format.encodeToString(dump))
}