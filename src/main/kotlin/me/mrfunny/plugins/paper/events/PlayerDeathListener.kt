package me.mrfunny.plugins.paper.events

import me.mrfunny.plugins.paper.BedWars.Companion.colorize
import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.gamemanager.GameState
import me.mrfunny.plugins.paper.players.NoFallPlayers
import me.mrfunny.plugins.paper.tasks.PlayerRespawnTask
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.InventoryApi
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.TeleportUtil
import me.mrfunny.plugins.paper.worlds.Island
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.entity.Skeleton
import org.bukkit.entity.Snowball
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask

class PlayerDeathListener(private val gameManager: GameManager): Listener {

    @EventHandler
    fun onDamageByOther(event: EntityDamageByEntityEvent){
        if(event.damager !is Player || event.entity !is Player) return
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

        if(event.finalDamage >= PlayerData.PLAYERS[player.uniqueId]?.health!!){
            damager.sendMessage(Colorize.c("&aYou killed ${ChatColor.GOLD}${player.name}"))
            InventoryApi.giveAllResourcesFromPlayerToPlayer(player, damager)
            damagerIsland.totalSouls = damagerIsland.totalSouls + 1
            damager.sendMessage("${ChatColor.AQUA}+1 soul")
            damager.playSound(damager.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        }

        PlayerData.PLAYERS[damager.uniqueId]?.lastCombat = System.currentTimeMillis()
        PlayerData.PLAYERS[player.uniqueId]?.lastCombat = System.currentTimeMillis()

        PlayerData.PLAYERS[player.uniqueId]?.lastAttacker = damager

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

        if(NoFallPlayers.check(player) && event.cause == EntityDamageEvent.DamageCause.FALL){
            event.isCancelled = true
            NoFallPlayers.remove(player)
            return
        }

        if(player.inventory.itemInOffHand.type == Material.TOTEM_OF_UNDYING || player.inventory.itemInOffHand.type == Material.TOTEM_OF_UNDYING){
            return
        }

        PlayerData.PLAYERS[player.uniqueId]?.health = player.health

        if(event.finalDamage >= player.health){
            event.isCancelled = true
            if(PlayerData.PLAYERS[player.uniqueId] != null){
                if(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker != null){
                    if(PlayerData.PLAYERS[player.uniqueId] != null){
                        if(PlayerData.PLAYERS[player.uniqueId]?.lastCombat!! < 5000L){
                            val attackerIsland: Island? = gameManager.world.getIslandForPlayer(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)
                            PlayerData.PLAYERS[player.uniqueId]?.lastAttacker?.sendMessage("&aYou killed ${ChatColor.GOLD}${player.name}".colorize())
                            InventoryApi.giveAllResourcesFromPlayerToPlayer(player, PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)
                            attackerIsland?.totalSouls = attackerIsland?.totalSouls!! + 1
                            PlayerData.PLAYERS[player.uniqueId]?.lastAttacker?.sendMessage("${ChatColor.AQUA}+1 soul")
                        }
                    }
                }
            }
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            player.activePotionEffects.clear()
            gameManager.playerManager.setSpectatorMode(player)
            if(playerIsland.isBedPlaced()){
                val task: BukkitTask = Bukkit.getScheduler().runTaskTimer(gameManager.plugin, PlayerRespawnTask(player, gameManager.world.getIslandForPlayer(player)!!, gameManager), 0, 20)
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
    fun onProjectileShoot(event: ProjectileHitEvent){
        if(event.entity is Snowball){
            if(event.entity.shooter is Player){
                if(event.hitEntity != null && event.hitEntity is Player){
                    val shooter: Player = event.entity.shooter as Player
                    val hitPlayer: Player = event.hitEntity as Player
                    val shooterLocation: Location = shooter.location
                    val hitPlayerLocation: Location = hitPlayer.location
                    shooter.teleport(hitPlayerLocation)
                    hitPlayer.teleport(shooterLocation)
                    shooter.sendMessage("&aSwapped with ${hitPlayer.name}".colorize())
                }
            }

        }
    }
}