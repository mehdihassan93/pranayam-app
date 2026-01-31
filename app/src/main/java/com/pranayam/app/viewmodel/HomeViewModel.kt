package com.pranayam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.data.model.Profile
import com.pranayam.app.di.UserSessionManager
import com.pranayam.app.repository.PranayamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _matchEvent = kotlinx.coroutines.flow.MutableSharedFlow<com.pranayam.app.data.model.LikeResponse>()
    val matchEvent = _matchEvent.asSharedFlow()

    private val _currentUserProfile = MutableStateFlow<Profile?>(null)
    val currentUserProfile: StateFlow<Profile?> = _currentUserProfile.asStateFlow()

    private val userId: String
        get() = sessionManager.getUserId() ?: ""

    init {
        loadProfiles()
        // Current user profile will be fetched from backend when needed
        _currentUserProfile.value = Profile(
            id = userId,
            name = "",
            age = 0,
            photos = emptyList(),
            profession = "",
            distance = 0,
            isVerified = false,
            hasVideo = false,
            prompts = emptyList()
        )
    }

    fun loadProfiles(lat: Double? = null, long: Double? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDiscoveryProfiles(userId, lat, long).collect { result ->
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

    fun like() {
        val currentProfile = _profiles.value.getOrNull(_currentIndex.value) ?: return
        viewModelScope.launch {
            repository.swipeProfile(userId, currentProfile.id, "LIKE").onSuccess { response ->
                if (response.isMatch) {
                    _matchEvent.emit(response)
                }
            }
            // Move to next card anyway for a smooth UX
            _currentIndex.value++
        }
    }

    fun pass() {
        val currentProfile = _profiles.value.getOrNull(_currentIndex.value) ?: return
        viewModelScope.launch {
            repository.swipeProfile(userId, currentProfile.id, "PASS")
            _currentIndex.value++
        }
    }

    fun undo() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
        }
    }
}
