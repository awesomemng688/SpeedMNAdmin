package mn.speed.admin.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mn.speed.admin.data.local.AuthManager
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(
    authManager: AuthManager,
    onBack: () -> Unit,
    onRestartApp: () -> Unit
) {
    val currentTheme = remember { mutableStateOf(authManager.getThemeColor()) }
    
    val themes = listOf(
        "#1F6FEB" to "Default Blue",
        "#FFD700" to "Golden Warrior",
        "#DA3633" to "Red Dragon",
        "#238636" to "Forest CT",
        "#A371F7" to "Purple Neon",
        "#FFFFFF" to "White Ghost"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Theme", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingSurface, titleContentColor = Color.White)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Text("Select primary accent color:", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(themes) { (hex, name) ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    ThemeCard(
                        name = name,
                        color = color,
                        isSelected = currentTheme.value == hex,
                        onClick = {
                            authManager.setThemeColor(hex)
                            currentTheme.value = hex
                            onRestartApp()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeCard(name: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color)) {
                    if (isSelected) {
                        Icon(Icons.Default.Check, null, tint = if (color == Color.White) Color.Black else Color.White, modifier = Modifier.size(20.dp).align(Alignment.Center))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
