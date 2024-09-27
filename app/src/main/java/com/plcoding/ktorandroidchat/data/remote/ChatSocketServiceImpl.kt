package com.plcoding.ktorandroidchat.data.remote

import com.plcoding.ktorandroidchat.data.remote.dto.MessageDto
import com.plcoding.ktorandroidchat.domain.model.Message
import com.plcoding.ktorandroidchat.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
) : ChatSocketService {
    private var socket: WebSocketSession? = null

    // Init connection to service
    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocketRoute.url}?username=$username")
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else
            {
                Resource.Error("Could not establish a connection.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> { // Wenn es eine neue Nachricht gibt
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text } //Checken, dass nur Text kommt
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: "" //Text in val abspeichern
                    val messageDto = Json.decodeFromString<MessageDto>(json) //JSON (vom Server) in das DTO umwandeln
                    messageDto.toMessage() //DTO zum "richtigen" Objekt
                } ?: flow {  }
        } catch (e:Exception)
        {
            e.printStackTrace()
            flow {  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}