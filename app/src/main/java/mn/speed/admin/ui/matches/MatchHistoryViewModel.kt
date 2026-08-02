package mn.speed.admin.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import javax.inject.Inject

data class MatchItem(
    val id: String,
    val map: String,
    val score: String,
    val result: String, // WIN, LOSS, DRAW
    val kills: Int,
    val deaths: Int,
    val date: String,
    val mvp: String? = null
)

@HiltViewModel
class MatchHistoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) : ViewModel() {

    private val _matches = MutableStateFlow<List<MatchItem>>(emptyList())
    val matches: StateFlow<List<MatchItem>> = _matches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches() {
        val myName = authManager.getUsername() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMatchHistory(myName)
                if (response.isSuccessful) {
                    _matches.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
