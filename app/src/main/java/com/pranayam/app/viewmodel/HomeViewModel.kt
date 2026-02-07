package com.pranayam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.data.model.Profile
import com.pranayam.app.di.UserSessionManager
import com.pranayam.app.repository.PranayamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PranayamRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _matchEvent = MutableSharedFlow<com.pranayam.app.data.model.LikeResponse>()
    val matchEvent = _matchEvent.asSharedFlow()

    private val _currentUserProfile = MutableStateFlow<Profile?>(null)
    val currentUserProfile: StateFlow<Profile?> = _currentUserProfile.asStateFlow()

    // Guest mode state
    private val _isGuestMode = MutableStateFlow(sessionManager.isGuest())
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    private val _guestSwipeCount = MutableStateFlow(0)
    val guestSwipeCount: StateFlow<Int> = _guestSwipeCount.asStateFlow()

    private val _showLoginPrompt = MutableSharedFlow<Unit>()
    val showLoginPrompt = _showLoginPrompt.asSharedFlow()

    private val userId: String
        get() = sessionManager.getUserId() ?: ""

    companion object {
        private const val GUEST_SWIPE_LIMIT = 3
    }

    init {
        loadProfiles()
        // Current user profile will be fetched from backend when needed
        _currentUserProfile.value = Profile(
            id = userId,
            name = "",
            age = 0,
            photos = emptyList(),
            profession = "",
            distance = 0
        )
    }

    fun loadProfiles(lat: Double? = null, long: Double? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _isGuestMode.value = sessionManager.isGuest()

            repository.getDiscoveryProfiles(lat, long, _isGuestMode.value).collect { result ->
                result.onSuccess {
                    _profiles.value = it
                    _error.value = null
                }.onFailure {
                    _error.value = it.message
                }
                _isLoading.value = false
            }
        }
    }

    private fun handleGuestSwipe() {
        _guestSwipeCount.value++
        _currentIndex.value++

        // Check if guest has reached swipe limit
        if (_guestSwipeCount.value >= GUEST_SWIPE_LIMIT) {
            viewModelScope.launch {
                _showLoginPrompt.emit(Unit)
            }
        }
    }

    fun like() {
        val currentProfile = _profiles.value.getOrNull(_currentIndex.value) ?: return

        // Guests can't actually save swipes - just show UI and track count
        if (_isGuestMode.value) {
            handleGuestSwipe()
            return
        }

        viewModelScope.launch {
            repository.swipeProfile(currentProfile.id, "LIKE").onSuccess { response ->
                if (response.isMatch) {
                    _matchEvent.emit(response)
                }
            }
            // Move to next card anyway for a smooth UX
            _currentIndex.value++
        }
    }

    fun superLike() {
        val currentProfile = _profiles.value.getOrNull(_currentIndex.value) ?: return

        if (_isGuestMode.value) {
            handleGuestSwipe()
            return
        }

        viewModelScope.launch {
            repository.swipeProfile(currentProfile.id, "SUPERLIKE").onSuccess { response ->
                if (response.isMatch) {
                    _matchEvent.emit(response)
                }
            }
            _currentIndex.value++
        }
    }

    fun pass() {
        val currentProfile = _profiles.value.getOrNull(_currentIndex.value) ?: return

        // Guests can't actually save swipes - just show UI and track count
        if (_isGuestMode.value) {
            handleGuestSwipe()
            return
        }

        viewModelScope.launch {
            repository.swipeProfile(currentProfile.id, "PASS")
            _currentIndex.value++
        }
    }

    fun undo() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
            // Decrement guest swipe count if in guest mode
            if (_isGuestMode.value && _guestSwipeCount.value > 0) {
                _guestSwipeCount.value--
            }
        }
    }

    fun resetGuestSwipeCount() {
        _guestSwipeCount.value = 0
    }

    fun refreshAuthState() {
        _isGuestMode.value = sessionManager.isGuest()
        if (!_isGuestMode.value) {
            loadProfiles()
        }
    }
}
