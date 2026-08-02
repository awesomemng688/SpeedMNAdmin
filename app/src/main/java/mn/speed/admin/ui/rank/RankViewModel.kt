package mn.speed.admin.ui.rank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mn.speed.admin.data.model.RankItem
import mn.speed.admin.data.repository.RankRepository
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(
    private val repository: RankRepository
) : ViewModel() {

    private val _ranks = MutableStateFlow<List<RankItem>>(emptyList())
    
    // Бүрэн жагсаалт (Top 100)
    val topThree: StateFlow<List<RankItem>> = _ranks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Бусад тоглогчид (Хэрэггүй болсон тул хоосон массив)
    val remainingPlayers: StateFlow<List<RankItem>> = MutableStateFlow<List<RankItem>>(emptyList()).asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchRanks(tabIndex: Int) {
        val type = when (tabIndex) {
            0 -> "pub1"
            1 -> "pub2"
            2 -> "knife1"
            3 -> "knife2"
            else -> "pub1"
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _ranks.value = repository.getRanks(type)
            _isLoading.value = false
        }
    }
}
