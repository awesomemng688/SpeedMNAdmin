package mn.speed.admin.ui.clan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import javax.inject.Inject

data class ChatMessage(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: String,
    val isMine: Boolean
)

@HiltViewModel
class ClanChatViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var isPolling = false

    fun startPolling(clanId: String) {
        isPolling = true
        viewModelScope.launch {
            while (isPolling) {
                fetchMessages(clanId)
                delay(3000) // 3 секунд тутамд шинэ мессеж шалгах
            }
        }
    }

    fun stopPolling() {
        isPolling = false
    }

    private suspend fun fetchMessages(clanId: String) {
        try {
            val response = apiService.getClanMessages(clanId)
            if (response.isSuccessful) {
                val myUsername = authManager.getUsername()
                val newMessages = response.body()?.map { map ->
                    ChatMessage(
                        id = map["id"]?.toString() ?: "",
                        sender = map["username"]?.toString() ?: "Unknown",
                        message = map["message"]?.toString() ?: "",
                        timestamp = map["created_at"]?.toString() ?: "",
                        isMine = map["username"] == myUsername
                    )
                } ?: emptyList()
                _messages.value = newMessages
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(clanId: String, message: String) {
        if (message.isBlank()) return
        val username = authManager.getUsername() ?: return

        viewModelScope.launch {
            try {
                val request = mapOf(
                    "action" to "send",
                    "clan_id" to clanId,
                    "username" to username,
                    "message" to message
                )
                val response = apiService.sendClanMessage(request)
                if (response.isSuccessful) {
                    fetchMessages(clanId) // Амжилттай болбол шууд шинэчилнэ
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
