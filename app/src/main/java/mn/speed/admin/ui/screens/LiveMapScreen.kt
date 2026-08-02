package mn.speed.admin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import mn.speed.admin.R
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMapScreen(
    serverIp: String,
    serverPort: Int,
    onBack: () -> Unit,
    viewModel: LiveMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val statusResponse by viewModel.status.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startPolling(serverIp, serverPort)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(statusResponse?.info?.name ?: "Live Server Status", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = Color.White)
                    }
                },
                actions = {
                    // Copy Connect Command Button
                    IconButton(onClick = {
                        val command = "connect $serverIp:$serverPort"
                        clipboardManager.setText(AnnotatedString(command))
                        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingSurface, titleContentColor = Color.White)
            )
        },
        containerColor = GamingDarkBackground
    ) { padding ->
        if (isLoading && statusResponse == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GamingBlueAccent)
            }
        } else {
            val info = statusResponse?.info
            val players = statusResponse?.players ?: emptyList()

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                // Map Image Section
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                        AsyncImage(
                            model = info?.mapImageUrl,
                            contentDescription = "Map Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Transparent, GamingDarkBackground))
                            )
                        )
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                            Text("CURRENT MAP", color = GamingBlueAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(info?.map?.uppercase() ?: "---", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                // Server Stats Row
                item {
                    Card(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Online Status", color = Color.Gray, fontSize = 12.sp)
                                Text(
                                    if (info?.status == "online") "LIVE NOW" else "OFFLINE",
                                    color = if (info?.status == "online") Color(0xFF4CAF50) else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Players", color = Color.Gray, fontSize = 12.sp)
                                Text("${info?.players ?: 0} / ${info?.maxPlayers ?: 32}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }

                // Players List Header
                item {
                    Text(
                        "Players in Server",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // Players List
                if (players.isEmpty()) {
                    item {
                        Text(
                            "No players online",
                            color = Color.Gray,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    items(players) { player ->
                        PlayerLiveRow(player)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerLiveRow(player: mn.speed.admin.data.model.LivePlayer) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GamingSurface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Person, null, tint = GamingBlueAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(player.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            }
            Row {
                Text("${player.score} pts", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(end = 12.dp))
                Text(player.time, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
