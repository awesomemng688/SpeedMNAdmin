package mn.speed.admin.ui.clan

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import mn.speed.admin.R
import mn.speed.admin.data.model.ClanItem
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClanScreen(
    onBack: () -> Unit,
    onChatClick: (String) -> Unit,
    viewModel: ClanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clans by viewModel.clans.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isGuest = viewModel.isGuest
    val isAdmin = viewModel.isAdmin

    var showCreateDialog by remember { mutableStateOf(false) }
    var clanToDelete by remember { mutableStateOf<ClanItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clan Leaderboard", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingSurface, titleContentColor = Color.White)
            )
        },
        containerColor = GamingDarkBackground,
        floatingActionButton = {
            if (!isGuest) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = GamingBlueAccent,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, "Create Clan")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = GamingBlueAccent)
            } else if (clans.isEmpty()) {
                Text("Мэдээлэл олдсонгүй", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(clans) { clan ->
                        ClanCard(
                            clan = clan,
                            isGuest = isGuest,
                            isAdmin = isAdmin,
                            onJoin = {
                                clan.id?.let { viewModel.joinClan(it) }
                            },
                            onDelete = {
                                clanToDelete = clan
                            },
                            onChat = {
                                clan.id?.let { onChatClick(it) }
                            }
                        )
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateClanDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, tag, imageUri ->
                    viewModel.uploadLogoAndCreateClan(name, tag, imageUri)
                    showCreateDialog = false
                }
            )
        }

        if (clanToDelete != null) {
            AlertDialog(
                onDismissRequest = { clanToDelete = null },
                title = { Text("Клан устгах", color = Color.White) },
                text = { Text("'${clanToDelete?.name}' кланыг устгахдаа итгэлтэй байна уу?", color = Color.LightGray) },
                containerColor = GamingSurface,
                confirmButton = {
                    Button(
                        onClick = {
                            clanToDelete?.id?.let { viewModel.deleteClan(it) }
                            clanToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("УСТГАХ", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { clanToDelete = null }) {
                        Text("БОЛИХ", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun ClanCard(clan: ClanItem, isGuest: Boolean, isAdmin: Boolean, onJoin: () -> Unit, onDelete: () -> Unit, onChat: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onChat() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(
                    if (clan.rank <= 3) GamingBlueAccent.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.3f)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text("#${clan.rank}", color = if (clan.rank <= 3) GamingBlueAccent else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            AsyncImage(
                model = clan.logoUrl,
                contentDescription = null,
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "[${clan.tag ?: ""}] ${clan.name ?: "Unknown"}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${clan.membersCount} members | ${clan.points} pts", color = Color.Gray, fontSize = 11.sp)
            }

            if (!isGuest) {
                IconButton(onClick = onChat) {
                    Icon(Icons.AutoMirrored.Filled.Chat, "Chat", tint = GamingBlueAccent)
                }
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Button(
                    onClick = onJoin,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingBlueAccent)
                ) {
                    Text("JOIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun CreateClanDialog(onDismiss: () -> Unit, onCreate: (String, String, Uri?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Клан үүсгэх", color = Color.White) },
        containerColor = GamingSurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Кланы нэр") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Клан Таг (Жишээ: MGL)") }, modifier = Modifier.fillMaxWidth())
                
                // Image Picker UI
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, null, tint = GamingBlueAccent)
                            Text("Лого сонгох", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank() && tag.isNotBlank()) onCreate(name, tag, selectedImageUri) }) {
                Text("ҮҮСГЭХ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("БОЛИХ", color = Color.Gray)
            }
        }
    )
}
