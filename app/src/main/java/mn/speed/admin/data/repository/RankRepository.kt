package mn.speed.admin.data.repository

import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.model.RankItem
import javax.inject.Inject

class RankRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getRanks(type: String): List<RankItem> {
        return try {
            val response = apiService.getRanks(type)
            if (response.isSuccessful) {
                val body = response.body()
                android.util.Log.d("RankRepo", "Response size: ${body?.size}")
                body ?: emptyList()
            } else {
                android.util.Log.e("RankRepo", "Error Code: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("RankRepo", "Exception: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
