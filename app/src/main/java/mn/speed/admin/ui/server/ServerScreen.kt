package mn.speed.admin.ui.server

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import mn.speed.admin.R
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface
import mn.speed.admin.ui.viewmodel.ServerViewModel

val SpeedOrange = Color(0xFFDE9B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerScreen(
    onBack: () -> Unit,
    onServerClick: (String) -> Unit,
    onRconClick: (String) -> Unit,
    viewModel: ServerViewModel
) {
    val context = LocalContext.current
    val servers by viewModel.servers.collectAsState()
    val totalPlayers by viewModel.totalPlayers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showConfirmDialog by remember { mutableStateOf<Pair<String, String>?>(null) } 

    LaunchedEffect(key1 = true) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        viewModel.startRealtimeUpdates()
        onDispose {
            viewModel.stopRealtimeUpdates()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "Server Network", 
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Manage & Monitor",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, 
                            contentDescription = stringResource(R.string.back), 
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchServers() }) {
                        Icon(Icons.Default.Refresh, "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GamingSurface
                )
            )
        },
        containerColor = GamingDarkBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Network Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GamingSurface)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(label = "ACTIVE SERVERS", value = "${servers.count { it.isOnline }}", color = Color(0xFF4CAF50))
                    HorizontalDivider(modifier = Modifier.height(30.dp).width(1.dp), color = Color.DarkGray)
                    StatItem(label = "TOTAL PLAYERS", value = "$totalPlayers", color = SpeedOrange)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(servers) { server ->
                        PremiumServerCard(
                            server = server, 
                            canManage = viewModel.canManageServer(server.id),
                            onAction = { action -> 
                                if (action == "rcon") {
                                    onRconClick(server.id)
                                } else {
                                    showConfirmDialog = server.id to action
                                }
                            },
                            onClick = { onServerClick(server.id) }
                        )
                    }
                }
            }

            if (isLoading && servers.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = SpeedOrange)
            }
        }

        // Action Confirmation Dialog
        showConfirmDialog?.let { pair ->
            AlertDialog(
                onDismissRequest = { showConfirmDialog = null },
                title = { Text("${pair.second.uppercase()} Server", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { 
                    Text(
                        "Are you sure you want to perform this action? This will affect all players on the server.", 
                        color = Color.LightGray
                    ) 
                },
                containerColor = GamingSurface,
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.controlServer(pair.first, pair.second)
                            showConfirmDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when(pair.second) {
                                "stop" -> Color.Red
                                "start" -> Color(0xFF4CAF50)
                                else -> SpeedOrange
                            }
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("EXECUTE", fontWeight = FontWeight.ExtraBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = null }) {
                        Text("CANCEL", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun PremiumServerCard(
    server: mn.speed.admin.data.model.ServerItem,
    canManage: Boolean,
    onAction: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                AsyncImage(
                    model = server.mapImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.3f)))))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusBadge(text = if (server.isOnline) "ONLINE" else "OFFLINE", color = if (server.isOnline) Color(0xFF4CAF50) else Color.Red)
                        StatusBadge(text = "CS 1.6", color = Color.Gray)
                    }
                    Icon(Icons.Default.SignalCellularAlt, null, tint = if (server.isOnline) Color(0xFF4CAF50) else Color.Red, modifier = Modifier.size(16.dp))
                }

                Box(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).clip(RoundedCornerShape(4.dp)).background(Color.Black.copy(alpha = 0.7f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, null, tint = SpeedOrange, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = server.map?.uppercase() ?: "UNKNOWN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = server.name?.uppercase() ?: "UNKNOWN SERVER", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.players_label), color = SpeedOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("${server.players} / ${server.maxPlayers}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = { server.players.toFloat() / server.maxPlayers.toFloat() }, modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape), color = SpeedOrange, trackColor = Color.DarkGray)

                if (canManage) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        ControlSmallButton(text = "START", color = Color(0xFF4CAF50), icon = Icons.Default.PlayArrow, onClick = { onAction("start") }, modifier = Modifier.weight(1f))
                        ControlSmallButton(text = "STOP", color = Color(0xFFF44336), icon = Icons.Default.Stop, onClick = { onAction("stop") }, modifier = Modifier.weight(1f))
                        ControlSmallButton(text = "RESTART", color = Color(0xFF2196F3), icon = Icons.Default.Refresh, onClick = { onAction("restart") }, modifier = Modifier.weight(1f))
                        
                        IconButton(
                            onClick = { onAction("rcon") },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.Terminal, "RCON Console", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = server.fullAddress, color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(SpeedOrange).clickable { /* Join */ }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ControlSmallButton(text: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(color = Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(4.dp), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))) {
        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            if (text == "ONLINE") {
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = text, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
