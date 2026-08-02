package mn.speed.admin.data.model

data class PlayerModel(
    val id: String,
    val name: String,
    val ping: Int = 0,
    val score: Int = 0,      // <- Энийг нэмнэ
    val time: String = ""    // <- Энийг нэмнэ
)