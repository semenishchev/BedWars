package me.mrfunny.bedwars.events

import me.mrfunny.bedwars.BedWars.Companion.colorize
import me.mrfunny.bedwars.BedWars.Companion.isProtected
import me.mrfunny.bedwars.game.GameManager
import me.mrfunny.bedwars.game.GameState
import me.mrfunny.bedwars.players.NoFallPlayers
import me.mrfunny.bedwars.players.PlayerData
import me.mrfunny.bedwars.tasks.PlayerRespawnTask
import me.mrfunny.bedwars.tasks.TeleportTask
import me.mrfunny.bedwars.util.InventoryApi
import me.mrfunny.bedwars.util.ItemBuilder
import me.mrfunny.bedwars.worlds.islands.Island
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.random.Random


class PlayerDeathListener(private val gameManager: GameManager): Listener {

    private val projectileToBukkitTaskMap = hashMapOf<Projectile, BukkitTask>()
    private val headshotModifier = 1.479
    @EventHandler
    fun onDamageByOther(event: EntityDamageByEntityEvent){
        if(event.entity is Monster && !event.entity.hasMetadata("protected")){
            event.entity.remove()
            (event.entity as Monster).damage(100000.0)
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

            handleDamage(player, damager, event)

            if (player.isGliding && damager.inventory.itemInMainHand.type == Material.CROSSBOW){
                event.damage = event.damage + 2.0
            } else {
                event.damage /= 2.6
            }

            if(event.damager is Arrow){
                damager.sendMessage("${ChatColor.RED}${player.name}${ChatColor.GRAY} is on ${ChatColor.RED}${roundOffDecimal(player.health)}")
            }
            return
        }
        if(event.damager is Player && event.entity is Player){
            val player: Player = event.entity as Player
            val damager = event.damager as Player

            handleDamage(player, damager, event)
        }
    }

    private fun handleDamage(
        player: Player,
        damager: Player,
        event: EntityDamageByEntityEvent
    ) {
        val playerData: PlayerData = PlayerData.get(player)
        val damagerData: PlayerData = PlayerData.get(damager)

        if ((System.currentTimeMillis() - playerData.lastRespawn) <= 10000L) {
            event.isCancelled = true
        }

        if ((System.currentTimeMillis() - damagerData.lastRespawn) <= 10000L) {
            damagerData.lastRespawn = 0
        }

        val damagerIsland: Island = gameManager.world.getIslandForPlayer(damager) ?: return

        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
            player.sendMessage("${ChatColor.RED}You took damage and lost your invisibility")
        }

        if (damagerIsland.players.contains(player)) {
            event.isCancelled = true
            return
        }

        if (player.isBlocking && ((System.currentTimeMillis() - playerData.lastShieldUse) <= 2000L)) {
            player.clearActiveItem()
            player.setCooldown(Material.SHIELD, 20 * 7)
            handleShield(player)
        } else if (player.isBlocking && damager.inventory.itemInMainHand.type.name.contains("AXE")) {
            handleShield(player)
            playerData.lastShieldUse = System.currentTimeMillis()
        } else {
            playerData.lastShieldUse = System.currentTimeMillis()
        }

        damagerData.lastCombat = System.currentTimeMillis()
        playerData.lastCombat = System.currentTimeMillis()

        playerData.lastAttacker = damager.uniqueId
    }

    private fun handleShield(player: Player) {
        val shield: ItemStack =
            if (player.inventory.itemInMainHand.type == Material.SHIELD) player.inventory.itemInMainHand else player.inventory.itemInOffHand
        val shieldMeta = shield.itemMeta as Damageable
        shieldMeta.damage = shieldMeta.damage + 15
        if (shieldMeta.damage >= 336) {
            player.inventory.remove(shield)
        } else {
            shield.setItemMeta(shieldMeta)
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
            if(event.entity.isProtected()){
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

        if(event.finalDamage >= player.health){
            if((player.inventory.itemInOffHand.type == Material.TOTEM_OF_UNDYING || player.inventory.itemInMainHand.type == Material.TOTEM_OF_UNDYING) && event.cause != EntityDamageEvent.DamageCause.VOID){
                player.health = 20.0
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 5, 2 * 20, false, true))
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 1, false, true))
                if(player.inventory.itemInOffHand.type == Material.TOTEM_OF_UNDYING){
                    player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                } else {
                    player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                }
                player.playEffect(EntityEffect.TOTEM_RESURRECT)
                player.world.playSound(event.entity.location, Sound.ITEM_TOTEM_USE,1f, 1f)
                object : BukkitRunnable() {
                    var counter = 0
                    override fun run() {
                        counter++
                        if(counter >= 60){
                            cancel()
                        }
                        event.entity.world.spawnParticle(Particle.TOTEM, event.entity.location.clone().add(.0, .5, .0), 20)
                    }
                }.runTaskTimer(gameManager.plugin, 0, 1)
                return
            }
            gameManager.endGameIfNeeded()
            gameManager.deadPlayers.add(player.uniqueId)
            event.isCancelled = true
            player.closeInventory()
            player.activePotionEffects.forEach {
                player.removePotionEffect(it.type)
            }
            val data = PlayerData.get(player)
            if((System.currentTimeMillis() - data.lastCombat) <= 10000L){
                data.lastAttacker?.let {lastAttacker ->
                    val attackerData = PlayerData.get(lastAttacker) ?: return
                    val attackerIsland: Island = attackerData.assignedIsland
                    val attacker = attackerData.getPlayer()
                    attacker?.let {
                        it.sendMessage("&aYou killed ${ChatColor.GOLD}${player.name}".colorize())
                        InventoryApi.giveAllResourcesFromPlayerToPlayer(
                            player,
                            it
                        )
                        it.sendMessage("${ChatColor.AQUA}+1 soul")
                        it.playSound(it.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                        gameManager.storage.addKill(it.uniqueId)
                        Bukkit.broadcast(player.name().color(playerIsland.color.toNamed())
                            .append(Component.text(" was killed by ", NamedTextColor.GRAY))
                            .append(it.name().color(attackerIsland.color.toNamed())))
                    }

                    attackerIsland.totalSouls += 1
                    attackerData.totalKills += 1
                    attackerData.addCoinsAndSave(5, "kill")

                }
//                val lastAttacker: UUID = data.lastAttacker!!

            } else {

                Bukkit.broadcast(player.name().color(playerIsland.color.toNamed())
                    .append(Component.text(" died", NamedTextColor.GRAY)))
            }
            data.totalDeaths += 1
            gameManager.storage.addDeath(player.uniqueId)
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            player.activePotionEffects.clear()
            projectileToBukkitTaskMap[event.entity]?.cancel()
            gameManager.playerManager.setSpectatorMode(player)
            if(playerIsland.isBedPlaced()){
                val task: BukkitTask = Bukkit.getScheduler().runTaskTimer(
                    gameManager.plugin, PlayerRespawnTask(
                        player, gameManager.world.getIslandForPlayer(
                            player
                        )!!
                    ), 0, 20
                )
                Bukkit.getScheduler().runTaskLater(gameManager.plugin, task::cancel, 20L * 6L)
            } else {
                player.sendTitle("${ChatColor.RED}YOU DIED", null, 0, 20, 20)

                if(!gameManager.world.getActiveIslands().contains(playerIsland)){

                    Bukkit.broadcast(Component.empty()
                        .append(Component.text("TEAM DESTRUCTION! ", NamedTextColor.WHITE)
                            .decorate(TextDecoration.BOLD))
                        .append(Component.text(playerIsland.color.formattedName() + " team ", playerIsland.color.toNamed()))
                        .append(Component.text("was destroyed", NamedTextColor.GRAY)))
                }

            }
        }
    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent){
        val entity = event.entity
        if (entity.shooter !is Player) return

        projectileToBukkitTaskMap[entity]?.let {
            it.cancel()
            projectileToBukkitTaskMap.remove(entity)
        }
    }

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent){
        if(event.entity !is Snowball) return
        val entity = event.entity as Snowball
        if (event.entity.shooter !is Player) return

        val player: Player = entity.shooter as Player
        val island: Island = gameManager.world.getIslandOf(player)
        val woolMaterial: Material = island.color.concreteMaterial()
        projectileToBukkitTaskMap[event.entity] = object : BukkitRunnable() {
            var timer = 0
            override fun run() {

                if(timer >= 200 || entity.isOnGround || entity.isDead){
                    cancel()
                    return
                }
                val targetBlock: Block = event.entity.world.getBlockAt(event.entity.location.clone().add(Random.nextInt(-1, 1).toDouble(), -1.5, Random.nextInt(-1, 1).toDouble()))
                targetBlock.setMetadata("placed", FixedMetadataValue(gameManager.plugin, "block"))
                if(!(targetBlock.type.name.contains("BED") || gameManager.world.isBlockInProtectedZone(targetBlock) || (!targetBlock.hasMetadata("placed") || targetBlock.type != Material.AIR))){
                    targetBlock.type = woolMaterial
                    event.entity.world.getBlockAt(event.entity.location.clone().clone().add(.0, -1.5, .0)).type = woolMaterial
                }
                player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 1f, 1f)
                timer++
            }

        }.runTaskTimer(gameManager.plugin, 3L, 1L)
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


    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }

    private fun playerMoved(from: Vector, to: Vector?): Boolean {
        return roundOffDecimal(from.x) == roundOffDecimal(to!!.x) && roundOffDecimal(from.y) == roundOffDecimal(to.y) && roundOffDecimal(from.z) == roundOffDecimal(to.z)
    }
}