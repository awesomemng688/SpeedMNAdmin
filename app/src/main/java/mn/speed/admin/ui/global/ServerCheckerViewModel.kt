package mn.speed.admin.ui.global

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mn.speed.admin.data.local.AuthManager
import javax.inject.Inject

@HiltViewModel
class ServerCheckerViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _recentServers = MutableStateFlow<List<String>>(emptyList())
    val recentServers: StateFlow<List<String>> = _recentServers.asStateFlow()

    init {
        loadRecentServers()
    }

    fun loadRecentServers() {
        _recentServers.value = authManager.getRecentServers()
    }

    fun addRecentServer(ip: String, port: Int) {
        authManager.addRecentServer("$ip:$port")
        loadRecentServers()
    }
}
