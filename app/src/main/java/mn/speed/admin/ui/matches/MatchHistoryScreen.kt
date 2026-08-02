package mn.speed.admin.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun MatchHistoryScreen(
    onBack: () -> Unit,
    viewModel: MatchHistoryViewModel = hiltViewModel()
) {
    val matches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match History", fontWeight = FontWeight.ExtraBold) },
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GamingBlueAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(matches) { match ->
                    MatchHistoryCard(match)
                }
            }
        }
    }
}

@Composable
fun MatchHistoryCard(match: MatchItem) {
    val resultColor = when(match.result) {
        "WIN" -> Color(0xFF238636)
        "LOSS" -> Color(0xFFDA3633)
        else -> Color.Gray
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Result Indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(resultColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(match.map.uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    if (match.mvp != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(14.dp))
                        Text("MVP", color = Color.Yellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(match.date, color = Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text("${match.kills} K", color = GamingBlueAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(" / ", color = Color.DarkGray)
                    Text("${match.deaths} D", color = Color.Red.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(match.result, color = resultColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(match.score, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
