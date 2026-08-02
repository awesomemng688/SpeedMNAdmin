package mn.speed.admin.data.model

data class AdminModel(
    val id: String,
    val username: String,
    val flags: String,
    val authType: String, // "SteamID", "IP", эсвэл "Password"
    val expireDate: String,
    val isActive: Boolean = true
)