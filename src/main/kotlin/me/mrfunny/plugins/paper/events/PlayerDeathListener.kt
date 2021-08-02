package me.mrfunny.plugins.paper.events

import com.ruverq.rubynex.economics.Main
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
import me.mrfunny.api.NPC
import me.mrfunny.api.PlayerApi
import me.mrfunny.plugins.paper.tasks.TeleportTask
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.inventory.meta.Damageable
import org.bukkit.potion.PotionEffect
import org.bukkit.util.Vector
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.random.Random


class PlayerDeathListener(private val gameManager: GameManager): Listener {

    private val projectileToBukkitTaskMap = hashMapOf<Projectile, BukkitTask>()

    @EventHandler
    fun onDamageByOther(event: EntityDamageByEntityEvent){
        if(event.entity is Monster && !event.entity.hasMetadata("protected")){
            event.entity.remove()
            (event.entity as Monster).damage(100000.0)
        }
        if(gameManager.getNPC(event.entity.uniqueId) != null){
            val npc: NPC = gameManager.getNPC(event.entity.uniqueId)!!
            npc.isTouched = true
            npc.despawn()
            event.damager.sendMessage("")
            return
        }

        if(event.damager is Arrow || event.damager is SpectralArrow || event.damager is Fireball){

            val damagerEntity: Entity = (event.damager as Projectile).shooter as Entity
            if(damagerEntity !is Player) return
            val damager: Player = damagerEntity
            if(event.entity !is Player && event.entity !is Monster){
                event.isCancelled = true
                return
            }
            val player: Player = event.entity as Player
            val playerData: PlayerData = PlayerData.PLAYERS[player.uniqueId]!!
            val damagerData: PlayerData = PlayerData.PLAYERS[damager.uniqueId]!!
            if((System.currentTimeMillis() - playerData.lastRespawn) <= 10000L){
                event.isCancelled = true
            }
            if((event.damager as Projectile).shooter !is Player) return
            val damagerIsland: Island = gameManager.world.getIslandForPlayer(damager) ?: return

            if(PlayerApi.isHeadShot(player, event.damager as Projectile)){
                event.damage * 1.479
            }

            if(damagerIsland.players.contains(player)){
                event.isCancelled = true
                return
            }

            if (player.isGliding && damager.inventory.itemInMainHand.type == Material.CROSSBOW){
                event.damage = event.damage + 2.0
            } else {
                event.damage /= 2.6
            }

            if(event.damager is Arrow){
                damager.sendMessage("${ChatColor.RED}${player.name}${ChatColor.GRAY} is on ${ChatColor.RED}${roundOffDecimal(player.health)}")
            }

            damagerData.lastCombat = System.currentTimeMillis()
            playerData.lastCombat = System.currentTimeMillis()

            playerData.lastAttacker = damager.uniqueId
            return
        }
        if(event.damager is Player && event.entity is Player){
            val player: Player = event.entity as Player
            val damager = event.damager as Player

            val playerData: PlayerData = PlayerData.PLAYERS[player.uniqueId]!!
            val damagerData: PlayerData = PlayerData.PLAYERS[damager.uniqueId]!!

            if((System.currentTimeMillis() - playerData.lastRespawn) <= 10000L){
                event.isCancelled = true
            }

            if((System.currentTimeMillis() - damagerData.lastRespawn) <= 10000L){
                damagerData.lastRespawn = 0
            }

            val damagerIsland: Island = gameManager.world.getIslandForPlayer(damager) ?: return

            if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                player.removePotionEffect(PotionEffectType.INVISIBILITY)
                player.sendMessage("${ChatColor.RED}You took damage and lost your invisibility")
            }

            if(damagerIsland.players.contains(player)){
                event.isCancelled = true
                return
            }

            if(player.isBlocking && ((System.currentTimeMillis() - playerData.lastShieldUse) <= 2000L)){
                player.clearActiveItem()
                player.setCooldown(Material.SHIELD, 20 * 7)
                val shield: ItemStack = if(player.inventory.itemInMainHand.type == Material.SHIELD) player.inventory.itemInMainHand else player.inventory.itemInOffHand
                val shieldMeta: ItemMeta = shield.itemMeta
                (shieldMeta as Damageable).damage = (shieldMeta as Damageable).damage + 15
                if((shieldMeta as Damageable).damage >= 336){
                    player.inventory.remove(shield)
                } else {
                    shield.setItemMeta(shieldMeta)
                }
            } else if(player.isBlocking && damager.inventory.itemInMainHand.type.name.contains("AXE")){
                val shield: ItemStack = if(player.inventory.itemInMainHand.type == Material.SHIELD) player.inventory.itemInMainHand else player.inventory.itemInOffHand
                val shieldMeta: ItemMeta = shield.itemMeta
                (shieldMeta as Damageable).damage = (shieldMeta as Damageable).damage + 15
                if((shieldMeta as Damageable).damage >= 336){
                    player.inventory.remove(shield)
                } else {
                    shield.setItemMeta(shieldMeta)
                }
                playerData.lastShieldUse = System.currentTimeMillis()
            } else {
                playerData.lastShieldUse = System.currentTimeMillis()
            }

            damagerData.lastCombat = System.currentTimeMillis()
            playerData.lastCombat = System.currentTimeMillis()

            playerData.lastAttacker = damager.uniqueId
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent){
        if(gameManager.state != GameState.ACTIVE) {
            event.isCancelled = true
            return
        } else if(gameManager.state == GameState.ACTIVE && event.cause == EntityDamageEvent.DamageCause.VOID && event.entity.location.y < 10){
            event.damage = 1000.0
        }

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

        if(event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
            event.damage /= 4
        } else if(event.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION){
            event.damage /= 2
        }

        if(NoFallPlayers.check(player) && event.cause == EntityDamageEvent.DamageCause.FALL){
            event.isCancelled = true
            NoFallPlayers.remove(player)
            return
        }

        if((player.inventory.itemInOffHand.type == Material.TOTEM_OF_UNDYING || player.inventory.itemInMainHand.type == Material.TOTEM_OF_UNDYING) && event.cause != EntityDamageEvent.DamageCause.VOID){
            player.health = 20.0;
            player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 5, 2, false, true))
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 1, false, true))
            return
        }

        if(event.finalDamage >= player.health){
            gameManager.endGameIfNeeded()
            event.isCancelled = true
            player.closeInventory()
            player.activePotionEffects.forEach {
                player.removePotionEffect(it.type)
            }
            if((System.currentTimeMillis() - PlayerData.PLAYERS[player.uniqueId]!!.lastCombat) <= 10000L){
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
//                gameManager.plugin.sendPluginMessage("coins:in", player, "7wZk8c5J3mgvFgUbK", player.uniqueId, "silver", 5)
                try{
                    Main.managerBank.addValueToPlayer(Bukkit.getPlayer(lastAttacker)!!.name, 5, "silver")
                    Bukkit.getPlayer(PlayerData.PLAYERS[player.uniqueId]?.lastAttacker!!)!!.sendMessage("${ChatColor.GRAY}+5 silver (Kill)")
                } catch (ex: Exception){
                    ex.printStackTrace()
                }

                Bukkit.broadcastMessage("${playerIsland.color.getChatColor()}${player.name}&f was killed by ${attackerIsland.color.getChatColor()}${Bukkit.getPlayer(lastAttacker)!!.name}".colorize())
            } else {
                Bukkit.broadcastMessage("${playerIsland.color.getChatColor()}${player.name}&f dead".colorize())
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
                        )!!
                    ), 0, 20
                )
                Bukkit.getScheduler().runTaskLater(gameManager.plugin, task::cancel, 20 * 6)
            } else {
                player.sendTitle(Colorize.c("&cYOU DIED"), null, 0, 20, 20)

                if(!gameManager.world.getActiveIslands().contains(playerIsland)){
                    Bukkit.broadcastMessage("TEAM DESTRUCTION> ${playerIsland.color.formattedName()}&f is destroyed".colorize())
                }

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
                val woolMaterial: Material = island?.color?.concreteMaterial() ?: Material.WHITE_CONCRETE
                projectileToBukkitTaskMap[event.entity] = object : BukkitRunnable() {
                    override fun run() {
                        val targetBlock: Block = event.entity.world.getBlockAt(event.entity.location.clone().add(Random.nextInt(-1, 1).toDouble(), -1.5, Random.nextInt(-1, 1).toDouble()))
                        targetBlock.setMetadata("placed", FixedMetadataValue(gameManager.plugin, "block"))
                        if(!(targetBlock.type.name.contains("BED") || gameManager.world.isBlockInProtectedZone(targetBlock) || (!targetBlock.hasMetadata("placed") || targetBlock.type != Material.AIR))){
                            targetBlock.type = woolMaterial
                            event.entity.world.getBlockAt(event.entity.location.clone().clone().add(.0, -1.5, .0)).type = woolMaterial
                        }
                        player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 1f, 1f)

                    }

                }.runTaskTimer(gameManager.plugin, 3L, 1L)
            }
        }
    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent){
        if(event.entity !is Projectile) return
        if(projectileToBukkitTaskMap.containsKey(event.entity as Projectile)){
            projectileToBukkitTaskMap.remove(event.entity as Projectile)
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent){
        if(gameManager.state != GameState.ACTIVE && event.to.y < 10.0){
            event.player.teleport(gameManager.world.lobbyPosition)
            return
        }

        if (TeleportTask.teleporting.contains(event.player) && playerMoved(event.from.toVector(), event.to.toVector())){
            TeleportTask.teleporting[event.player]!!.cancel()
            event.player.sendMessage("&cTeleportation cancelled".colorize())
            TeleportTask.teleporting.remove(event.player)
            event.player.inventory.addItem(ItemBuilder(Material.GUNPOWDER).setName("&aHome teleporter").toItemStack())
        }
    }


    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }

    fun playerMoved(from: Vector, to: Vector?): Boolean {
        return from.distance(to!!) > 0
    }
}