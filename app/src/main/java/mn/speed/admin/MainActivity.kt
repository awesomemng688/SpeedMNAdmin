package mn.speed.admin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import mn.speed.admin.data.local.AuthManager
import mn.speed.admin.ui.navigation.SetupNavGraph
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.SpeedMNAdminTheme
import mn.speed.admin.utils.LocaleUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Хэлний тохиргоог шалгаж, шаардлагатай бол хэрэглэх
        val currentLang = authManager.getLanguage()
        if (LocaleUtils.getLanguageCode(this) != currentLang) {
            LocaleUtils.applyLocale(this, currentLang)
        }

        setContent {
            // Push Notification Permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            // Get Firebase Token for Debug
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    android.util.Log.d("FCM", "Firebase Token: $token")
                }
            }

            val themeColorHex = authManager.getThemeColor()
            val primaryColor = try {
                Color(android.graphics.Color.parseColor(themeColorHex))
            } catch (e: Exception) {
                GamingBlueAccent
            }

            SpeedMNAdminTheme(primaryColor = primaryColor) {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController, authManager = authManager)
            }
        }
    }
}
