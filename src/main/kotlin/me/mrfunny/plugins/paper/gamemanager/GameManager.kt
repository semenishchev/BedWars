package me.mrfunny.plugins.paper.gamemanager

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import com.ruverq.rubynex.economics.Main
import dev.jcsoftware.jscoreboards.JScoreboard
import dev.jcsoftware.jscoreboards.JScoreboardOptions
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle
import me.mrfunny.api.MySQLManager
import me.mrfunny.plugins.paper.BedWars
import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.BedWars.Companion.log
import me.mrfunny.plugins.paper.config.ConfigurationManager
import me.mrfunny.api.CustomConfiguration
import me.mrfunny.plugins.paper.gameutils.StartingPower
import me.mrfunny.plugins.paper.gui.GUIManager
import me.mrfunny.plugins.paper.gui.shops.teamupgrades.MaxLevel
import me.mrfunny.plugins.paper.gui.shops.teamupgrades.UpgradeItem
import me.mrfunny.plugins.paper.messages.MessagesManager
import me.mrfunny.plugins.paper.players.NoFallPlayers
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.players.PlayerManager
import me.mrfunny.plugins.paper.setup.SetupWizardManager
import me.mrfunny.plugins.paper.tasks.GameStartingTask
import me.mrfunny.plugins.paper.tasks.GameTickTask
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.Cooldowns
import me.mrfunny.plugins.paper.util.ItemBuilder
import me.mrfunny.plugins.paper.worlds.GameWorld
import me.mrfunny.plugins.paper.worlds.Island
import me.mrfunny.plugins.paper.worlds.IslandColor
import org.apache.commons.lang.StringUtils
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class GameManager(val plugin: BedWars, version: Double) {

    val scoreboard: JScoreboard = JScoreboard(JScoreboardOptions("&c&lBEDWARS".colorize(), JScoreboardTabHealthStyle.NONE, true))
    val setupWizardManager: SetupWizardManager = SetupWizardManager
    val configurationManager: ConfigurationManager = ConfigurationManager(this)
    val guiManager: GUIManager = GUIManager
    val gameConfig = CustomConfiguration("gameconfig", plugin)
    var isBetatest = false

    val messagingChannel = "selector:bedwars"

    val englishMessages = CustomConfiguration("messages_en", plugin)
    val russianMessages = CustomConfiguration("messages_ru", plugin)
    val itemsLocalization = CustomConfiguration("items", plugin)
    val hubCount = 1

    val sql = MySQLManager(plugin)

    var messages: MessagesManager
    val playerManager: PlayerManager = PlayerManager(this)
    val cooldowns: Cooldowns = Cooldowns()
    val deadPlayers = arrayListOf<UUID>()

    lateinit var world: GameWorld

    var gameStartingTask: GameStartingTask? = null
    private lateinit var gameTickTask: GameTickTask
    val bossBar: BossBar = Bukkit.createBossBar("Waiting", BarColor.GREEN, BarStyle.SEGMENTED_20)

    val startingPowers = arrayOf(
        StartingPower(
            "x2blocks",
            ItemBuilder(Material.WHITE_WOOL).setName("&aX6 blocks").toItemStack(),
            { it.canBuyForSale = true },
            { it.canBuyForSale = false }),
        StartingPower("speed", ItemBuilder(Material.SUGAR).setName("&aSpeed II").toItemStack(), {
            it.player.addPotionEffect(
                PotionEffect(PotionEffectType.SPEED, 60 * 20, 1)
            )
        }, {}),
        StartingPower(
            "generator",
            ItemBuilder(Material.IRON_INGOT).setName("&aGenerator upgrade").toItemStack(),
            { it.isGeneratorMultiplier = true },
            { it.isGeneratorMultiplier = false }),
        StartingPower("strength", ItemBuilder(Material.DIAMOND_SWORD).setName("&aStrength I").toItemStack(), {
            it.player.addPotionEffect(
                PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90 * 20, 0)
            )
        }, {}),
    )
    val upgrades: Array<UpgradeItem> = arrayOf(
        UpgradeItem(
            11, "armor", "", ItemBuilder(Material.DIAMOND_CHESTPLATE).setName("&aArmor protection").setLore(
                "",
                "&aArmor Upgrade"
            ).toItemStack(), MaxLevel.FOUR, 2, 4, 6, 8
        ),
        UpgradeItem(
            12,
            "generator",
            "",
            ItemBuilder(Material.BEACON).setName("&aGenerator upgrade").toItemStack(),
            MaxLevel.THREE,
            2,
            4,
            8
        ),
    )

    fun getIslandByColor(color: IslandColor): Island{
        for (island in world.islands){
            if(island.color == color){
                return island
            }
        }

        return world.islands.first()
    }


    var secondsTimer = 5 * 60

    init {
        "Reached target &aGameManager".log()
        configurationManager.loadWorld(configurationManager.randomMapName()) { world: GameWorld ->
            this.world = world
            state = GameState.LOBBY

            for(island: Island in world.islands){
                this.scoreboard.createTeam(island.color.formattedName(), "", island.color.getChatColor())
            }
        }
        if(!plugin.config.isConfigurationSection("version")){
            plugin.config.set("version", version)
        }
        messages = MessagesManager(this)
        "&f[&aOK&f] GameManager started".log()
        updateScoreboard(true)
    }
    
    var state: GameState = GameState.PRELOBBY
    set(value) {
        field = value
        when(value){
            GameState.LOBBY -> {
                if (isStarting()) {
                    Bukkit.broadcastMessage("Start failed!")
                    Bukkit.getOnlinePlayers()
                        .forEach { it.sendTitle("&cNot enough players".colorize(), null, 0, 40, 10) }
                    gameStartingTask?.cancel()
                    gameStartingTask = null
                    bossBar.setTitle("Waiting for more players")
                    bossBar.progress = 1.0
                }
            }
            GameState.STARTING -> {
                if (!isStarting()) {
                    gameStartingTask = GameStartingTask(this, 20)
                    gameStartingTask!!.runTaskTimer(plugin, 0, 20)
                }
            }
            GameState.ACTIVE -> {
//                plugin.sendPluginMessage("selector:bedwars","UpdateState", "ACTIVE")
                gameStartingTask?.cancel()
                bossBar.progress = 1.0
                this.gameTickTask = GameTickTask(this)
                gameTickTask.runTaskTimer(plugin, 0, 20)
                bossBar.color = BarColor.BLUE
                NoFallPlayers.clear()

                for (pData in PlayerData.PLAYERS.values) {
                    if (!pData.isStartPowerSelected) {
                        val randomPower = startingPowers[Random().nextInt(startingPowers.size - 1)]
                        randomPower.players.add(pData)
                    }
                }

                startingPowers.forEach { it.enable() }
                Bukkit.getScheduler().runTaskLater(plugin, { -> startingPowers.forEach { it.disable() } }, 20L * 30L)

                for (player: Player in Bukkit.getOnlinePlayers()) {
                    val island: Island? = world.getIslandForPlayer(player)
                    player.saturation = 20f
                    player.health = 20.0
                    player.foodLevel = 20

                    if (island == null) {
                        val optionalIsland: Optional<Island> = world.islands.stream().filter {
                            return@filter it.players.size < world.maxTeamSize
                        }.findAny()

                        if (!optionalIsland.isPresent) {
                            player.kickPlayer("Not enough islands")
                            continue
                        }
                        optionalIsland.get().addMember(player)
                        scoreboard.findTeam(optionalIsland.get().color.formattedName()).get().addPlayer(player)
                    }

                    playerManager.setPlaying(player)
                }

                world.islands.forEach {
                    it.spawnShops()
                    it.players.forEach { player ->
                        it.players.forEach { other ->
                            if (player.uniqueId != other.uniqueId) {
                                player.collidableExemptions.add(other.uniqueId)
                            }
                        }
                    }
                }
                val border: WorldBorder = world.world.worldBorder
                border.setCenter(gameConfig.get().getDouble("center.x"), gameConfig.get().getDouble("center.z"))
                border.size = gameConfig.get().getDouble("border")
                updateScoreboard(true)
            }
            GameState.WON -> {
                updateScoreboard()
                Bukkit.getScheduler().runTaskLater(plugin, { ->
                    state = GameState.RESET
                }, 20L * 15L)
                this.gameTickTask.cancel()
                val finalIsland: Optional<Island> = if (world.getActiveIslands().size > 1) {
                    println("random winner")
                    world.getActiveIslands().stream().max(Comparator.comparingInt(Island::calculateStat))
                } else {
                    world.getActiveIslands().stream().findFirst()
                }

                if (!finalIsland.isPresent) {
                    Bukkit.broadcastMessage(Colorize.c("&fDRAW"))
                    Bukkit.getOnlinePlayers().forEach {
                        it.sendTitle("BUG FOUND!!!", "No draw possible", 0, 50, 50)
                    }
                } else {
                    val island: Island = finalIsland.get()
                    Bukkit.broadcastMessage("${island.color.getChatColor()}${StringUtils.center("", 40, "▬")}${island.color.getChatColor()}".colorize())
                    Bukkit.broadcastMessage("${island.color.getChatColor()}${StringUtils.center("", 40)}${island.color.getChatColor()}".colorize())
                    Bukkit.broadcastMessage(
                        "&8${
                            StringUtils.center(
                                "${island.color.getChatColor()}${island.color.formattedName()}&f has won!",
                                40
                            )
                        }&8".colorize()
                    )

                    val winners: java.lang.StringBuilder = java.lang.StringBuilder()
                    val playerIter: Iterator<Player> = island.players.iterator()
                    while (playerIter.hasNext()) {
                        val element = playerIter.next()
                        winners.append(element.name)
                        if (playerIter.hasNext()) {
                            winners.append(", ")
                        }
//                        plugin.sendPluginMessage("coins:in", element, "7wZk8c5J3mgvFgUbK", element.uniqueId, "silver", 50 + (island.calculateStat() * 10)
                    }

                    Bukkit.broadcastMessage(
                        "&8${
                            StringUtils.center(
                                "&7Winners: ${island.color.getChatColor()}$winners",
                                40,
                                " "
                            )
                        }&8".colorize()
                    )
                    Bukkit.broadcastMessage("${island.color.getChatColor()}${StringUtils.center("", 40,
                        " ")}${island.color.getChatColor()}".colorize())
                    Bukkit.broadcastMessage("${island.color.getChatColor()}${StringUtils.center("", 40, "▬")}${island.color.getChatColor()}".colorize())
                    Bukkit.getOnlinePlayers().forEach {
                        it.sendMessage("${island.color.getChatColor()}${StringUtils.center("Round summary", 33, "▬")}${island.color.getChatColor()}".colorize())
                        it.sendMessage("")
                        it.sendMessage("&7${StringUtils.center("+${(PlayerData.PLAYERS[it.uniqueId]!!.totalKills * 5) + (if(world.getIslandForPlayer(it) == island) 50 else 10)
                                + world.getIslandForPlayer(it)!!.calculateStat() * 10} silver", 40)}&7".colorize())
                        it.sendMessage("")
                        it.sendMessage("${island.color.getChatColor()}${StringUtils.center("", 40, "▬")}${island.color.getChatColor()}".colorize())
                        Main.managerBank.addValueToPlayer(it.name, (if(world.getIslandForPlayer(it) == island) 50 else 10) + (world.getIslandForPlayer(it)!!.calculateStat() * 10), "silver")
                    }
                }
                updateScoreboard()
            }
            GameState.RESET -> {
                Bukkit.getOnlinePlayers().forEach {
                    it.allowFlight = false
                    it.isFlying = false
                    it.activePotionEffects.clear()
                    val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
                    out.writeUTF("Connect")
                    out.writeUTF("hub${if(hubCount != 1) "-${kotlin.random.Random.nextInt(1, hubCount)}" else ""}")
                    it.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
                }

                Bukkit.getScheduler().runTaskLater(plugin, { ->
                    Bukkit.getOnlinePlayers().forEach {
                        it.kickPlayer("Server restarting")
                    }
                    world.resetWorld()
                    Bukkit.shutdown()
                }, 20)
            }
            else -> {
                println("\n\n###################")
                println("\n\nInvalid game state. If you see this, it is most likely a bug. Report on https://github.com/SashaSemenishchev/BedWars/issues\n\n")
                println("###################\n\n")
            }
        }
    }

    fun centerString(width: Int, s: String): String? {
        return String.format("%-" + width + "s", String.format("%" + (s.length + (width - s.length) / 2) + "s", s))
    }

    var currentEvent: GameEvent = GameEvent.RUBY_TWO
    set(value) {
        field = value

        secondsTimer = when(value){
            GameEvent.RUBY_TWO -> 5 * 60
            GameEvent.RUBY_THREE -> 5 * 60
            GameEvent.ALL_BEDS_DESTRUCTION -> 20 * 60
            GameEvent.GAME_END -> 15 * 60
        }
    }

    fun getStartPowerByItem(item: ItemStack): StartingPower? {
        for(power in startingPowers){
            if(power.item == item){
                return power
            }
        }
        return null
    }

    fun endGameIfNeeded() {
        if(state != GameState.ACTIVE) return

        if(world.getActiveIslands().size > 1){
            return
        }

        state = GameState.WON
    }

    fun forceEndGame(){
        if(state != GameState.ACTIVE) return

        state = GameState.WON
    }

//    fun getNPC(uuid: UUID): NPC?{
//        world.getActiveIslands().forEach {
//            for(loopUUID in it.leavedPlayers.keys){
//                if(loopUUID == uuid){
//                    return it.leavedPlayers[loopUUID]
//                }
//            }
//        }
//        return null
//    }

    fun updateScoreboard(){
        val lines = arrayListOf<String>()
        lines.add("")
        if(state == GameState.LOBBY || state == GameState.STARTING){
            lines.add("&fКарта: ${world.world.name.replace("_playing", "")}")
            lines.add("&fИгроков: &a${Bukkit.getOnlinePlayers().size}/${world.maxTeamSize * world.islands.size}")
        } else {
            val currentMinute = (gameTickTask.currentSecond % 3600) / 60
            val currentSecond = gameTickTask.currentSecond % 60
            lines.add("Time: ${if (currentMinute < 10) "0$currentMinute" else currentMinute}:${if (currentSecond < 10) "0$currentSecond" else currentSecond}")
            lines.add("")
            lines.add("Teams: ")
            for (island: Island in world.islands){
                if(island.players.size == 0) continue
                val builder: StringBuilder = StringBuilder()

                builder.append("  ").append(island.color.getChatColor()).append("&l")

                if(island.isBedPlaced()){
                    builder.append("🛡 &r${island.color.getChatColor()}${island.color.formattedName()}")
                } else {
                    if(island.alivePlayerCount() != 0){
                        builder.append("&l&8⚔ &r${island.color.getChatColor()}${island.color.formattedName()} &7(${island.alivePlayerCount()})")
                    }
                }

//                builder.append(" ${if (island.alivePlayerCount() == 0 && !island.isBedPlaced()) "" else "&r&${island.color.getChatColor().char}"}${island.color.formattedName()}")

                lines.add(builder.toString())
            }

            lines.add("")
            lines.add("Event:")
            val minutesToEvent = (secondsTimer % 3600) / 60
            val secondsToEvent = secondsTimer % 60
            lines.add(
                "  ${currentEvent.displayName}".replace(
                    "{time}",
                    "${if (minutesToEvent < 10) "0$minutesToEvent" else minutesToEvent}:${if (secondsToEvent < 10) "0$secondsToEvent" else secondsToEvent}"
                )
            )
        }
        lines.add("")
        lines.add("${ChatColor.YELLOW}server.millida.net")
        scoreboard.setLines(lines)
    }

    fun updateScoreboard(update: Boolean){
        val lines = arrayListOf<String>()
        lines.add("")
        if(state == GameState.LOBBY || state == GameState.STARTING){
            lines.add("&fКарта: ${world.world.name.replace("_playing", "")}")
            lines.add("&fИгроков: &a${Bukkit.getOnlinePlayers().size}/${world.maxTeamSize * world.islands.size}")
        } else {
            val currentMinute = (gameTickTask.currentSecond % 3600) / 60
            val currentSecond = gameTickTask.currentSecond % 60
            lines.add("Time: ${if (currentMinute < 10) "0$currentMinute" else currentMinute}:${if (currentSecond < 10) "0$currentSecond" else currentSecond}")
            lines.add("")
            lines.add("Teams: ")
            for (island: Island in world.islands){
                if(island.players.size == 0) continue
                val builder: StringBuilder = StringBuilder()

                builder.append("  ").append(island.color.getChatColor()).append("&l")

                if(island.isBedPlaced()){
                    builder.append("🛡 &r${island.color.getChatColor()}${island.color.formattedName()}")
                } else {
                    builder.append("&l&8⚔ &r${island.color.getChatColor()}${island.color.formattedName()}")
                    if(island.alivePlayerCount() != 0){
                        builder.append(" &7(${island.alivePlayerCount()})")
                    }
                }

//                builder.append(" ${if (island.alivePlayerCount() == 0 && !island.isBedPlaced()) "" else "&r&${island.color.getChatColor().char}"}${island.color.formattedName()}")

                lines.add(builder.toString())
            }

            lines.add("")
            lines.add("Event:")
            val minutesToEvent = (secondsTimer % 3600) / 60
            val secondsToEvent = secondsTimer % 60
            lines.add(
                "  ${currentEvent.displayName}".replace(
                    "{time}",
                    "${if (minutesToEvent < 10) "0$minutesToEvent" else minutesToEvent}:${if (secondsToEvent < 10) "0$secondsToEvent" else secondsToEvent}"
                )
            )
        }
        lines.add("")
        lines.add("${ChatColor.YELLOW}server.millida.net")
        scoreboard.setLines(update, lines)
    }

    fun isStarting(): Boolean{
        return gameStartingTask != null
    }

    companion object{
        fun getNearbyPlayers(location: Location, distance: Double): ArrayList<Player>{
            val result = arrayListOf<Player>()
            for(player in Bukkit.getOnlinePlayers()){
//                if(location.world.name != player.world.name)continue
                if(location.world!!.name == player.world.name && location.world == player.world && location.world == player.location.world){
                    if(location.distance(player.location) <= distance){
                        result.add(player)
                    }
                }
            }
            return result
        }
        var isLagged: Boolean = false
        var firstUpdate = true
    }
}