package com.pranayam.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val matchNotifications: StateFlow<Boolean> = repository.matchNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val messageNotifications: StateFlow<Boolean> = repository.messageNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val showOnlineStatus: StateFlow<Boolean> = repository.showOnlineStatus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val showDistance: StateFlow<Boolean> = repository.showDistance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val darkMode: StateFlow<Boolean> = repository.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setNotificationsEnabled(value: Boolean) { viewModelScope.launch { repository.setNotificationsEnabled(value) } }
    fun setMatchNotifications(value: Boolean) { viewModelScope.launch { repository.setMatchNotifications(value) } }
    fun setMessageNotifications(value: Boolean) { viewModelScope.launch { repository.setMessageNotifications(value) } }
    fun setShowOnlineStatus(value: Boolean) { viewModelScope.launch { repository.setShowOnlineStatus(value) } }
    fun setShowDistance(value: Boolean) { viewModelScope.launch { repository.setShowDistance(value) } }
    fun setDarkMode(value: Boolean) { viewModelScope.launch { repository.setDarkMode(value) } }
}
