package com.tashrif.jarvis.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tashrif.jarvis.data.ChatDatabase
import com.tashrif.jarvis.data.ChatMessage
import com.tashrif.jarvis.network.KahuguEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ChatDatabase.getInstance(application).chatDao()
    private val engine = KahuguEngine()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllMessages().collectLatest { list ->
                _messages.value = list
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            dao.insert(ChatMessage(role = "user", content = text))
            _isThinking.value = true
            _errorMessage.value = null
            val recent = dao.getRecentMessages(20).reversed()
            val history = recent.map { it.role to it.content }
            val result = engine.ask(text, history)
            result.fold(
                onSuccess = { reply ->
                    dao.insert(ChatMessage(role = "assistant", content = reply))
                },
                onFailure = { error ->
                    _errorMessage.value = "Jarvis hajafanikiwa: ${error.message}"
                }
            )
            _isThinking.value = false
        }
    }

    fun clearConversation() {
        viewModelScope.launch { dao.clearAll() }
    }
}
