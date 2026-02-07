package com.pranayam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.data.model.Conversation
import com.pranayam.app.data.model.Message
import com.pranayam.app.data.model.ContentType
import com.pranayam.app.data.model.MessageStatus
import com.pranayam.app.di.UserSessionManager
import com.pranayam.app.repository.PranayamRepository
import com.pranayam.app.util.VoiceRecorder
import com.pranayam.app.api.SocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

/**
 * ChatViewModel handles the UI logic for a single conversation.
 * It coordinates real-time data from SocketService, media recording,
 * and historical data from the Repository.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: PranayamRepository,
    private val voiceRecorder: VoiceRecorder,
    private val socketService: SocketService,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val userId: String
        get() = sessionManager.getUserId() ?: ""

    // --- State Observables ---

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0)
    val recordingDuration: StateFlow<Int> = _recordingDuration.asStateFlow()

    // Real-time status of the person the user is chatting with
    private val _remoteUserStatus = MutableStateFlow<String>("Offline")
    val remoteUserStatus: StateFlow<String> = _remoteUserStatus.asStateFlow()

    // "Typing..." feedback for the local user
    private val _remoteUserTyping = MutableStateFlow(false)
    val remoteUserTyping: StateFlow<Boolean> = _remoteUserTyping.asStateFlow()

    // Conversations list for ChatListScreen
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private var recordingJob: Job? = null
    private var _currentConversationId: String? = null

    init {
        // Automatically start listening for real-time events upon entering the chat
        observeSocketEvents()
        loadConversations()
    }

    /**
     * Subscribes to the SocketService flows to update the UI
     * as events happen on the server.
     */
    private fun observeSocketEvents() {
        // Listen for Partner's Online/Offline status changes
        viewModelScope.launch {
            socketService.statusFlow.collect { data ->
                val isOnline = data.optBoolean("isOnline", false)
                _remoteUserStatus.value = if (isOnline) "Online" else "Offline"
            }
        }

        // Listen for "Partner IS TYPING" events
        viewModelScope.launch {
            socketService.typingFlow.collect { data ->
                _remoteUserTyping.value = data.optBoolean("isTyping", false)
            }
        }

        // Listen for incoming messages and persist them locally
        viewModelScope.launch {
            socketService.messageFlow.collect { data ->
                val conversationId = data.optString("conversationId", "")
                val content = data.optString("content", "")
                val senderId = data.optString("senderId", "")
                val messageId = data.optString("id", data.optString("_id", java.util.UUID.randomUUID().toString()))
                val timestamp = data.optString("timestamp", "Just now")

                if (conversationId.isNotEmpty() && content.isNotEmpty()) {
                    val message = Message(
                        id = messageId,
                        text = content,
                        timestamp = timestamp,
                        isSent = senderId == userId,
                        contentType = ContentType.TEXT,
                        status = MessageStatus.DELIVERED
                    )
                    repository.persistMessage(conversationId, message)
                }
            }
        }

        // Connect socket with authenticated user ID
        if (userId.isNotEmpty()) {
            socketService.connect(userId)
        }
    }

    /**
     * Loads conversations from the API.
     */
    private fun loadConversations() {
        viewModelScope.launch {
            repository.getConversations().collect { result ->
                result.onSuccess { _conversations.value = it }
            }
        }
    }

    /**
     * Joins a conversation: hydrates local DB from API and tracks the active conversation.
     */
    fun joinConversation(conversationId: String) {
        _currentConversationId = conversationId
        viewModelScope.launch {
            repository.refreshMessages(conversationId)
        }
    }

    /**
     * Called by the UI when the user type in the text field.
     * Triggers the remote "Typing..." indicator.
     */
    fun onMessageChange(text: String, conversationId: String) {
        _messageText.value = text
        // Send typing indicator to partner via Socket
        if (userId.isNotEmpty()) {
            socketService.sendTyping(conversationId, userId, text.isNotEmpty())
        }
    }

    /**
     * Exposes the message stream for a specific conversation.
     * Uses stateIn to keep the flow active as long as the UI is visible.
     */
    fun getMessages(conversationId: String): StateFlow<List<Message>> {
        return repository.getMessagesForConversation(conversationId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    /**
     * Finalizes and sends the currently typed message.
     */
    fun sendMessage(conversationId: String, recipientId: String = "") {
        val text = _messageText.value.trim()
        if (text.isEmpty() || userId.isEmpty()) return

        // Persist locally for optimistic UI
        viewModelScope.launch {
            repository.sendMessage(conversationId, text)
        }

        // Also send via socket for real-time delivery
        val data = JSONObject().apply {
            put("conversationId", conversationId)
            put("senderId", userId)
            put("content", text)
            put("recipientId", recipientId)
        }
        socketService.sendMessage(data)

        // Reset local state
        _messageText.value = ""
        socketService.sendTyping(conversationId, userId, false)
    }

    /**
     * Lifecycle Guard: Do NOT disconnect the singleton SocketService here.
     */
    override fun onCleared() {
        super.onCleared()
        // SocketService is a Singleton — disconnecting here would kill it for all consumers
    }

    /**
     * Voice Recording: Initial trigger for microphone capture.
     */
    fun startRecording() {
        _isRecording.value = true
        _recordingDuration.value = 0
        voiceRecorder.startRecording()

        // Start the UI timer job
        recordingJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _recordingDuration.value++
            }
        }
    }

    /**
     * Voice Recording: Complete and send the audio file.
     */
    fun stopRecording(conversationId: String) {
        _isRecording.value = false
        recordingJob?.cancel()
        val audioFile = voiceRecorder.stopRecording()

        if (audioFile != null && _recordingDuration.value > 0) {
            viewModelScope.launch {
                // Future Implementation: repository.sendVoiceMessage(conversationId, audioFile, _recordingDuration.value)
            }
        }
    }

    /**
     * Voice Recording: Discard current capture without sending.
     */
    fun cancelRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
        voiceRecorder.cancelRecording()
    }

    /**
     * Reports a user for inappropriate behavior.
     */
    fun reportUser(reportedUserId: String, reason: String, description: String = "", onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.reportUser(reportedUserId, reason, description)
                .onSuccess { onResult(true) }
                .onFailure { onResult(false) }
        }
    }

    /**
     * Blocks a user and removes their conversation.
     */
    fun blockUser(blockedUserId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.blockUser(blockedUserId)
                .onSuccess { onResult(true) }
                .onFailure { onResult(false) }
        }
    }
}
