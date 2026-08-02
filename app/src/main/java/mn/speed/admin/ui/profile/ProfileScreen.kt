package mn.speed.admin.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mn.speed.admin.R
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface
import mn.speed.admin.utils.LocaleUtils

data class LangItem(val code: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onThemeClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var currentLang by remember { mutableStateOf(viewModel.getLanguage()) }
    
    val languages = listOf(
        LangItem("en", "English"),
        LangItem("mn", "Монгол"),
        LangItem("ru", "Русский"),
        LangItem("ko", "한국어"),
        LangItem("ja", "日本語")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_profile), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingSurface)
            )
        },
        containerColor = GamingDarkBackground
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(GamingBlueAccent),
                contentAlignment = Alignment.Center
            ) {
                Text("A", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Awesome.!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Root Server Administrator", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // Мэдээллийн карт
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${stringResource(R.string.role)}: Full Access (a-z flags)", color = GamingBlueAccent, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${stringResource(R.string.managed_network)}: Speed.mn", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${stringResource(R.string.system_status)}: ${stringResource(R.string.connected)}", color = Color(0xFF238636), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Options
            Button(
                onClick = onThemeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GamingSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ColorLens, null, tint = GamingBlueAccent)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Change Theme Color", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Хэлний сонголт
            Text(
                stringResource(R.string.language),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
            )
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(languages) { lang ->
                    FilterChip(
                        selected = currentLang == lang.code,
                        onClick = {
                            currentLang = lang.code
                            viewModel.setLanguage(lang.code)
                            LocaleUtils.applyLocale(context, lang.code)
                        },
                        label = { Text(lang.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GamingBlueAccent,
                            selectedLabelColor = Color.White,
                            containerColor = GamingSurface,
                            labelColor = Color.Gray
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { /* Logout */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA3633))
            ) {
                Text(stringResource(R.string.logout), fontWeight = FontWeight.Bold)
            }
        }
    }
}
