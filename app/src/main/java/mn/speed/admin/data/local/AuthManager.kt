package mn.speed.admin.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val prefs: SharedPreferences
) {

    companion object {
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val KEY_USER_ROLE = "KEY_USER_ROLE"
        private const val KEY_MANAGED_SERVERS = "KEY_MANAGED_SERVERS"
        private const val KEY_USERNAME = "KEY_USERNAME"
        private const val KEY_LANGUAGE = "KEY_LANGUAGE"
        private const val KEY_RECENT_SERVERS = "KEY_RECENT_SERVERS"
        private const val KEY_THEME_COLOR = "KEY_THEME_COLOR"
    }

    fun getThemeColor(): String = prefs.getString(KEY_THEME_COLOR, "#1F6FEB") ?: "#1F6FEB"
    fun setThemeColor(hex: String) {
        prefs.edit { putString(KEY_THEME_COLOR, hex) }
    }

    fun saveSession(username: String, token: String?, role: String?, managedServers: String?) {
        prefs.edit {
            putString(KEY_USERNAME, username)
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_ROLE, role ?: "player")
            putString(KEY_MANAGED_SERVERS, managedServers ?: "")
        }
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)
    
    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    fun setLanguage(lang: String) {
        prefs.edit { putString(KEY_LANGUAGE, lang) }
    }

    fun getRecentServers(): List<String> {
        val serversStr = prefs.getString(KEY_RECENT_SERVERS, "") ?: ""
        return if (serversStr.isEmpty()) emptyList() else serversStr.split("|")
    }

    fun addRecentServer(addr: String) {
        val current = getRecentServers().toMutableList()
        if (current.contains(addr)) {
            current.remove(addr)
        }
        current.add(0, addr)
        val result = current.take(5).joinToString("|")
        prefs.edit { putString(KEY_RECENT_SERVERS, result) }
    }

    fun getRole(): String = prefs.getString(KEY_USER_ROLE, "guest") ?: "guest"
    fun getManagedServers(): List<String> {
        val serversStr = prefs.getString(KEY_MANAGED_SERVERS, "") ?: ""
        return if (serversStr.isEmpty()) emptyList() else serversStr.split(",")
    }

    fun clearSession() {
        prefs.edit {
            clear()
        }
    }

    fun setGuestSession() {
        prefs.edit {
            putString(KEY_USERNAME, "Guest")
            putString(KEY_USER_ROLE, "guest")
        }
    }

    fun isLoggedIn(): Boolean {
        return getUsername() != null
    }

    fun isGuest(): Boolean = getRole() == "guest"

    fun isRootAdmin(): Boolean = getRole() == "root_admin" || getRole() == "root_role"
    fun isServerAdmin(): Boolean = getRole() == "server_admin"
    fun isPlayer(): Boolean = getRole() == "player"

    fun canManageServer(serverId: String): Boolean {
        if (isRootAdmin()) return true
        return getManagedServers().contains(serverId)
    }
}
