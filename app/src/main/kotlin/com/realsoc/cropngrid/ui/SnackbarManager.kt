package com.realsoc.cropngrid.ui

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class Message(val id: Long, @StringRes val textId: Int, val action: Action? = null)

data class Action(@StringRes val textId: Int, val onClick: () -> Unit)

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
object SnackbarManager {

    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    fun showState(@StringRes messageTextId: Int, overrideSameTextId: Boolean = false, action: Action? = null) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { overrideSameTextId && it.textId == messageTextId } + Message(
                id = UUID.randomUUID().mostSignificantBits,
                textId = messageTextId,
                action = action
            )
        }
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}
