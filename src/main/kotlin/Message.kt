package org.example

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*
import kotlin.text.Charsets

@Serializable
data class DumpFile(
    val messages: List<Message>,
)

@Serializable(with = ParticipantSerializer::class)
data class Participant(
    val name: String,
)

@Serializable(with = MessageSerializer::class)
data class Message(
    val senderName: String,
    val timestampMs: Instant,
    val videos: List<Media>,
    val photos: List<Media>,
    val content: String,
    val reactions: List<Reaction>,
    val isGeoBlockedForViewer: Boolean
) {
    init {
        //require(senderName.isNotEmpty()) { "name cannot be empty" }
        //require(content.isNotEmpty()) { "content cannot be empty" }
    }
}

@Serializable(with = MediaSerializer::class)
data class Media(
    val uri: String,
    val creationMs: Instant,
) {
    init {
        //require(uri.isNotEmpty()) { "name cannot be empty" }
    }
}

@Serializable(with = ReactionSerializer::class)
data class Reaction(
    val reaction: String,
    val actor: String,
) {
    init {
        require(reaction.isNotEmpty()) { "reaction cannot be empty" }
        require(actor.isNotEmpty()) { "actor cannot be empty" }
    }
}

object ParticipantSerializer : KSerializer<Participant> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Participant") {
            element<String>("name")
        }

    override fun serialize(encoder: Encoder, value: Participant) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
        }
    }

    override fun deserialize(decoder: Decoder): Participant {
        var name = ""
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, 0)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return Participant(name.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8))
    }
}

object MessageSerializer : KSerializer<Message> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Message") {
        element<String>("sender_name")
        element<Long>("timestamp_ms")
        element<List<Media>>("videos")
        element<List<Media>>("photos")
        element<String>("content")
        element<List<Reaction>>("reactions")
        element<Boolean>("is_geoblocked_for_viewer")
    }

    override fun serialize(encoder: Encoder, value: Message) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.senderName)
            encodeLongElement(descriptor, 1, value.timestampMs.toEpochMilliseconds())
            ListSerializer(MediaSerializer).serialize(encoder, value.videos)
            ListSerializer(MediaSerializer).serialize(encoder, value.photos)
            encodeStringElement(descriptor, 3, value.content)
            ListSerializer(ReactionSerializer).serialize(encoder, value.reactions)
            encodeBooleanElement(descriptor, 5, value.isGeoBlockedForViewer)
        }

    override fun deserialize(decoder: Decoder): Message {
        var senderName = ""
        var timestampMs: Instant = Instant.fromEpochMilliseconds(0)
        val mediaSerializer = ListSerializer(Media.serializer())
        var videos: List<Media> = mutableListOf()
        var photos: List<Media> = mutableListOf()
        var content = ""
        val reactionSerializer = ListSerializer(Reaction.serializer())
        var reactions: List<Reaction> = mutableListOf()
        var isGeoblockedForViewer = false
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> senderName =
                        decodeStringElement(descriptor, 0).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

                    1 -> timestampMs = Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
                    2 -> videos = mediaSerializer.deserialize(decoder)
                    3 -> photos = mediaSerializer.deserialize(decoder)
                    4 -> content =
                        decodeStringElement(descriptor, 3).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

                    5 -> reactions = reactionSerializer.deserialize(decoder)
                    6 -> isGeoblockedForViewer = decodeBooleanElement(descriptor, 4)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return Message(senderName, timestampMs, videos, photos, content, reactions, isGeoblockedForViewer)
    }
}


object MediaSerializer: KSerializer<Media> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Photo") {
        element<String>("uri")
        element<Long>("creation_timestamp")
    }

    override fun serialize(encoder: Encoder, value: Media) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.uri)
            encodeLongElement(descriptor, 1, value.creationMs.toEpochMilliseconds())
        }

    override fun deserialize(decoder: Decoder): Media {
        var uri = ""
        var creationMs: Instant = Instant.fromEpochMilliseconds(0)
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> uri =
                        decodeStringElement(descriptor, 0)

                    1 -> creationMs = Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return Media(uri, creationMs)
    }
}

object ReactionSerializer : KSerializer<Reaction> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Reaction") {
        element<String>("reaction")
        element<String>("actor")
    }

    override fun serialize(encoder: Encoder, value: Reaction) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.reaction)
            encodeStringElement(descriptor, 1, value.actor)
        }

    override fun deserialize(decoder: Decoder): Reaction {
        var reaction = ""
        var actor = ""
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> reaction =
                        decodeStringElement(descriptor, 0).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

                    1 -> actor =
                        decodeStringElement(descriptor, 1).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return Reaction(reaction, actor)
    }
}
