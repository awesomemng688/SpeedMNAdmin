package mn.speed.admin.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import mn.speed.admin.data.model.AuthRequest
import mn.speed.admin.data.model.AuthResponse
import javax.inject.Inject

class SpeedRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) : SpeedRepository {

    suspend fun loginWithFullResponse(user: String, pass: String): AuthResponse? = withContext(Dispatchers.IO) {
        Log.d("SpeedAuth", "Attempting login for: $user")
        return@withContext try {
            val request = AuthRequest(username = user, password = pass)
            val response = apiService.loginUser(request)
            
            if (response.isSuccessful) {
                val authResponse = response.body()
                Log.d("SpeedAuth", "Login response: $authResponse")
                if (authResponse?.success == true) {
                    authManager.saveSession(
                        username = user,
                        token = authResponse.token,
                        role = authResponse.role,
                        managedServers = authResponse.managedServers
                    )
                }
                authResponse
            } else {
                // errorBody() унших нь заримдаа удаан байдаг тул IO thread дээр хийх нь зөв
                val errorStr = response.errorBody()?.string() ?: "Unknown error"
                Log.e("SpeedAuth", "Login failed: $errorStr")
                AuthResponse(false, "Сервер алдаа (${response.code()})", null)
            }
        } catch (e: Exception) {
            Log.e("SpeedAuth", "Network error", e)
            AuthResponse(false, "Сүлжээний алдаа: ${e.message}", null)
        }
    }

    override suspend fun login(user: String, pass: String): Boolean {
        val result = loginWithFullResponse(user, pass)
        return result?.success == true
    }

    override suspend fun logAction(action: String) {
        // Лог хадгалах
    }

    override suspend fun register(user: String, email: String, pass: String): AuthResponse? = withContext(Dispatchers.IO) {
        Log.d("SpeedAuth", "Attempting register for: $user ($email)")
        return@withContext try {
            val request = AuthRequest(username = user, email = email, password = pass)
            val response = apiService.registerUser(request)
            
            if (response.isSuccessful) {
                val authResponse = response.body()
                Log.d("SpeedAuth", "Register response: $authResponse")
                authResponse
            } else {
                val errorStr = response.errorBody()?.string() ?: "Unknown error"
                Log.e("SpeedAuth", "Register failed: $errorStr")
                AuthResponse(false, "Бүртгэлд алдаа гарлаа (Code: ${response.code()})", null)
            }
        } catch (e: Exception) {
            Log.e("SpeedAuth", "Register Network error", e)
            AuthResponse(false, "Сүлжээний алдаа: ${e.message}", null)
        }
    }
}
