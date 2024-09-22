package org.example

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.security.MessageDigest
import java.security.DigestInputStream


@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val startTime = System.nanoTime()
    if (args.isEmpty()) {
        println("ERROR: Data file path expected")
    }
    if (args.size == 1) {
        val dumpFile = readMessageFile(args[0])
        insertDumpfile(dumpFile)
    }

    val endTime = System.nanoTime()
    val durationMs = (endTime - startTime) / 1_000_000
    println(durationMs)
}

@kotlinx.serialization.ExperimentalSerializationApi
fun readMessageFile(filepath: String): DumpFile {
    val format = Json { ignoreUnknownKeys = true; prettyPrint = true; explicitNulls = true }

    val messageFile = File(filepath)

    val sha256Digest = MessageDigest.getInstance("SHA-256")

    val digestStream = DigestInputStream(messageFile.inputStream(), sha256Digest)
    val messageDump = format.decodeFromStream<DumpFile>(digestStream)

    val hashBytes = sha256Digest.digest()
    val sha256sum = hashBytes.joinToString("") { "%02x".format(it) }

    messageDump.sha256sum = sha256sum

    return messageDump
}
