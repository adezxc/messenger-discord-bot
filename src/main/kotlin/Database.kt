package org.example

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

fun getConnection(): Connection {
    val url = "jdbc:sqlite:sample.db"
    return DriverManager.getConnection(url)
}

fun Connection.insertParticipant(participant : Participant) {
    try  {
        val stmt = prepareStatement("""INSERT OR IGNORE INTO participants(name) VALUES(?)""")
        stmt.setString(1, participant.name.toString())
        stmt.execute()
    } catch (e:SQLException) {
        println(e.message)
    }
}

fun Connection.insertMessage(message : Message) {
    try  {
        val stmt = prepareStatement("""INSERT OR IGNORE INTO messages(id, sender_name, content, timestamp_ms) VALUES(?,?,?,?)""")
        stmt.setString(1, message.id)
        stmt.setString(2, message.senderName.toString())
        stmt.setString(3, message.content.toString())
        stmt.setLong(4, message.timestampMs.toEpochMilliseconds())
        stmt.execute()
    } catch (e:SQLException) {
        println(e.message)
    }
}

//fun Connection.insertReaction(reaction: LatinString, actor: LatinString) {
//    try  {
//        val stmt = prepareStatement("""INSERT OR IGNORE INTO reactions(actor, message_id, reaction) VALUES(?,?,?)""")
//        stmt.setString(1, message.senderName.toString())
//        stmt.setString(2, message.content.toString())
//        stmt.setLong(3, message.timestampMs.toEpochMilliseconds())
//        stmt.execute()
//    } catch (e:SQLException) {
//        println(e.message)
//    }
//}
