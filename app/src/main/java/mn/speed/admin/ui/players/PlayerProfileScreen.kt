package mn.speed.admin.ui.players

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import mn.speed.admin.ui.friends.FriendsViewModel
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    playerName: String,
    serverType: String = "pub1", // Энийг нэмэв
    onBack: () -> Unit,
    viewModel: PlayerProfileViewModel = hiltViewModel(),
    friendsViewModel: FriendsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val followedPlayers by friendsViewModel.friends.collectAsState()
    
    val isFollowing = remember(followedPlayers, playerName) { 
        followedPlayers.any { it.username.equals(playerName, ignoreCase = true) } 
    }

    LaunchedEffect(playerName, serverType) {
        viewModel.fetchPlayerProfile(playerName, serverType) // serverType дамжуулна
        friendsViewModel.loadFriends()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Leaderboard", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    val followColor = if (isFollowing) Color.Red else GamingBlueAccent
                    TextButton(onClick = {
                        if (isFollowing) {
                            friendsViewModel.unfollowPlayer(playerName)
                            Toast.makeText(context, "Unfollowed $playerName", Toast.LENGTH_SHORT).show()
                        } else {
                            friendsViewModel.followPlayer(playerName)
                            Toast.makeText(context, "Following $playerName", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(if (isFollowing) "UNFOLLOW" else "FOLLOW", color = followColor, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingDarkBackground, titleContentColor = Color.White)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GamingBlueAccent)
            }
        } else {
            stats?.let { playerStats ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. TOP HERO HEADER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GamingSurface)
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF238636)) 
                                    .border(2.dp, Color(0xFF238636).copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("R", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color.Black.copy(alpha = 0.5f))) {
                                    Text("OFFLINE", color = Color.White, fontSize = 8.sp, modifier = Modifier.align(Alignment.Center))
                                }
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(playerName.uppercase(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    Text(" Last played ${playerStats.lastPlayed}", color = Color.Gray, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    Text(" Joined ${playerStats.joinedDate}", color = Color.Gray, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(playerStats.steamId, color = Color.DarkGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            // Rank Number Card
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                                modifier = Modifier.size(70.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("#${playerStats.globalRank}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text("OF 12,484", color = Color.Gray, fontSize = 8.sp)
                                }
                            }
                        }
                    }

                    // 2. MAIN 4 STATS
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BigStatCard("SPEED RANK", "${playerStats.rankPoints}", modifier = Modifier.weight(1f))
                        val kdRatio = if(playerStats.deaths > 0) String.format("%.2f", playerStats.kills.toDouble()/playerStats.deaths) else "${playerStats.kills}.0"
                        BigStatCard("K/D RATIO", kdRatio, modifier = Modifier.weight(1f))
                        BigStatCard("ACCURACY", "${playerStats.accuracy}%", modifier = Modifier.weight(1f))
                        BigStatCard("WIN RATE", "${playerStats.winRate}%", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. STATISTICS SECTION
                    Text("STATISTICS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp)).background(GamingSurface).padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SmallStatItem("KILLS", formatNumber(playerStats.kills))
                            SmallStatItem("DEATHS", formatNumber(playerStats.deaths))
                            val kd = if(playerStats.deaths > 0) String.format("%.2f", playerStats.kills.toDouble()/playerStats.deaths) else "${playerStats.kills}.0"
                            SmallStatItem("K/D", kd)
                            SmallStatItem("ASSISTS", formatNumber(playerStats.assists))
                            SmallStatItem("MVP", formatNumber(playerStats.mvp))
                            SmallStatItem("1ST BLOOD", formatNumber(playerStats.firstBlood))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. COMBAT ANALYTICS
                    Text("COMBAT ANALYTICS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GamingSurface),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("GAUGES", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                                    CircularGauge(playerStats.accuracy, "ACCURACY")
                                    CircularGauge(playerStats.winRate, "WIN RATE")
                                }
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = GamingSurface),
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("DAMAGE ZONES", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                playerStats.damageZones.forEach { (zone, value) ->
                                    DamageRow(zone, value, 5000) 
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun BigStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun SmallStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun CircularGauge(percent: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier.size(50.dp),
                color = GamingBlueAccent,
                strokeWidth = 4.dp,
                trackColor = Color.DarkGray.copy(alpha = 0.3f)
            )
            Text("${percent}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DamageRow(label: String, value: Int, max: Int) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.Gray, fontSize = 9.sp)
            Text("$value", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { value.toFloat() / max.toFloat() },
            modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
            color = when(label) {
                "Head" -> Color.Red
                "Chest" -> Color.Yellow
                else -> GamingBlueAccent
            },
            trackColor = Color.DarkGray.copy(alpha = 0.2f)
        )
    }
}

fun formatNumber(num: Int): String {
    return if (num >= 1000) String.format("%.1fk", num / 1000f) else "$num"
}

fun getRankName(kills: Int): String {
    return when {
        kills >= 5000 -> "GLOBAL ELITE"
        kills >= 3000 -> "SUPREME MASTER"
        kills >= 2000 -> "LEGENDARY EAGLE"
        kills >= 1000 -> "MASTER GUARDIAN"
        kills >= 500 -> "GOLD NOVA"
        kills >= 100 -> "SILVER ELITE"
        else -> "SILVER I"
    }
}
