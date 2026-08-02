package mn.speed.admin.ui.check

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerCheckScreen(
    onBack: () -> Unit,
    viewModel: ServerCheckViewModel = hiltViewModel()
) {
    val statuses by viewModel.serverStatuses.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Server Health Check", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadAndCheckServers() }) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingSurface, titleContentColor = Color.White)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isRefreshing && statuses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GamingBlueAccent)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(statuses) { status ->
                        ServerHealthCard(status)
                    }
                }
            }
        }
    }
}

@Composable
fun ServerHealthCard(status: ServerStatus) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon with Pulse
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (status.isLoading) Color.Gray.copy(alpha = 0.2f)
                        else if (status.isOnline) Color.Green.copy(alpha = 0.1f * alpha)
                        else Color.Red.copy(alpha = 0.1f)
                    )
                    .border(
                        1.dp,
                        if (status.isLoading) Color.Gray else if (status.isOnline) Color.Green.copy(alpha = alpha) else Color.Red,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = if (status.isLoading) Color.Gray else if (status.isOnline) Color.Green else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.server.name ?: "Unknown Server",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = if (status.isLoading) "Checking..." else if (status.isOnline) status.map else "Offline",
                    color = if (status.isOnline) Color.Gray else Color.Red.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            if (!status.isLoading && status.isOnline) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, null, tint = GamingBlueAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${status.ping}ms", color = GamingBlueAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text(status.players, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
        }
    }
}
