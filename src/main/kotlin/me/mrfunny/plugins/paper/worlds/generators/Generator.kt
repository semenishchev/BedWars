package me.mrfunny.plugins.paper.worlds.generators

import me.mrfunny.plugins.paper.gamemanager.GameManager
import me.mrfunny.plugins.paper.players.PlayerData
import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class Generator(var location: Location, var type: GeneratorType, val isIslandGenerator: Boolean) {

    private var armorStand: ArmorStand? = null
    var currentTier: GeneratorTier = GeneratorTier.ONE

    var activated: Boolean = false
    set(value) {
        if(type == GeneratorType.DIAMOND) return
        if(value == field) return

        field = value

        if(isIslandGenerator) return

        armorStand = location.world!!.spawn(location.add(0.0, 1.0, 0.0), ArmorStand::class.java)

        if(!value && armorStand != null){
            armorStand?.remove()
            return
        }

        armorStand?.setGravity(false)
        armorStand?.isVisible = false
        armorStand?.isInvulnerable = true
        armorStand?.isCustomNameVisible = true
        armorStand?.customName = Colorize.c(getArmorstandName())
    }

    private var secondsSinceActivation: Int = 0

    fun getItemStack(): Material {
        return when (type) {
            GeneratorType.IRON -> {
                GeneratorType.IRON.getMaterial()
            }
            GeneratorType.GOLD -> {
                GeneratorType.GOLD.getMaterial()
            }
            GeneratorType.DIAMOND -> {
                Material.DIAMOND
            }
            GeneratorType.EMERALD -> {
                Material.FERMENTED_SPIDER_EYE
            }
        }
    }

    fun spawn(gameManager: GameManager){
        if(type == GeneratorType.DIAMOND) return // забанить нафиг возможность спавнить алмазы (защита от граша)

        if(!activated){
            if(armorStand != null){
                armorStand?.isCustomNameVisible = false
            }
            return
        }

        secondsSinceActivation++

        if(!isIslandGenerator) {
            armorStand?.isCustomNameVisible = true
            armorStand?.customName = Colorize.c(getArmorstandName())
        }

        if(secondsSinceActivation < getActivationTime()) return

        secondsSinceActivation = 0
        val name = type.getName()

        val resourceType: Material = type.getMaterial()

        val playersCount = GameManager.getNearbyPlayers(location, 2.0).size

        if(playersCount > 0){
            if (!GameManager.isLagged){
                for(player in GameManager.getNearbyPlayers(location, 2.0)){
                    if(player.gameMode == GameMode.SPECTATOR || gameManager.deadPlayers.contains(player.uniqueId)) continue
                    player.inventory.addItem(ItemBuilder(resourceType, if(PlayerData.PLAYERS[player.uniqueId]!!.isGeneratorMultiplier) 2 else 1).setName(name).toItemStack())
                    player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 1f, 1f)
                }
            }
        } else {
            location.world!!.dropItem(location, ItemBuilder(resourceType, 1).setName(name).toItemStack())
        }
    }

    private fun getArmorstandName(): String{
        var timeLeft: Int = getActivationTime() - secondsSinceActivation
        if(timeLeft == 0){
            timeLeft = getActivationTime()
        }

        val pluralize: String = if(timeLeft == 1) "" else "s"
        val colorCode: String = if(type == GeneratorType.EMERALD) "&4" else if (type == GeneratorType.DIAMOND) "&b" else ""
        return "${colorCode}Ruby at $timeLeft second$pluralize..."
    }

    private fun getActivationTime(): Int {
        when(type){
            GeneratorType.IRON -> {
                return when (currentTier) {
                    GeneratorTier.ONE -> {
                        3
                    }
                    GeneratorTier.TWO -> {
                        2
                    }
                    else -> {
                        1
                    }
                }
            }
            GeneratorType.GOLD -> {
                return when (currentTier) {
                    GeneratorTier.ONE -> {
                        8
                    }
                    GeneratorTier.TWO -> {
                        4
                    }
                    else -> {
                        2
                    }
                }
            }
            GeneratorType.DIAMOND -> {
                return when (currentTier) {
                    GeneratorTier.ONE -> {
                        40
                    }
                    GeneratorTier.TWO -> {
                        15
                    }
                    else -> {
                        10
                    }
                }
            }
            GeneratorType.EMERALD -> {
                if(isIslandGenerator){
                    if (currentTier == GeneratorTier.ONE) {
                        return 35
                    }
                    else if (currentTier == GeneratorTier.TWO) {
                        return 15
                    }
                } else {
                    return when (currentTier) {
                        GeneratorTier.ONE -> {
                            30
                        }
                        GeneratorTier.TWO -> {
                            25
                        }
                        else -> {
                            20
                        }
                    }
                }
            }
        }
        return 20
    }

    companion object{
        fun getMaterialByGeneratorType(type: GeneratorType): Material {
            return when (type) {
                GeneratorType.IRON -> {
                    GeneratorType.IRON.getMaterial()
                }
                GeneratorType.GOLD -> {
                    GeneratorType.GOLD.getMaterial()
                }
                GeneratorType.DIAMOND -> {
                    Material.DIAMOND
                }
                GeneratorType.EMERALD -> {
                    Material.FERMENTED_SPIDER_EYE
                }
            }
        }

        fun getGeneratorTypeByMaterial(material: Material): String{
            return when(material){
                Material.GHAST_TEAR -> "Iron"
                Material.GOLD_NUGGET -> "Gold"
                Material.FERMENTED_SPIDER_EYE -> "Rubies"
                else -> ""
            }
        }

        fun getColorByGenerator(material: Material): ChatColor{
            return when(material){
                Material.GHAST_TEAR -> ChatColor.WHITE
                Material.GOLD_NUGGET -> ChatColor.GOLD
                Material.FERMENTED_SPIDER_EYE -> ChatColor.DARK_RED
                else -> ChatColor.WHITE
            }
        }
    }
}