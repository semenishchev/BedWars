package me.mrfunny.bedwars.game

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import dev.jcsoftware.jscoreboards.JScoreboard
import dev.jcsoftware.jscoreboards.JScoreboardOptions
import dev.jcsoftware.jscoreboards.JScoreboardTabHealthStyle
import me.mrfunny.api.CustomConfiguration
import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.BedWars.Companion.log
import me.mrfunny.bedwars.config.ConfigurationManager
import me.mrfunny.bedwars.gameutils.StartingPower
import me.mrfunny.bedwars.gui.GUIManager
import me.mrfunny.bedwars.messages.MessagesManager
import me.mrfunny.bedwars.players.NoFallPlayers
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.players.PlayerManager
import me.mrfunny.bedwars.setup.SetupWizardManager
import me.mrfunny.bedwars.storage.BedwarsStorage
import me.mrfunny.bedwars.storage.MongoDbStorage
import me.mrfunny.bedwars.tasks.GameStartingTask
import me.mrfunny.bedwars.tasks.GameTickTask
import me.mrfunny.bedwars.util.Colorize
import me.mrfunny.bedwars.util.Cooldowns
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.util.StringProcessing
import me.mrfunny.bedwars.worlds.GameWorld
import me.mrfunny.bedwars.worlds.islands.Island
import me.mrfunny.bedwars.worlds.islands.IslandColor
import me.mrfunny.bedwars.worlds.islands.teamupgrades.ArmourUpgrade
import me.mrfunny.bedwars.worlds.islands.teamupgrades.GeneratorUpgrade
import me.mrfunny.bedwars.worlds.islands.teamupgrades.GpsUpgrade
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.apache.commons.lang.StringUtils
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.security.SecureRandom
import java.util.*
import java.util.function.Predicate

class GameManager(val plugin: me.mrfunny.bedwars.BedWars, version: Double) {

    val scoreboard: JScoreboard = JScoreboard(JScoreboardOptions("&c&lBEDWARS".colorize(), JScoreboardTabHealthStyle.NONE, true))
    val setupWizardManager: SetupWizardManager = SetupWizardManager
    val configurationManager: ConfigurationManager = ConfigurationManager(this)
    val guiManager: GUIManager = GUIManager
    val gameConfig = CustomConfiguration("gameconfig", plugin)
    var isBetatest = false

    val englishMessages = CustomConfiguration("messages_en", plugin)
    val russianMessages = CustomConfiguration("messages_ru", plugin)
    val itemsLocalization = CustomConfiguration("items", plugin)
    private val hubCount = 1

    val random = SecureRandom()
    val storage: BedwarsStorage = MongoDbStorage(plugin)

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
            it.getPlayer()?.addPotionEffect(
                PotionEffect(PotionEffectType.SPEED, 60 * 20, 1)
            )
        }, {}),
        StartingPower(
            "generator",
            ItemBuilder(Material.IRON_INGOT).setName("&aGenerator upgrade").toItemStack(),
            { it.isGeneratorMultiplier = true },
            { it.isGeneratorMultiplier = false }),
        StartingPower("strength", ItemBuilder(Material.DIAMOND_SWORD).setName("&aStrength I").toItemStack(), {
            it.getPlayer()?.addPotionEffect(
                PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90 * 20, 0)
            )
        }, {}),
    )
    val upgrades = listOf(
        ArmourUpgrade(),
        GeneratorUpgrade(),
        GpsUpgrade()
    )

    fun getIslandByColor(color: IslandColor): Island {
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
                gameStartingTask?.cancel()
                bossBar.progress = 1.0
                this.gameTickTask = GameTickTask(this)
                gameTickTask.runTaskTimer(plugin, 0, 20)
                bossBar.color = BarColor.BLUE
                NoFallPlayers.clear()

                for (data in PlayerData.all()) {
                    if (!data.isStartPowerSelected) {
                        val randomPower = startingPowers[Random().nextInt(startingPowers.size - 1)]
                        randomPower.players.add(data)
                    }
                }

                for (it in startingPowers) {
                    it.enable()
                }
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

                for (it in world.islands) {
                    it.spawnShops()
                    for (player in it.players) {
                        for (other in it.players) {
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
                    world.getActiveIslands().stream().max(Comparator.comparingInt(Island::calculateStat))
                } else {
                    world.getActiveIslands().stream().findAny()
                }

                if (!finalIsland.isPresent) {
                    Bukkit.broadcastMessage(Colorize.c("&fDRAW"))
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.sendTitle("BUG FOUND!!!", "No draw possible", 0, 50, 50)
                    }
                    return
                }
                val width: Int = 30
                val padStr = "▬"
                val winnerIsland: Island = finalIsland.get()
                val winnerText = winnerIsland.color.formattedName()
                val wins = " wins!"
                val padCount = StringProcessing.calculatePadsForCenter(winnerText + wins, width)
                val pads = padStr.repeat(padCount)
                val winnerColor = winnerIsland.color.toNamed()

                val winners = StringBuilder()
                val playerIter = winnerIsland.players.iterator()
                while (playerIter.hasNext()) {
                    val element = playerIter.next()
                    winners.append(element.name)
                    if (playerIter.hasNext()) {
                        winners.append(", ")
                    }
                }

                Bukkit.broadcast(Component.text(padStr.repeat(width) + "\n\n", winnerColor)
                    .append(Component.text(pads + winnerText))
                    .append(Component.text(wins + pads + "\n", NamedTextColor.WHITE))
                    .append(Component.text("Winners: ", NamedTextColor.WHITE))
                    .append(Component.text(winners.toString() + "\n\n" + padStr.repeat(width))))

                for (data in PlayerData.values()) {

                    data.getPlayer()?.let { player ->
//                        val coins = (if(world.getIslandForPlayer(player) == island) 50 else 10) + (world.getIslandForPlayer(player)!!.calculateStat() * 10)

                        val island = world.getIslandOf(data)
                        var coins = 10 + (island.calculateStat() * 10)
                        if(winnerIsland == island) {
                            coins += 40
                            storage.addWinAndCoins(player.uniqueId, coins)
                        } else {
                            storage.incrementCoins(player.uniqueId, coins)
                        }
                        player.sendMessage(Component.text(StringUtils.center("Round Summary", width, padStr) + "\n\n", NamedTextColor.DARK_AQUA)
                            .append(Component.text(StringUtils.center("+${coins + data.totalCoins} silver", width) + "\n\n", NamedTextColor.GRAY))
                            .append(Component.text(padStr.repeat(width))))
                    }
                }
                updateScoreboard()
            }
            GameState.RESET -> {
                for (it in Bukkit.getOnlinePlayers()) {
                    it.allowFlight = false
                    it.isFlying = false
                    it.activePotionEffects.clear()
                    val out: ByteArrayDataOutput = ByteStreams.newDataOutput()
                    out.writeUTF("Connect")
                    out.writeUTF("hub")
                    it.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
                }

                Bukkit.getScheduler().runTaskLater(plugin, { ->
                    for (it in Bukkit.getOnlinePlayers()) {
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

    fun updateScoreboard(){
        updateScoreboard(false)
    }

    fun updateScoreboard(update: Boolean){
        val lines = arrayListOf<String>()
        lines.add("")
        if(state == GameState.LOBBY || state == GameState.STARTING){
            lines.add("&fMap: ${world.world.name.replace("_playing", "")}")
            lines.add("&Players count: &a${Bukkit.getOnlinePlayers().size}/${world.maxTeamSize * world.islands.size}")
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
        lines.add("${ChatColor.YELLOW}grash where 15k")
        scoreboard.setLines(update, lines)
    }

    fun isStarting(): Boolean{
        return gameStartingTask != null
    }

    companion object{
        fun getNearbyPlayers(location: Location, distance: Double, filter: Predicate<Player>): ArrayList<PlayerData>{
            val result = arrayListOf<PlayerData>()
            for(data in PlayerData.values()){
                val player = data.getPlayer()!!
                if(location.world != player.world) continue
                if(location.distance(player.location) > distance) continue
                if(!filter.test(player)) continue
                result.add(data)
            }
            return result
        }
        var isLagged: Boolean = false
    }
}