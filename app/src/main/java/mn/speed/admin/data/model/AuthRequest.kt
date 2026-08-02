package mn.speed.admin.data.model

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    val username: String,
    val email: String? = null,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String? = null,
    val role: String? = "player", // root_admin, server_admin, player
    @SerializedName("managed_servers")
    val managedServers: String? = "" // Жишээ нь: "5,6,7"
)
