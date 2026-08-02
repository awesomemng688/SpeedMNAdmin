package mn.speed.admin.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mn.speed.admin.R
import mn.speed.admin.ui.navigation.Screen
import mn.speed.admin.ui.players.PlayerStats
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface
import mn.speed.admin.ui.theme.GamingBlueGlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val username = viewModel.username
    val role = viewModel.userRole
    val personalStats by viewModel.personalStats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Speed.mn Control Center", 
                        color = Color.White, 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.SemiBold 
                    ) 
                },
                actions = {
                    IconButton(onClick = {
                        if (role != "guest") viewModel.logout()
                        onLogout()
                    }) {
                        Icon(
                            imageVector = if (role == "guest") Icons.Default.Login else Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingDarkBackground)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Personal Progress Section (If Logged In)
            if (role != "guest" && personalStats != null) {
                item(span = { GridItemSpan(2) }) {
                    PersonalProgressCard(personalStats!!)
                }
            }

            // 2. Announcement Banner
            item(span = { GridItemSpan(2) }) {
                AnnouncementBanner()
            }

            // 3. Greeting
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Welcome back, $username!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // 4. Main Grid Cards
            item {
                DashboardCard(
                    title = "Followed Players",
                    subtitle = "Online Friends",
                    icon = Icons.Default.PersonSearch,
                    onClick = { onNavigate(Screen.FriendsList.route) }
                )
            }
            item {
                DashboardCard(
                    title = "Daily Reward",
                    subtitle = "Get Free Points",
                    icon = Icons.Default.CardGiftcard,
                    onClick = { onNavigate(Screen.DailyRewards.route) }
                )
            }
            item {
                DashboardCard(
                    title = "Match History",
                    subtitle = "Recent Results",
                    icon = Icons.Default.History,
                    onClick = { onNavigate(Screen.MatchHistory.route) }
                )
            }
            item {
                DashboardCard(
                    title = "Servers",
                    subtitle = "CS 1.6 Nodes",
                    icon = Icons.Default.Storage,
                    onClick = { onNavigate(Screen.ServerList.route) }
                )
            }
            item {
                DashboardCard(
                    title = "Clan System",
                    subtitle = "Clan Leaderboard",
                    icon = Icons.Default.Groups,
                    onClick = { onNavigate(Screen.Clans.route) }
                )
            }
            item {
                DashboardCard(
                    title = "Rank System",
                    subtitle = "Stats & Leaderboard",
                    icon = Icons.Default.BarChart,
                    onClick = { onNavigate(Screen.Rank.route) }
                )
            }
            item {
                DashboardCard(
                    title = "Server Checker",
                    subtitle = "Check any CS server",
                    icon = Icons.Default.ManageSearch,
                    onClick = { onNavigate(Screen.GlobalBrowser.route) }
                )
            }

            // Admin Only Cards
            if (role == "admin" || role == "owner") {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "Admin Management",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                item {
                    DashboardCard(
                        title = "Players",
                        subtitle = "Kick / Ban Players",
                        icon = Icons.Default.People,
                        onClick = { onNavigate(Screen.Players.route) }
                    )
                }
                item {
                    DashboardCard(
                        title = "Admins",
                        subtitle = "Manage Privileges",
                        icon = Icons.Default.Shield,
                        onClick = { onNavigate(Screen.Admins.route) }
                    )
                }
                item {
                    DashboardCard(
                        title = "Logs",
                        subtitle = "Admin Actions",
                        icon = Icons.Default.Description,
                        onClick = { onNavigate(Screen.Logs.route) }
                    )
                }
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PersonalProgressCard(stats: PlayerStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBlueAccent.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("PERSONAL PROGRESS", color = GamingBlueAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(stats.skill, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
                Surface(
                    color = GamingBlueAccent.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBlueAccent.copy(alpha = 0.3f))
                ) {
                    Text(
                        "${stats.rankPoints} PTS",
                        color = GamingBlueAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar to Next Rank
            val nextRankPoints = 5000 // Example
            val progress = (stats.rankPoints.toFloat() / nextRankPoints).coerceIn(0f, 1f)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Rank Progress", color = Color.Gray, fontSize = 11.sp)
                Text("${(progress * 100).toInt()}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = GamingBlueAccent,
                trackColor = Color.DarkGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatMiniItem("KILLS", stats.kills.toString())
                StatMiniItem("DEATHS", stats.deaths.toString())
                val kd = if(stats.deaths > 0) String.format("%.2f", stats.kills.toDouble()/stats.deaths) else "0.0"
                StatMiniItem("K/D", kd)
                StatMiniItem("WIN RATE", "${stats.winRate}%")
            }
        }
    }
}

@Composable
fun StatMiniItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun AnnouncementBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // Blue indicator dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(GamingBlueAccent)
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Speed.mn Сүлжээнд тавтай морил",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Манай шинэ аппликейшн ашиглалтанд орлоо.",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Text(
                text = "Тоглогчиддоо амжилт хүсье!",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GamingBlueAccent,
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}
