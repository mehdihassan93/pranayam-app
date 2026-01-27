package com.pranayam.app.api

import com.pranayam.app.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketService @Inject constructor() {
    private var socket: Socket? = null

    private val _messageFlow = MutableSharedFlow<JSONObject>()
    val messageFlow: SharedFlow<JSONObject> = _messageFlow

    private val _statusFlow = MutableSharedFlow<JSONObject>()
    val statusFlow: SharedFlow<JSONObject> = _statusFlow

    private val _typingFlow = MutableSharedFlow<JSONObject>()
    val typingFlow: SharedFlow<JSONObject> = _typingFlow

    fun connect(userId: String) {
        try {
            val opts = IO.Options()
            opts.query = "userId=$userId"
            socket = IO.socket(BuildConfig.SOCKET_URL, opts)
            
            socket?.on(Socket.EVENT_CONNECT) {
                println("Socket Connected")
            }
            
            socket?.on("new_message") { args ->
                val data = args[0] as JSONObject
                _messageFlow.tryEmit(data)
            }
            
            socket?.on("user_status") { args ->
                val data = args[0] as JSONObject
                _statusFlow.tryEmit(data)
            }
            
            socket?.on("user_typing") { args ->
                val data = args[0] as JSONObject
                _typingFlow.tryEmit(data)
            }
            
            socket?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(data: JSONObject) {
        socket?.emit("send_message", data)
    }

    fun sendTyping(conversationId: String, userId: String, isTyping: Boolean) {
        val data = JSONObject().apply {
            put("conversationId", conversationId)
            put("userId", userId)
            put("isTyping", isTyping)
        }
        socket?.emit("typing", data)
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}
