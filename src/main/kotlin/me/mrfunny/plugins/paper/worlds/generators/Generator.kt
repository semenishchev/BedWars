package me.mrfunny.plugins.paper.worlds.generators

import me.mrfunny.plugins.paper.util.Colorize
import me.mrfunny.plugins.paper.util.ItemBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

class Generator(var location: Location, var type: GeneratorType, val isIslandGenerator: Boolean) {

    private var armorStand: ArmorStand? = null
    var currentTier: GeneratorTier = GeneratorTier.ONE

    var activated: Boolean = false
    set(value) {
        if(type == GeneratorType.DIAMOND) return
        if(value == field) return

        field = value

        if(isIslandGenerator) return

        armorStand = location.world.spawn(location.add(0.0, 1.0, 0.0), ArmorStand::class.java)

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
                Material.GHAST_TEAR
            }
            GeneratorType.GOLD -> {
                Material.GOLD_NUGGET
            }
            GeneratorType.DIAMOND -> {
                Material.DIAMOND
            }
            GeneratorType.EMERALD -> {
                Material.FERMENTED_SPIDER_EYE
            }
        }
    }

    fun spawn(){
        if(type == GeneratorType.DIAMOND) return // забанить нафиг возможность спавнить алмазы на базе (защита от дурака)

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
        var name = ""

        val resourceType: Material = when(type){
            GeneratorType.IRON -> {
                name = "&fIron"
                Material.GHAST_TEAR
            }
            GeneratorType.GOLD -> {
                name = "&6Gold"
                Material.GOLD_NUGGET
            }
            GeneratorType.DIAMOND -> {
                name = "&4Lol"
                Material.DIAMOND
            }
            GeneratorType.EMERALD -> {
                name = "&4Ruby"
                Material.FERMENTED_SPIDER_EYE
            }
        }

        location.world.dropItem(location, ItemBuilder(resourceType).setName(name).toItemStack())
    }

    private fun getArmorstandName(): String{
        var timeLeft: Int = getActivationTime() - secondsSinceActivation
        if(timeLeft == 0){
            timeLeft = getActivationTime()
        }
//        val typeName: String = type.name.toLowerCase().capitalize()
        val pluralize: String = if(timeLeft == 1) "" else "s"
        val colorCode: String = if(type == GeneratorType.EMERALD) "&4" else if (type == GeneratorType.DIAMOND) "&b" else ""
        return "${colorCode}Ruby at $timeLeft second$pluralize..." //todo: same color code as material color
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
                        return 60
                    }
                    else if (currentTier == GeneratorTier.TWO) {
                        return 20
                    }
                } else {
                    return when (currentTier) {
                        GeneratorTier.ONE -> {
                            40
                        }
                        GeneratorTier.TWO -> {
                            30
                        }
                        else -> {
                            15
                        }
                    }
                }
            }
        }
        return 20
    }

    companion object{
        fun getItemStack(type: GeneratorType): Material {
            return when (type) {
                GeneratorType.IRON -> {
                    Material.GHAST_TEAR
                }
                GeneratorType.GOLD -> {
                    Material.GOLD_NUGGET
                }
                GeneratorType.DIAMOND -> {
                    Material.DIAMOND
                }
                GeneratorType.EMERALD -> {
                    Material.FERMENTED_SPIDER_EYE
                }
            }
        }
    }
}