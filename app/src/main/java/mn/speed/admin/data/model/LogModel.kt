package mn.speed.admin.data.model

data class LogModel(
    val id: String,
    val timestamp: String,
    val tag: String, // "INFO", "WARN", "ERROR", "ADMIN"
    val message: String
)