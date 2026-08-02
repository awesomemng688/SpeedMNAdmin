package mn.speed.admin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import mn.speed.admin.data.model.NewsItem
import javax.inject.Inject

import mn.speed.admin.ui.players.PlayerStats

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val apiService: ApiService
) : ViewModel() {

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news: StateFlow<List<NewsItem>> = _news.asStateFlow()

    private val _isNewsLoading = MutableStateFlow(false)
    val isNewsLoading: StateFlow<Boolean> = _isNewsLoading.asStateFlow()

    private val _personalStats = MutableStateFlow<PlayerStats?>(null)
    val personalStats: StateFlow<PlayerStats?> = _personalStats.asStateFlow()

    val userRole: String
        get() = authManager.getRole()

    val username: String
        get() = authManager.getUsername() ?: "User"

    init {
        if (authManager.isLoggedIn()) {
            fetchPersonalStats()
        }
    }

    fun fetchPersonalStats() {
        val name = authManager.getUsername() ?: return
        viewModelScope.launch {
            try {
                val response = apiService.getPlayerProfile(name, "pub1") // "pub1" default дамжуулав
                if (response.isSuccessful) {
                    val body = response.body()
                    _personalStats.value = PlayerStats(
                        kills = (body?.get("kills") as? Double)?.toInt() ?: 1240,
                        deaths = (body?.get("deaths") as? Double)?.toInt() ?: 850,
                        rankPoints = (body?.get("rank_points") as? Double)?.toInt() ?: 4500,
                        winRate = (body?.get("win_rate") as? Double)?.toInt() ?: 62,
                        accuracy = (body?.get("accuracy") as? Double)?.toInt() ?: 24,
                        skill = body?.get("skill")?.toString() ?: "Master Guardian"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchNews() {
        viewModelScope.launch {
            _isNewsLoading.value = true
            try {
                val response = apiService.getNews()
                _news.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isNewsLoading.value = false
            }
        }
    }

    fun logout() {
        authManager.clearSession()
    }
}
