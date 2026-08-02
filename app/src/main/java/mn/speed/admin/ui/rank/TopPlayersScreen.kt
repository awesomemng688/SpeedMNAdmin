package mn.speed.admin.ui.rank

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import mn.speed.admin.data.model.RankItem
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopPlayersScreen(
    onBack: () -> Unit,
    onPlayerClick: (String, String) -> Unit,
    viewModel: RankViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val serverTypes = listOf("pub1", "pub2", "knife1", "knife2")
    val currentType = serverTypes[selectedTab]

    val allRanks by viewModel.topThree.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(selectedTab) {
        viewModel.fetchRanks(selectedTab)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Server Rankings", fontWeight = FontWeight.ExtraBold) },
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = GamingSurface,
                contentColor = GamingBlueAccent,
                edgePadding = 16.dp,
                divider = {}
            ) {
                val tabs = listOf("Public #1", "Public #2", "Knife #1", "Knife #2")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GamingBlueAccent)
                }
            } else if (allRanks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Өгөгдөл олдсонгүй", color = Color.Gray)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().background(GamingSurface).padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(30.dp))
                    Text("НЭР", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.weight(1f))
                    val label = if (currentType.contains("knife")) "KILLS" else "XP / OHOO"
                    Text(label, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(allRanks) { player ->
                        if (!player.playerName.isNullOrBlank()) {
                            RankTableRow(player, currentType) { 
                                onPlayerClick(player.playerName, currentType) 
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankTableRow(player: RankItem, currentType: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(30.dp), contentAlignment = Alignment.CenterStart) {
            if (player.rank <= 3) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = when(player.rank) {
                        1 -> Color(0xFFFFD700)
                        2 -> Color(0xFFC0C0C0)
                        else -> Color(0xFFCD7F32)
                    },
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(text = "${player.rank}", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            AsyncImage(
                model = player.avatarUrl ?: "https://api.dicebear.com/9.x/bottts/svg?seed=${player.playerName}",
                contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.DarkGray.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = player.playerName ?: "Unknown", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                
                val rankColor = getRankColor(player.skill ?: "")
                Surface(
                    color = rankColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, rankColor.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = player.skill ?: "SILVER I",
                        color = rankColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(90.dp)) {
            Text(
                text = String.format("%,d", player.xp),
                color = if (currentType.contains("knife")) Color(0xFFEF4444) else Color(0xFFDE9B35),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!currentType.contains("knife")) {
                    Text(text = "${player.hsPercent} HS", color = Color.Gray, fontSize = 9.sp)
                    Text(text = " | ", color = Color.DarkGray, fontSize = 9.sp)
                }
                Text(
                    text = "K/D ${player.kdRatio}", 
                    color = if(player.kdRatio >= 1.0) Color(0xFF00BFA5) else Color.Gray, 
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getRankColor(skill: String): Color {
    return when {
        skill.contains("GOD", true) -> Color(0xFFEF4444)
        skill.contains("ASSASSIN", true) -> Color(0xFFF97316)
        skill.contains("BLADE", true) -> Color(0xFFEAB308)
        skill.contains("SLICER", true) -> Color(0xFF38BDF8)
        skill.contains("GLOBAL", true) -> Color(0xFFDE9B35)
        skill.contains("SUPREME", true) -> Color(0xFFDE9B35)
        skill.contains("LEGENDARY", true) -> Color(0xFFDE9B35)
        else -> Color(0xFFDE9B35)
    }
}
