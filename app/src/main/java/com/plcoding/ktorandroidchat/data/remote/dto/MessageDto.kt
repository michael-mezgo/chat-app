package com.plcoding.ktorandroidchat.data.remote.dto

import com.plcoding.ktorandroidchat.domain.model.Message
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class MessageDto(
    val text: String,
    val timestamp: Long,
    val username: String,
    val id: String
) {
    fun toMessage(): Message {
        val date = Date(timestamp)
        val formattedDate = "${date.year}-${date.month}"

        return Message(
            text = text,
            formattedTime = formattedDate,
            username = username
        )
    }
}
