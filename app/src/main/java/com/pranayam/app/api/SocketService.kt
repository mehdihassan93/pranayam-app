package com.pranayam.app.api

import android.util.Log
import com.pranayam.app.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketService @Inject constructor() {
    private var socket: Socket? = null
    private var currentUserId: String? = null

    private val _messageFlow = MutableSharedFlow<JSONObject>()
    val messageFlow: SharedFlow<JSONObject> = _messageFlow

    private val _statusFlow = MutableSharedFlow<JSONObject>()
    val statusFlow: SharedFlow<JSONObject> = _statusFlow

    private val _typingFlow = MutableSharedFlow<JSONObject>()
    val typingFlow: SharedFlow<JSONObject> = _typingFlow

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    enum class ConnectionState {
        DISCONNECTED, CONNECTING, CONNECTED, ERROR
    }

    fun connect(userId: String) {
        // Prevent duplicate connections
        if (socket?.connected() == true && currentUserId == userId) {
            return
        }

        // Disconnect existing socket if connecting with different user
        if (currentUserId != null && currentUserId != userId) {
            disconnect()
        }

        currentUserId = userId
        _connectionState.value = ConnectionState.CONNECTING

        try {
            val opts = IO.Options().apply {
                query = "userId=$userId"
                reconnection = true
                reconnectionAttempts = 5
                reconnectionDelay = 1000
            }
            socket = IO.socket(BuildConfig.SOCKET_URL, opts)

            socket?.on(Socket.EVENT_CONNECT) {
                _connectionState.value = ConnectionState.CONNECTED
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Socket connected for user: $userId")
                }
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                _connectionState.value = ConnectionState.DISCONNECTED
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Socket disconnected")
                }
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                _connectionState.value = ConnectionState.ERROR
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Socket connection error: ${args.firstOrNull()}")
                }
            }

            socket?.on("new_message") { args ->
                parseAndEmit(args) { data -> _messageFlow.tryEmit(data) }
            }

            socket?.on("user_status") { args ->
                parseAndEmit(args) { data -> _statusFlow.tryEmit(data) }
            }

            socket?.on("user_typing") { args ->
                parseAndEmit(args) { data -> _typingFlow.tryEmit(data) }
            }

            socket?.connect()
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.ERROR
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Socket connection failed", e)
            }
        }
    }

    private inline fun parseAndEmit(args: Array<Any>, emit: (JSONObject) -> Unit) {
        try {
            val data = args.firstOrNull()
            when (data) {
                is JSONObject -> emit(data)
                is String -> emit(JSONObject(data))
                else -> {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Unexpected socket data type: ${data?.javaClass?.simpleName}")
                    }
                }
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to parse socket message", e)
            }
        }
    }

    fun sendMessage(data: JSONObject) {
        if (socket?.connected() == true) {
            socket?.emit("send_message", data)
        }
    }

    fun sendTyping(conversationId: String, userId: String, isTyping: Boolean) {
        if (socket?.connected() != true) return

        try {
            val data = JSONObject().apply {
                put("conversationId", conversationId)
                put("userId", userId)
                put("isTyping", isTyping)
            }
            socket?.emit("typing", data)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to send typing indicator", e)
            }
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
        currentUserId = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    fun isConnected(): Boolean = socket?.connected() == true

    companion object {
        private const val TAG = "SocketService"
    }
}
