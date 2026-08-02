package mn.speed.admin.ui.rcon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RconScreen(
    serverId: String,
    onBack: () -> Unit,
    viewModel: RconViewModel = hiltViewModel()
) {
    val logs by viewModel.consoleLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var commandText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new logs arrive
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RCON Console", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearLogs() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Logs", tint = Color.Gray)
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
                .padding(16.dp)
        ) {
            // Console Output Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(logs) { log ->
                        Text(
                            text = log,
                            color = if (log.startsWith(">")) GamingBlueAccent else Color.Green,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.TopEnd).size(20.dp),
                        strokeWidth = 2.dp,
                        color = GamingBlueAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commandText,
                    onValueChange = { commandText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter RCON command...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingBlueAccent,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        if (commandText.isNotBlank()) {
                            viewModel.sendCommand(serverId, commandText)
                            commandText = ""
                        }
                    },
                    containerColor = GamingBlueAccent,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}
