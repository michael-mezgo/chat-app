package com.plcoding.ktorandroidchat.data.remote.dto

import com.plcoding.ktorandroidchat.domain.model.Message
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val text: String,
    val timestamp: Long,
    val username: String,
    val id: String
) {
    fun toMessage(): Message {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val date = instant.toLocalDateTime(TimeZone.UTC).date
        val formattedDate = "${date.year}-${date.month}-${date.dayOfMonth}"

        return Message(
            text = text,
            formattedTime = formattedDate,
            username = username
        )
    }
}
