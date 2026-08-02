package mn.speed.admin.data.repository

import mn.speed.admin.data.model.AuthResponse

interface SpeedRepository {
    suspend fun login(user: String, pass: String): Boolean
    suspend fun logAction(action: String)
    suspend fun register(user: String, email: String, pass: String): AuthResponse?
}
