package org.example

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*
import java.util.UUID
import kotlin.text.Charsets

@Serializable
data class DumpFile(
    var sha256sum: String? = null,
    val participants: List<Participant>? = null,
    val messages: List<Message>? = null,
    val title: LatinString,
    @SerialName("thread_path") val threadPath: LatinString,
    val image: Media? = null,
)

@Serializable
data class Participant(
    val name: LatinString,
)

@Serializable
data class Message(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("sender_name") @Required val senderName: LatinString,
    @SerialName("timestamp_ms") @Serializable(with = InstantSerializer::class) @Required val timestampMs: Instant,
    val videos: List<Media>? = null,
    val photos: List<Media>? = null,
    val content: LatinString? = null,
    val reactions: List<Reaction>? = null,
    @SerialName("is_geoblocked_for_viewer") val isGeoBlockedForViewer: Boolean
)

@Serializable
data class Media(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    @Serializable(with = InstantSerializer::class) @SerialName("creation_timestamp") val creationMs: Instant,
)

@Serializable
data class Reaction(
    val reaction: LatinString,
    val actor: LatinString,
)

@Serializable(with = LatinSerializer::class)
data class LatinString(val value: String) {
    override fun toString(): String = value
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("creation_ms", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilliseconds())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochMilliseconds(decoder.decodeLong())
    }
}

object LatinSerializer : KSerializer<LatinString> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Latin", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LatinString) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LatinString {
        val encodedString = decoder.decodeString()
        return LatinString(encodedString.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8))
    }
}