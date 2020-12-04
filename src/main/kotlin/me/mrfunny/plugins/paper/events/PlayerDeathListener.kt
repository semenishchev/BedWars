package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.players.NoFallPlayers
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.tasks.PlayerRespawnTask
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.InventoryApi
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.random.Random


class PlayerDeathListener(private val gameManager: GameManager): Listener {

    private val projectileToBukkitTaskMap = hashMapOf<Projectile, BukkitTask>()

    @EventHandler
    fun onDamageByOther(event: EntityDamageByEntityEvent){
        if(event.damager !is Player || event.entity !is Player) return
        if(event.damager is Arrow || event.damager is SpectralArrow || event.damager is Fireball){
            println("part 1")
            if((event.damager as Projectile).shooter !is Player) return
            val damager: Player = (event.damager as Projectile).shooter as Player
            val player: Player = event.entity as Player
            val damagerIsland: Island = gameManager.world.getIslandForPlayer(damager) ?: return

            if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                player.removePotionEffect(PotionEffectType.INVISIBILITY)
                player.sendMessage("${ChatColor.RED}You took damage and lost your invisibility")
            }
            if(damagerIsland.players.contains(player)){
                event.isCancelled = true
                return
            }

            PlayerData.PLAYERS[damager.uniqueId]?.lastCombat = System.currentTimeMillis()
            PlayerData.PLAYERS[player.uniqueId]?.lastCombat = System.currentTimeMillis()

            PlayerData.PLAYERS[player.uniqueId]?.lastAttacker = damager.uniqueId
            return
        }
        val damager: Player = event.damager as Player
        val player: Player = event.entity as Player
        val damagerIsland: Island = gameManager.world.getIslandForPlayer(damager) ?: return

        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
            player.sendMessage("${ChatColor.RED}You took damage and lost your invisibility")
        }
        if(damagerIsland.players.contains(player)){
            event.isCancelled = true
            return
        }

        PlayerData.PLAYERS[damager.uniqueId]!!.lastCombat = System.currentTimeMillis()
        PlayerData.PLAYERS[player.uniqueId]!!.lastCombat = System.currentTimeMillis()

        PlayerData.PLAYERS[player.uniqueId]!!.lastAttacker = damager.uniqueId

    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent){

        if(gameManager.state != GameState.ACTIVE) { event.isCancelled = true; return;}

        if(event.entity !is Player) {
            if(event.entity is Villager || event.entity is Skeleton){
                event.isCancelled = true
            }
            return
        }

        val player: Player = event.entity as Player
        val playerIsland: Island? = gameManager.world.getIslandForPlayer(player)
        if(playerIsland == null || player.gameMode != GameMode.SURVIVAL){
            event.isCancelled = true
            return
        }

        PlayerData.PLAYERS[player.uniqueId]?.health = player.health

        if(NoFallPlayers.check(player) && event.cause == EntityDamageEvent.DamageCause.FALL){
            event.isCancelled = true
            NoFallPlayers.remove(player)
            return
        }

        if(player.inventory.itemInOffHand.type == Material.TOTEM_OF_UNDYING || player.inventory.itemInMainHand.type == Material.TOTEM_OF_UNDYING){
            return
        }

        println(System.currentTimeMillis() - PlayerData.PLAYERS[player.uniqueId]!!.lastCombat)

        if(event.finalDamage >= player.health){
            event.isCancelled = true
            if((System.currentTimeMillis() - PlayerData.PLAYERS[player.uniqueId]!!.lastCombat) < 100000L){
                println("passed combat time")
                if(PlayerData.PLAYERS[player.uniqueId]!!.lastAttacker == null){ return }
                val lastAttacker: UUID = PlayerData.PLAYERS[player.uniqueId]!!.lastAttacker!!
                val attackerIsland: Island? = gameManager.world.getIslandForPlayer(Bukkit.getPlayer(lastAttacker)!!)
                Bukkit.getPlayer(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)!!.sendMessage("&aYou killed ${ChatColor.GOLD}${player.name}".colorize())
                InventoryApi.giveAllResourcesFromPlayerToPlayer(
                    player,
                    Bukkit.getPlayer(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)!!
                )
                attackerIsland?.totalSouls = attackerIsland?.totalSouls!! + 1
                Bukkit.getPlayer(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)!!.sendMessage("${ChatColor.AQUA}+1 soul")
                Bukkit.getPlayer(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)!!.playSound(Bukkit.getPlayer(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)!!.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                PlayerData.PLAYERS[PlayerData.PLAYERS[player.uniqueId]!!.lastAttacker]?.totalKills = PlayerData.PLAYERS[PlayerData.PLAYERS[player.uniqueId]!!.lastAttacker]?.totalKills!! + 1
            }
            PlayerData.PLAYERS[player.uniqueId]?.totalDeaths = PlayerData.PLAYERS[player.uniqueId]?.totalDeaths!! + 1
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            player.activePotionEffects.clear()
            gameManager.playerManager.setSpectatorMode(player)
            if(playerIsland.isBedPlaced()){
                val task: BukkitTask = Bukkit.getScheduler().runTaskTimer(
                    gameManager.plugin, PlayerRespawnTask(
                        player, gameManager.world.getIslandForPlayer(
                            player
                        )!!, gameManager
                    ), 0, 20
                )
                Bukkit.getScheduler().runTaskLater(gameManager.plugin, task::cancel, 20 * 6)
            } else {
                player.sendTitle(Colorize.c("&cYOU DIED"), null, 0, 20, 20)

                if(!gameManager.world.getActiveIslands().contains(playerIsland)){
                    Bukkit.broadcastMessage("TEAM DESTRUCTION> ${playerIsland.color.formattedName()}&f is destroyed".colorize())
                }

                player.world.spigot().strikeLightningEffect(player.location, false)

                gameManager.endGameIfNeeded()
            }
            player.teleport(gameManager.world.lobbyPosition)
        }
    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent){
        if (event.entity.shooter is Player) {
            val task: BukkitTask? = projectileToBukkitTaskMap[event.entity]
            if (task != null) {
                task.cancel()
                projectileToBukkitTaskMap.remove(event.entity)
            }
        }
    }

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent){
        if(event.entity is Snowball){
            if (event.entity.shooter is Player) {
                val player: Player = event.entity.shooter as Player
                val island: Island? = gameManager.world.getIslandForPlayer(player)
                val woolMaterial: Material = island?.color?.woolMaterial() ?: Material.WHITE_WOOL
                projectileToBukkitTaskMap[event.entity] = object : BukkitRunnable() {
                    override fun run() {
                        val targetBlock: Block = event.entity.world.getBlockAt(event.entity.location.add(Random.nextInt(-1, 1).toDouble(), -1.0, Random.nextInt(-1, 1).toDouble()))
                        targetBlock.setMetadata("placed", FixedMetadataValue(gameManager.plugin, "block"))
                        if(!(targetBlock.type.name.contains("BED") || gameManager.world.isBlockInProtectedZone(targetBlock) || (!targetBlock.hasMetadata("placed") || targetBlock.type != Material.AIR))){
                            targetBlock.type = woolMaterial
                        }
                    }
                }.runTaskTimer(gameManager.plugin, 2L, 1L)
            }
        }
    }

    @EventHandler
    fun onHeal(event: EntityRegainHealthEvent){
        if(event.entity !is Player) return
        PlayerData.PLAYERS[event.entity.uniqueId]?.health = (event.entity as Player).health
    }
}