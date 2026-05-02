package com.amolieres.setlistync.core.domain.song.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Serialized as a plain JSON string (e.g. "uuid-value") rather than {"value":"uuid-value"}. */
@Serializable(with = SongId.Serializer::class)
data class SongId(val value: String) {
    object Serializer : KSerializer<SongId> {
        override val descriptor = PrimitiveSerialDescriptor("SongId", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: SongId) = encoder.encodeString(value.value)
        override fun deserialize(decoder: Decoder) = SongId(decoder.decodeString())
    }
}
