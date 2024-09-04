package org.example

import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*
import kotlin.text.Charsets

@Serializable(with = MessageSerializer::class)
data class Message(
    val senderName: String,
    val timestampMs: Instant,
    val photos: List<Photo>,
    val content: String,
    val reactions: List<Reaction>,
    val isGeoBlockedForViewer: Boolean
) {
    init {
        require(senderName.isNotEmpty()) { "name cannot be empty" }
        require(content.isNotEmpty()) { "content cannot be empty" }
    }
}

@Serializable(with = PhotoSerializer::class)
data class Photo(
    val uri: String,
    val creationMs: Instant,
) {
    init {
        require(uri.isNotEmpty()) { "name cannot be empty" }
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

object MessageSerializer : KSerializer<Message> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Message") {
        element<String>("sender_name")
        element<Long>("timestamp_ms")
        element<List<Photo>>("photos")
        element<String>("content")
        element<List<Reaction>>("reactions")
        element<Boolean>("is_geoblocked_for_viewer")
    }

    override fun serialize(encoder: Encoder, value: Message) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.senderName)
            encodeLongElement(descriptor, 1, value.timestampMs.toEpochMilliseconds())
            ListSerializer(PhotoSerializer).serialize(encoder, value.photos)
            encodeStringElement(descriptor, 3, value.content)
            ListSerializer(ReactionSerializer).serialize(encoder, value.reactions)
            encodeBooleanElement(descriptor, 5, value.isGeoBlockedForViewer)
        }

    override fun deserialize(decoder: Decoder): Message {
        var senderName = ""
        var timestampMs: Instant = Instant.fromEpochMilliseconds(0)
        var photosSerializer = ListSerializer(PhotoSerializer)
        var photos: List<Photo> = mutableListOf()
        var content = ""
        var reactionSerializer = ListSerializer(ReactionSerializer)
        var reactions: List<Reaction> = mutableListOf()
        var isGeoblockedForViewer = false
        @OptIn(ExperimentalSerializationApi::class)
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                senderName = decodeStringElement(descriptor, 0).byteInputStream(Charsets.UTF_8).toString()
                timestampMs = Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
                photos = photosSerializer.deserialize(decoder)
                content = decodeStringElement(descriptor, 3)
                reactions = reactionSerializer.deserialize(decoder)
                isGeoblockedForViewer = decodeBooleanElement(descriptor, 5)
            } else while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> senderName =
                        decodeStringElement(descriptor, 0).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

                    1 -> timestampMs = Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
                    2 -> photos = photosSerializer.deserialize(decoder)
                    3 -> content =
                        decodeStringElement(descriptor, 3).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

                    4 -> reactions = reactionSerializer.deserialize(decoder)
                    5 -> isGeoblockedForViewer = decodeBooleanElement(descriptor, 4)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return Message(senderName, timestampMs, photos, content, reactions, isGeoblockedForViewer)
    }
}


object PhotoSerializer : KSerializer<Photo> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Photo") {
        element<String>("uri")
        element<Long>("creation_ms")
    }

    override fun serialize(encoder: Encoder, value: Photo) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.uri)
            encodeLongElement(descriptor, 1, value.creationMs.toEpochMilliseconds())
        }

    override fun deserialize(decoder: Decoder): Photo {
        var uri = ""
        var creationMs: Instant = Instant.fromEpochMilliseconds(0)
        @OptIn(ExperimentalSerializationApi::class)
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                uri = decodeStringElement(descriptor, 0)
                creationMs = Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
            } else while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> uri =
                        decodeStringElement(descriptor, 0)

                    1 -> creationMs = Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return Photo(uri, creationMs)
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
        @OptIn(ExperimentalSerializationApi::class)
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                reaction = decodeStringElement(descriptor, 0).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
                actor = decodeStringElement(descriptor, 1).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
            } else while (true) {
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
