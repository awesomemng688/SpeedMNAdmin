package mn.speed.admin.ui.admins

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import mn.speed.admin.R
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

data class AdminModel(
    val id: String,
    val username: String,
    val steamIdOrIp: String,
    val flags: String,
    val serverName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminsScreen(
    onBackClick: () -> Unit,
    viewModel: AdminsViewModel = hiltViewModel()
) {
    val admins by viewModel.admins.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val filteredAdmins = admins.filter { 
        it.username.contains(searchQuery, ignoreCase = true) || 
        it.steamIdOrIp.contains(searchQuery, ignoreCase = true)
    }

    var adminToDelete by remember { mutableStateOf<AdminModel?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_mgmt), color = Color.White, fontWeight = FontWeight.Bold) },
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
        containerColor = GamingDarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GamingBlueAccent,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_admin))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                placeholder = { Text("Админ хайх...", color = Color.Gray) },
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

            Text(
                text = stringResource(R.string.active_admins, filteredAdmins.size),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredAdmins) { admin ->
                    AdminItemCard(
                        admin = admin,
                        onEdit = { /* TODO */ },
                        onDelete = { adminToDelete = admin }
                    )
                }
            }
        }

        // Delete Confirmation Dialog
        adminToDelete?.let { admin ->
            AlertDialog(
                onDismissRequest = { adminToDelete = null },
                title = { Text("Админ устгах", color = Color.White) },
                text = { Text("'${admin.username}' админыг устгахдаа итгэлтэй байна уу?", color = Color.LightGray) },
                containerColor = GamingSurface,
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteAdmin(admin)
                            adminToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("УСТГАХ", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { adminToDelete = null }) {
                        Text("БОЛИХ", color = Color.Gray)
                    }
                }
            )
        }

        if (showAddDialog) {
            AddAdminDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { username, steamid, flags, server ->
                    viewModel.addAdmin(username, steamid, flags, server)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddAdminDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var steamid by remember { mutableStateOf("") }
    var flags by remember { mutableStateOf("abcdefghijklu") }
    var server by remember { mutableStateOf("Global") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Шинэ админ нэмэх", color = Color.White) },
        containerColor = GamingSurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Нэр") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = steamid, onValueChange = { steamid = it }, label = { Text("SteamID эсвэл IP") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = flags, onValueChange = { flags = it }, label = { Text("Эрхийн Flags") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = server, onValueChange = { server = it }, label = { Text("Сервер") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { 
                if (username.isNotBlank() && steamid.isNotBlank()) {
                    onConfirm(username, steamid, flags, server)
                }
            }) {
                Text("НЭМЭХ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("БОЛИХ", color = Color.Gray)
            }
        }
    )
}

@Composable
fun AdminItemCard(
    admin: AdminModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GamingBlueAccent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = GamingBlueAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = admin.username,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = admin.steamIdOrIp,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dns, null, tint = GamingBlueAccent, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = admin.serverName,
                            fontSize = 11.sp,
                            color = GamingBlueAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = stringResource(R.string.delete),
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
