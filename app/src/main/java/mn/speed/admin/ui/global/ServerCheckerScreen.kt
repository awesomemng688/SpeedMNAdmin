package mn.speed.admin.ui.global

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mn.speed.admin.R
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerCheckerScreen(
    onBack: () -> Unit,
    onCheckStatus: (String, Int) -> Unit,
    viewModel: ServerCheckerViewModel = hiltViewModel()
) {
    var ipAddress by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("27015") }
    val recentServers by viewModel.recentServers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Server Status Checker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingSurface, titleContentColor = Color.White)
            )
        },
        containerColor = GamingDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Icon(
                imageVector = Icons.Default.Dns,
                contentDescription = null,
                tint = GamingBlueAccent,
                modifier = Modifier.size(70.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Check Any CS 1.6 Server",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // IP Input
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Server IP Address") },
                placeholder = { Text("e.g. 203.34.37.57") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Port Input
            OutlinedTextField(
                value = port,
                onValueChange = { if (it.all { char -> char.isDigit() }) port = it },
                label = { Text("Server Port") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (ipAddress.isNotBlank()) {
                        val p = port.toIntOrNull() ?: 27015
                        viewModel.addRecentServer(ipAddress, p)
                        onCheckStatus(ipAddress, p)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GamingBlueAccent)
            ) {
                Icon(Icons.Default.Search, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CHECK STATUS", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            // Recent Checks Section
            if (recentServers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.recent_checks), color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentServers) { addr ->
                        RecentServerChip(addr = addr) {
                            val parts = addr.split(":")
                            if (parts.size == 2) {
                                ipAddress = parts[0]
                                port = parts[1]
                                onCheckStatus(parts[0], parts[1].toIntOrNull() ?: 27015)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentServerChip(addr: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = GamingSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
    ) {
        Text(
            text = addr,
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}
