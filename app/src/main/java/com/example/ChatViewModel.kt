package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "ADVISOR"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Pre-populate with welcome message
        _messages.value = listOf(
            ChatMessage(
                sender = "ADVISOR",
                text = """
                    [VANGUARD STRATEGIST COGNITIVE GRID ACTIVE]
                    System Status: ONLINE
                    Tactical Target: WarEra.io Universe
                    
                    Greetings, Commander. I am your specialized combat and market strategist.
                    Ask me for optimal troop setups, resource management schemes, trade arbitrage opportunities, or custom battlefield plans. 
                    
                    How shall we deploy today?
                """.trimIndent()
            )
        )
    }

    fun sendMessage(text: String, apiToken: String, playerName: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(sender = "USER", text = text)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val replyText = GeminiService.generateAdvice(text, apiToken, playerName)
                _messages.value = _messages.value + ChatMessage(sender = "ADVISOR", text = replyText)
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    sender = "ADVISOR", 
                    text = "COMMAND PROTOCOL ERR: AI advisor communication lost. Ensure link is stable."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        _messages.value = listOf(
            ChatMessage(
                sender = "ADVISOR",
                text = "[VANGUARD CORE RESET] Chat memory wiped. Ready for new input commands."
            )
        )
    }
}
