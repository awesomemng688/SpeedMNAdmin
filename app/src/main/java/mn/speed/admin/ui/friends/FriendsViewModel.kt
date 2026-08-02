package mn.speed.admin.ui.friends

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

data class FriendModel(
    val username: String,
    val isOnline: Boolean = false,
    val lastServer: String? = null,
    val avatarUrl: String? = null
)

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) : ViewModel() {

    private val _friends = MutableStateFlow<List<FriendModel>>(emptyList())
    val friends: StateFlow<List<FriendModel>> = _friends.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        val myName = authManager.getUsername()
        if (myName == null) {
            _friends.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getFriends(myName)
                if (response.isSuccessful) {
                    val rawList = response.body() ?: emptyList()
                    _friends.value = rawList.map { map ->
                        FriendModel(
                            username = map["friend_name"]?.toString() ?: "",
                            isOnline = (map["is_online"] as? Double)?.toInt() == 1,
                            lastServer = map["server_name"]?.toString(),
                            avatarUrl = map["avatar_url"]?.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun followPlayer(friendName: String) {
        val myName = authManager.getUsername() ?: return
        viewModelScope.launch {
            try {
                apiService.performFriendAction(mapOf(
                    "action" to "follow",
                    "username" to myName,
                    "friend_name" to friendName
                ))
                loadFriends()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unfollowPlayer(friendName: String) {
        val myName = authManager.getUsername() ?: return
        viewModelScope.launch {
            try {
                apiService.performFriendAction(mapOf(
                    "action" to "unfollow",
                    "username" to myName,
                    "friend_name" to friendName
                ))
                loadFriends()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
