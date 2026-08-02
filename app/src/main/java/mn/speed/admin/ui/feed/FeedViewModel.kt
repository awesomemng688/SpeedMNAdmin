package mn.speed.admin.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import javax.inject.Inject

data class FeedItem(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: String,
    val type: String // ACHIEVEMENT, CLAN, SYSTEM
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ирээдүйд apiService.getGlobalFeed() холбоно
                // Одоогоор Mock өгөгдөл
                _feedItems.value = listOf(
                    FeedItem("1", "Шинэ цол!", "Awesome.! 'GLOBAL ELITE' цол авлаа!", "10 минутын өмнө", "ACHIEVEMENT"),
                    FeedItem("2", "Шинэ клан", "Mongol Warriors клан шинээр үүсгэгдлээ.", "1 цагийн өмнө", "CLAN"),
                    FeedItem("3", "Мэдэгдэл", "Маргааш 20:00 цагт тэмцээн эхэлнэ.", "2 цагийн өмнө", "SYSTEM")
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
