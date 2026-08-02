package mn.speed.admin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mn.speed.admin.ui.admins.AdminsScreen
import mn.speed.admin.ui.admins.AdminsViewModel
import mn.speed.admin.ui.dashboard.DashboardScreen
import mn.speed.admin.ui.login.LoginScreen
import mn.speed.admin.ui.logs.LogsScreen
import mn.speed.admin.ui.logs.LogsViewModel
import mn.speed.admin.ui.players.PlayersScreen
import mn.speed.admin.ui.players.PlayersViewModel
import mn.speed.admin.ui.profile.ProfileScreen
import mn.speed.admin.ui.rank.TopPlayersScreen
import mn.speed.admin.ui.register.RegisterScreen
import mn.speed.admin.ui.server.ServerScreen
import mn.speed.admin.ui.screens.LiveMapScreen
import mn.speed.admin.ui.global.ServerCheckerScreen
import mn.speed.admin.ui.check.ServerCheckScreen
import mn.speed.admin.ui.clan.ClanChatScreen
import mn.speed.admin.ui.clan.ClanScreen
import mn.speed.admin.ui.feed.FeedScreen
import mn.speed.admin.ui.friends.FriendsScreen
import mn.speed.admin.ui.matches.MatchHistoryScreen
import mn.speed.admin.ui.players.PlayerProfileScreen
import mn.speed.admin.ui.rcon.RconScreen
import mn.speed.admin.ui.settings.ThemeSelectionScreen
import mn.speed.admin.ui.screens.DailyCheckInScreen
import mn.speed.admin.ui.viewmodel.ServerViewModel
import mn.speed.admin.data.local.AuthManager
import androidx.compose.ui.platform.LocalContext
import android.app.Activity

@Composable
fun SetupNavGraph(navController: NavHostController, authManager: AuthManager) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.ServerList.route) {
            val serverViewModel: ServerViewModel = hiltViewModel()
            ServerScreen(
                viewModel = serverViewModel,
                onBack = { navController.popBackStack() },
                onServerClick = { serverId ->
                    navController.navigate(Screen.LiveMap.createRoute(serverId))
                },
                onRconClick = { serverId ->
                    navController.navigate(Screen.RconConsole.createRoute(serverId))
                }
            )
        }

        composable(route = Screen.Players.route) {
            val playersViewModel: PlayersViewModel = hiltViewModel()
            val players by playersViewModel.players.collectAsState()
            PlayersScreen(
                players = players,
                onBackClick = { navController.popBackStack() },
                onKickClick = { player -> playersViewModel.kickPlayer(player) },
                onBanClick = { player -> playersViewModel.banPlayer(player) },
                onMuteClick = { player -> playersViewModel.mutePlayer(player) }
            )
        }

        composable(route = Screen.Admins.route) {
            val adminsViewModel: AdminsViewModel = hiltViewModel()
            AdminsScreen(onBackClick = { navController.popBackStack() }, viewModel = adminsViewModel)
        }

        composable(route = Screen.Rank.route) {
            TopPlayersScreen(
                onBack = { navController.popBackStack() },
                onPlayerClick = { playerName, serverType ->
                    navController.navigate(Screen.PlayerProfile.createRoute(playerName, serverType))
                }
            )
        }

        composable(route = Screen.Logs.route) {
            val logsViewModel: LogsViewModel = hiltViewModel()
            LogsScreen(viewModel = logsViewModel, onBack = { navController.popBackStack() })
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onThemeClick = { navController.navigate(Screen.ThemeSelection.route) }
            )
        }

        composable(route = Screen.LiveMap.route) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId")
            val serverViewModel: ServerViewModel = hiltViewModel()
            val servers by serverViewModel.servers.collectAsState()
            val server = servers.find { it.id == serverId }
            if (server != null) {
                LiveMapScreen(
                    serverIp = server.ip ?: "203.34.37.57",
                    serverPort = server.port,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(route = Screen.GlobalBrowser.route) {
            ServerCheckerScreen(
                onBack = { navController.popBackStack() },
                onCheckStatus = { ip, port -> navController.navigate("live_map_direct/$ip/$port") }
            )
        }

        composable(route = "live_map_direct/{ip}/{port}") { backStackEntry ->
            val ip = backStackEntry.arguments?.getString("ip") ?: ""
            val port = backStackEntry.arguments?.getString("port")?.toIntOrNull() ?: 27015
            LiveMapScreen(serverIp = ip, serverPort = port, onBack = { navController.popBackStack() })
        }

        composable(route = Screen.Clans.route) {
            ClanScreen(
                onBack = { navController.popBackStack() },
                onChatClick = { clanId -> navController.navigate(Screen.ClanChat.createRoute(clanId)) }
            )
        }

        composable(route = Screen.ClanChat.route) { backStackEntry ->
            val clanId = backStackEntry.arguments?.getString("clanId") ?: ""
            ClanChatScreen(clanId = clanId, onBack = { navController.popBackStack() })
        }

        composable(route = Screen.PlayerProfile.route) { backStackEntry ->
            val encodedName = backStackEntry.arguments?.getString("playerName") ?: ""
            val serverType = backStackEntry.arguments?.getString("serverType") ?: "pub1"
            val playerName = java.net.URLDecoder.decode(encodedName, "UTF-8")
            PlayerProfileScreen(
                playerName = playerName,
                serverType = serverType,
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.ThemeSelection.route) {
            ThemeSelectionScreen(
                authManager = authManager,
                onBack = { navController.popBackStack() },
                onRestartApp = { (context as? Activity)?.recreate() }
            )
        }

        composable(route = Screen.DailyRewards.route) {
            DailyCheckInScreen(onBack = { navController.popBackStack() })
        }

        composable(route = Screen.FriendsList.route) {
            FriendsScreen(onBack = { navController.popBackStack() })
        }

        composable(route = Screen.GlobalFeed.route) {
            FeedScreen(onBack = { navController.popBackStack() })
        }

        composable(route = Screen.MatchHistory.route) {
            MatchHistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(route = Screen.ServerCheck.route) {
            ServerCheckScreen(onBack = { navController.popBackStack() })
        }

        composable(route = Screen.RconConsole.route) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            RconScreen(serverId = serverId, onBack = { navController.popBackStack() })
        }
    }
}
