package com.pranayam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.data.model.Message
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
    private val socketService: SocketService
) : ViewModel() {

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

    private var recordingJob: Job? = null

    init {
        // Automatically start listening for real-time events upon entering the chat
        observeSocketEvents()
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
        
        // Ensure the socket is connected using the current user's session
        // Note: In production, "me" would be the actual authenticated UID
        socketService.connect("me") 
    }

    /**
     * Called by the UI when the user type in the text field.
     * Triggers the remote "Typing..." indicator.
     */
    fun onMessageChange(text: String, conversationId: String) {
        _messageText.value = text
        // Throttle/Send typing indicator to partner via Socket
        socketService.sendTyping(conversationId, "me", text.isNotEmpty())
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
    fun sendMessage(conversationId: String) {
        val text = _messageText.value.trim()
        if (text.isEmpty()) return

        // Construct the JSON payload for the industry-standard socket protocol
        val data = JSONObject().apply {
            put("conversationId", conversationId)
            put("senderId", "me")
            put("content", text)
            put("recipientId", "other_user_id") // Placeholder for the actual target UID
        }
        
        socketService.sendMessage(data)
        
        // Reset local state
        _messageText.value = ""
        socketService.sendTyping(conversationId, "me", false)
    }

    /**
     * Lifecycle Guard: Closes socket connection when the user leaves the chat screen.
     */
    override fun onCleared() {
        super.onCleared()
        socketService.disconnect()
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
}
