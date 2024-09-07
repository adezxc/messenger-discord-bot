package org.example

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.Database
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File


@kotlinx.serialization.ExperimentalSerializationApi
fun main() {

    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:test.db")
    Database.Schema.create(driver)
    val format = Json { ignoreUnknownKeys = true; prettyPrint = true; explicitNulls = true}

    val test = """
        {
        "participants" : [{
            "name": "test"
        }],
        "messages" : [{
            "sender_name": "Adam",
            "timestamp_ms": 1701858897578,
            "content": "faking fak",
            "is_geoblocked_for_viewer": false
        }]
        }
    """.trimIndent()

    val dump = format.decodeFromStream<DumpFile>(File("data/message_1.json").inputStream())
    val dump2 = format.decodeFromString<DumpFile>(test)

    println(format.encodeToString(dump))
    println(format.encodeToString(dump2))

    //println(format.encodeToString(dump))
}