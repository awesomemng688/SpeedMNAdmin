package mn.speed.admin.ui.navigation

@Suppress("unused")
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object ServerList : Screen("server_list")
    object ServerDetail : Screen("server_detail/{serverId}") {
        fun createRoute(serverId: String) = "server_detail/$serverId"
    }
    object Players : Screen("players")
    object Rank : Screen("rank")
    object Admins : Screen("admins")
    object Logs : Screen("logs")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object LiveMap : Screen("live_map/{serverId}") {
        fun createRoute(serverId: String) = "live_map/$serverId"
    }
    object TopPlayers : Screen("top_players")
    object GlobalBrowser : Screen("global_browser")
    object Clans : Screen("clans")
    object ClanChat : Screen("clan_chat/{clanId}") {
        fun createRoute(clanId: String) = "clan_chat/$clanId"
    }
    object PlayerProfile : Screen("player_profile/{playerName}/{serverType}") {
        fun createRoute(playerName: String, serverType: String): String {
            val encodedName = java.net.URLEncoder.encode(playerName, "UTF-8")
            return "player_profile/$encodedName/$serverType"
        }
    }
    object ThemeSelection : Screen("theme_selection")
    object DailyRewards : Screen("daily_rewards")
    object FriendsList : Screen("friends_list")
    object GlobalFeed : Screen("global_feed")
    object MatchHistory : Screen("match_history")
    object ServerCheck : Screen("server_check")
    object RconConsole : Screen("rcon_console/{serverId}") {
        fun createRoute(serverId: String) = "rcon_console/$serverId"
    }
}
