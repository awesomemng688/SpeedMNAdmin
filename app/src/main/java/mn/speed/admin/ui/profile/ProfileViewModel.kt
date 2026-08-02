package mn.speed.admin.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import mn.speed.admin.data.local.AuthManager
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    fun getLanguage(): String = authManager.getLanguage()

    fun setLanguage(lang: String) {
        authManager.setLanguage(lang)
    }
}
