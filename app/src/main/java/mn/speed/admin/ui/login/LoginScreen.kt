package mn.speed.admin.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mn.speed.admin.R
import mn.speed.admin.ui.theme.GamingBlueAccent
import mn.speed.admin.ui.theme.GamingDarkBackground
import mn.speed.admin.ui.theme.GamingSurface
import mn.speed.admin.utils.LocaleUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onBack: () -> Unit, // Шинэ
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    val languages = listOf(
        Pair("en", "EN"),
        Pair("mn", "MN"),
        Pair("ru", "RU"),
        Pair("ko", "KO"),
        Pair("ja", "JA")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.speedlogo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 12.dp)
                    )

                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Counter-Strike 1.6 System",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(stringResource(R.string.username)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.login(username, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingBlueAccent)
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(stringResource(R.string.login), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onRegisterClick) {
                        Text(stringResource(R.string.new_register), color = GamingBlueAccent)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { viewModel.loginAsGuest() }) {
                        Text(stringResource(R.string.continue_as_guest), color = Color.Gray, fontSize = 12.sp)
                    }

                    if (loginState is LoginState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Хэл солих хэсэг
            Text("Language / Хэл", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(languages) { lang ->
                    InputChip(
                        selected = false,
                        onClick = { LocaleUtils.applyLocale(context, lang.first) },
                        label = { Text(lang.second, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = InputChipDefaults.inputChipColors(
                            containerColor = GamingSurface,
                            labelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
