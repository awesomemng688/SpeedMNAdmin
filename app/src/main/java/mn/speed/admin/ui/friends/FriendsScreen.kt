package mn.speed.admin.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun FriendsScreen(
    onBack: () -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val friends by viewModel.friends.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friends & Following", fontWeight = FontWeight.Bold) },
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
        } else if (friends.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Та хараахан хэн нэгнийг дагаагүй байна.", color = Color.Gray, fontSize = 14.sp)
                    Text("Тоглогчийн профиль дээр очиж дагаарай!", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(friends) { friend ->
                    FriendCard(
                        friend = friend,
                        onUnfollow = { viewModel.unfollowPlayer(friend.username) }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendCard(friend: FriendModel, onUnfollow: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (friend.isOnline) GamingBlueAccent.copy(alpha = 0.2f) else Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = if (friend.isOnline) GamingBlueAccent else Color.Gray)
                // Online indicator
                if (friend.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(friend.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (friend.isOnline) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, null, tint = Color.Green, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(friend.lastServer ?: "Online Now", color = Color.Green, fontSize = 12.sp)
                    }
                } else {
                    Text("Offline", color = Color.Gray, fontSize = 12.sp)
                }
            }

            IconButton(onClick = onUnfollow) {
                Icon(Icons.Default.PersonRemove, null, tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}
