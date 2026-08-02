package mn.speed.admin.ui.clan

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import mn.speed.admin.data.model.ClanItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class ClanViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager,
    application: Application
) : AndroidViewModel(application) {

    private val _clans = MutableStateFlow<List<ClanItem>>(emptyList())
    val clans: StateFlow<List<ClanItem>> = _clans.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchClans()
    }

    private val _toastMessage = kotlinx.coroutines.flow.MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    val isGuest: Boolean get() = authManager.isGuest()
    val isAdmin: Boolean get() = authManager.isRootAdmin()

    fun createClan(name: String, tag: String, logoUrl: String) {
        val username = authManager.getUsername() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = mapOf(
                    "action" to "create",
                    "username" to username,
                    "name" to name,
                    "tag" to tag,
                    "logo_url" to logoUrl // logoUrl-ыг logo_url болгож засав (PHP backend-д нийцүүлэн)
                )
                val response = apiService.performClanAction(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.get("success") == true || body?.get("success").toString() == "true") {
                        _toastMessage.emit("Клан амжилттай үүсгэгдлээ!")
                        fetchClans()
                    } else {
                        val msg = body?.get("message")?.toString() ?: "Сервер татгалзлаа"
                        _toastMessage.emit(msg)
                    }
                } else {
                    _toastMessage.emit("Серверийн алдаа (500/400)")
                }
            } catch (e: Exception) {
                _toastMessage.emit("Сүлжээний алдаа: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun joinClan(clanId: String) {
        val username = authManager.getUsername() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = mapOf(
                    "action" to "join",
                    "username" to username,
                    "clan_id" to clanId // clanId-ыг clan_id болгож засав
                )
                val response = apiService.performClanAction(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.get("success") == true || body?.get("success").toString() == "true") {
                        _toastMessage.emit("Кланд нэгдлээ!")
                        fetchClans()
                    } else {
                        val msg = body?.get("message")?.toString() ?: "Сервер татгалзлаа"
                        _toastMessage.emit(msg)
                    }
                } else {
                    _toastMessage.emit("Серверийн алдаа (500/400)")
                }
            } catch (e: Exception) {
                _toastMessage.emit("Сүлжээний алдаа")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteClan(clanId: String) {
        val username = authManager.getUsername() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Сервер ямар түлхүүр үг хүлээж авдагийг тодорхой мэдэхгүй тул бүх хувилбарыг илгээв
                val request = mapOf(
                    "action" to "delete",
                    "username" to username,
                    "clan_id" to clanId,
                    "clanId" to clanId,
                    "id" to clanId
                )
                android.util.Log.d("ClanDebug", "Deleting clan: $request")
                
                val response = apiService.performClanAction(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    android.util.Log.d("ClanDebug", "Server response: $body")
                    val isSuccess = body?.get("success") == true || 
                                    body?.get("success")?.toString() == "true" ||
                                    body?.get("status")?.toString() == "success"

                    if (isSuccess) {
                        _toastMessage.emit("Клан амжилттай устгагдлаа!")
                        fetchClans()
                    } else {
                        val msg = body?.get("message")?.toString() ?: "Сервер татгалзлаа"
                        _toastMessage.emit(msg)
                    }
                } else {
                    // Сервер 500 алдаа өгвөл ирж буй текст хариуг унших
                    val errorText = response.errorBody()?.string() ?: ""
                    android.util.Log.e("ClanDebug", "Server Error 500 Body: $errorText")
                    _toastMessage.emit("Серверийн алдаа: ${response.code()}")
                }
            } catch (e: Exception) {
                _toastMessage.emit("Сүлжээний алдаа: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchClans() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _clans.value = apiService.getClans()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadLogoAndCreateClan(name: String, tag: String, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var finalLogoUrl = ""
                
                if (imageUri != null) {
                    val contentResolver = getApplication<Application>().contentResolver
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("image", "logo_${System.currentTimeMillis()}.jpg", requestFile)
                        
                        val uploadResponse = apiService.uploadImage(body)
                        if (uploadResponse.isSuccessful && uploadResponse.body()?.get("success") == true) {
                            finalLogoUrl = uploadResponse.body()?.get("url")?.toString() ?: ""
                        } else {
                            _toastMessage.emit("Зураг байршуулахад алдаа гарлаа")
                            _isLoading.value = false
                            return@launch
                        }
                    }
                }

                createClan(name, tag, finalLogoUrl)
            } catch (e: Exception) {
                _toastMessage.emit("Алдаа: ${e.localizedMessage}")
                _isLoading.value = false
            }
        }
    }
}
