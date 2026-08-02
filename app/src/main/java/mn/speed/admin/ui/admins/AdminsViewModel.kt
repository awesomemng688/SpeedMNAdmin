package mn.speed.admin.ui.admins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import javax.inject.Inject

@HiltViewModel
class AdminsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _admins = MutableStateFlow<List<AdminModel>>(emptyList())
    val admins: StateFlow<List<AdminModel>> = _admins.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAdmins()
    }

    fun loadAdmins() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getAdmins()
                if (response.isSuccessful) {
                    val rawList = response.body() ?: emptyList()
                    _admins.value = rawList.map { map ->
                        AdminModel(
                            id = map["id"] ?: "",
                            username = map["username"] ?: "Unknown",
                            steamIdOrIp = map["steamid"] ?: map["ip"] ?: "",
                            flags = map["flags"] ?: "",
                            serverName = map["server_name"] ?: "Global"
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

    fun addAdmin(username: String, steamid: String, flags: String, server: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.performAdminAction(mapOf(
                    "action" to "add",
                    "username" to username,
                    "steamid" to steamid,
                    "flags" to flags,
                    "server_name" to server
                ))
                if (response.isSuccessful) {
                    loadAdmins()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAdmin(admin: AdminModel) {
        viewModelScope.launch {
            try {
                val response = apiService.performAdminAction(mapOf(
                    "action" to "delete",
                    "admin_id" to admin.id
                ))
                if (response.isSuccessful) {
                    loadAdmins() // Жагсаалтыг шинэчлэх
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}