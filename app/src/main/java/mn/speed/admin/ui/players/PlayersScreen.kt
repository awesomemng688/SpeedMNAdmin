package mn.speed.admin.ui.players

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mn.speed.admin.R
import mn.speed.admin.data.model.PlayerModel
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    players: List<PlayerModel>,
    onBackClick: () -> Unit,
    onKickClick: (PlayerModel) -> Unit,
    onBanClick: (PlayerModel) -> Unit,
    onMuteClick: (PlayerModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredPlayers = players.filter { it.name.contains(searchQuery, ignoreCase = true) }

    var playerToConfirmAction by remember { mutableStateOf<Pair<PlayerModel, String>?>(null) } // Player to ActionType

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.player_management), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingSurface)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Хайлтын хэсэг
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                placeholder = { Text("Тоглогч хайх...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, null, tint = Color.Gray)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GamingBlueAccent,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.online_players, filteredPlayers.size),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (searchQuery.isNotEmpty()) {
                    Text("Хайлт: '$searchQuery'", color = GamingBlueAccent, fontSize = 12.sp)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredPlayers) { player ->
                    PlayerItemCard(
                        player = player,
                        onKick = { playerToConfirmAction = player to "KICK" },
                        onBan = { playerToConfirmAction = player to "BAN" },
                        onMute = { onMuteClick(player) }
                    )
                }
            }
        }

        // Баталгаажуулах цонх
        playerToConfirmAction?.let { (player, action) ->
            AlertDialog(
                onDismissRequest = { playerToConfirmAction = null },
                title = { Text(if (action == "BAN") "Тоглогчийг BAN-дах" else "Тоглогчийг KICK-лэх", color = Color.White) },
                text = { 
                    Text(
                        "Та '${player.name}' тоглогчийг системээс $action хийхдээ итгэлтэй байна уу?", 
                        color = Color.LightGray
                    ) 
                },
                containerColor = GamingSurface,
                confirmButton = {
                    Button(
                        onClick = {
                            if (action == "BAN") onBanClick(player) else onKickClick(player)
                            playerToConfirmAction = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (action == "BAN") Color.Red else Color(0xFFFF9800)
                        )
                    ) {
                        Text("ТЙИМ", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { playerToConfirmAction = null }) {
                        Text("БОЛИХ", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun PlayerItemCard(
    player: PlayerModel,
    onKick: () -> Unit,
    onBan: () -> Unit,
    onMute: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GamingBlueAccent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = GamingBlueAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Column {
                    Text(
                        text = player.name,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${player.score} pts",
                            fontSize = 12.sp,
                            color = GamingBlueAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(" | ", color = Color.DarkGray)
                        Text(
                            text = "${player.ping}ms",
                            fontSize = 11.sp,
                            color = if (player.ping < 50) Color.Green else if (player.ping < 100) Color.Yellow else Color.Red
                        )
                        Text(" | ", color = Color.DarkGray)
                        Text(
                            text = player.time,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mute
                Surface(
                    onClick = onMute,
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.VolumeOff, null, tint = Color.Yellow, modifier = Modifier.size(18.dp))
                    }
                }
                
                // Kick
                Surface(
                    onClick = onKick,
                    color = Color(0xFFFF9800).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Gavel, null, tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
                    }
                }

                // Ban
                Surface(
                    onClick = onBan,
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Block, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
