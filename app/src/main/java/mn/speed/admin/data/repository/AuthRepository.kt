package mn.speed.admin.data.repository

import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.model.AuthRequest
import mn.speed.admin.data.model.AuthResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(user: String, pass: String): Response<AuthResponse> {
        return apiService.loginUser(AuthRequest(username = user, password = pass))
    }

    suspend fun register(user: String, email: String, pass: String): Response<AuthResponse> {
        return apiService.registerUser(AuthRequest(username = user, email = email, password = pass))
    }
}
