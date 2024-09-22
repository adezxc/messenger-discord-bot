package org.example

import java.security.MessageDigest
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID

fun getConnection(): Connection {
    val url = "jdbc:sqlite:sample.db"
    return DriverManager.getConnection(url)
}

fun insertDumpfile(dumpFile: DumpFile) {
    val connection = getConnection()
    try {
        connection.autoCommit = false

        val dumpFileStmt = connection.prepareStatement("""INSERT INTO dumpfiles(hash) VALUES(?)""")
        dumpFileStmt.setString(1, dumpFile.sha256sum)
        dumpFileStmt.execute()

        dumpFile.participants?.forEach{ connection.insertParticipant(it) }

        dumpFile.messages?.forEach{ message ->
            connection.insertMessage(message, dumpFile.sha256sum)

            message.photos?.forEach { photo -> connection.insertMedia(message.id, photo)}
            message.videos?.forEach { video -> connection.insertMedia(message.id, video)}
            message.reactions?.forEach { reaction -> connection.insertReaction(message.id, reaction)}
        }
    } catch (e:SQLException) {
        connection.rollback()
        println(e.message)
    } finally {
        connection.autoCommit = true
        connection.close()
    }
}

fun Connection.insertParticipant(participant : Participant) {
    try  {
        val stmt = prepareStatement("""INSERT OR IGNORE INTO participants(name) VALUES(?)""")
        stmt.setString(1, participant.name.toString())
        stmt.execute()
    } catch (e:SQLException) {
        throw e
    }
}

fun Connection.insertMessage(message : Message, dumpfileHash : String?) {
    try  {
        val stmt = prepareStatement("""INSERT OR IGNORE INTO messages(id, sender_name, content, timestamp_ms, dumpfile_hash) VALUES(?,?,?,?,?)""")
        stmt.setString(1, message.id.toString())
        stmt.setString(2, message.senderName.toString())
        stmt.setString(3, message.content.toString())
        stmt.setLong(4, message.timestampMs.toEpochMilliseconds())
        stmt.setString(5, dumpfileHash)
        stmt.execute()
    } catch (e:SQLException) {
        throw e
    }
}

fun Connection.insertReaction(messageUuid : String, reaction: Reaction) {
    try  {
        val stmt = prepareStatement("""INSERT OR IGNORE INTO reactions(message_id, actor, reaction) VALUES(?,?,?)""")
        stmt.setString(1, messageUuid)
        stmt.setString(2, reaction.actor.toString())
        stmt.setString(3, reaction.reaction.toString())
        stmt.execute()
    } catch (e:SQLException) {
        throw e
    }
}

fun Connection.insertMedia(messageUuid : String, media : Media) {
    try {
        val stmt = prepareStatement("""INSERT OR IGNORE INTO media(id, message_id, uri, timestamp_ms) VALUES (?,?,?,?)""")
        stmt.setString(1, media.id)
        stmt.setString(2, messageUuid)
        stmt.setString(3, media.uri)
        stmt.setLong(4, media.creationMs.toEpochMilliseconds())
        stmt.execute()
    } catch (e:SQLException) {
        throw e
    }
}