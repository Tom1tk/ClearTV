package com.cleartv.ui.screensaver

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cleartv.data.PreferencesRepo
import com.cleartv.data.model.ScreensaverType
import com.cleartv.data.model.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing the screensaver idle timer.
 * Counts down from the configured timeout; any key event resets it.
 */
class ScreensaverViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepo = PreferencesRepo(application)

    val preferences: StateFlow<UserPreferences> = preferencesRepo.preferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private var timerJob: Job? = null

    init {
        startIdleTimer()
    }

    /**
     * Reset the idle timer — called on any key event from MainActivity.
     */
    fun resetIdleTimer() {
        _isActive.value = false
        startIdleTimer()
    }

    /**
     * Dismiss the screensaver (called when a key is pressed while active).
     */
    fun dismiss() {
        _isActive.value = false
        startIdleTimer()
    }

    private fun startIdleTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val prefs = preferences.value
            if (!prefs.screensaverEnabled) return@launch

            val timeoutMs = prefs.screensaverTimeoutMin * 60 * 1000L
            delay(timeoutMs)
            _isActive.value = true
        }
    }
}
