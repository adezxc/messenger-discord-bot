package org.example

import kotlinx.serialization.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*
import kotlin.text.Charsets

@Serializable(with = MessageSerializer::class)
data class Message(
    val senderName: String,
    val timestampMs: kotlinx.datetime.Instant,
    val content: String,
    val isGeoBlockedForViewer: Boolean
) {
    init {
        require(senderName.isNotEmpty()) { "name cannot be empty" }
        require(content.isNotEmpty()) { "content cannot be empty" }
    }
}

object MessageSerializer : KSerializer<Message> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Message") {
        element<String>("sender_name")
        element<Long>("timestamp_ms")
        element<String>("content")
        element<Boolean>("is_geoblocked_for_viewer")
    }

    override fun serialize(encoder: Encoder, value: Message) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.senderName)
            encodeLongElement(descriptor, 1, value.timestampMs.toEpochMilliseconds())
            encodeStringElement(descriptor, 2, value.content)
            encodeBooleanElement(descriptor, 3, value.isGeoBlockedForViewer)
        }

    override fun deserialize(decoder: Decoder): Message {
        var senderName = ""
        var timestampMs: kotlinx.datetime.Instant = kotlinx.datetime.Instant.fromEpochMilliseconds(0)
        var content = ""
        var isGeoblockedForViewer = false
        @OptIn(ExperimentalSerializationApi::class)
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                senderName = decodeStringElement(descriptor, 0).byteInputStream(Charsets.UTF_8).toString()
                timestampMs = kotlinx.datetime.Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
                content = decodeStringElement(descriptor, 2)
                isGeoblockedForViewer = decodeBooleanElement(descriptor, 3)
            } else while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> senderName =
                        decodeStringElement(descriptor, 0).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
                    1 -> timestampMs = kotlinx.datetime.Instant.fromEpochMilliseconds(decodeLongElement(descriptor, 1))
                    2 -> content =
                        decodeStringElement(descriptor, 2).toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)

                    3 -> isGeoblockedForViewer = decodeBooleanElement(descriptor, 3)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }
        return Message(senderName, timestampMs, content, isGeoblockedForViewer)
    }
}
