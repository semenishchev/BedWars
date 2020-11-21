package me.mrfunny.plugins.paper.worlds.generators

import me.mrfunny.plugins.paper.util.Colorize
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

    var secondsSinceActivation: Int = 0

    fun spawn(){
        if(type == GeneratorType.DIAMOND && isIslandGenerator) return // забанить нафиг возможность спавнить алмазы на базе (защита от дурака)

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

        val resourceType: Material = when(type){
            GeneratorType.IRON -> {
                Material.IRON_INGOT
            }
            GeneratorType.GOLD -> {
                Material.GOLD_INGOT
            }
            GeneratorType.DIAMOND -> {
                Material.DIAMOND
            }
            GeneratorType.EMERALD -> {
                Material.EMERALD
            }
        }

        location.world.dropItem(location, ItemStack(resourceType))
    }

    private fun getArmorstandName(): String{
        var timeLeft: Int = getActivationTime() - secondsSinceActivation
        if(timeLeft == 0){
            timeLeft = getActivationTime()
        }
        val typeName: String = type.name.toLowerCase().capitalize()
        val pluralize: String = if(timeLeft == 1) "" else "s"
        return "&a$typeName: $timeLeft second$pluralize..." //todo: same color code as material color
    }

    private fun getActivationTime(): Int {
        when(type){
            GeneratorType.IRON -> {
                return when (currentTier) {
                    GeneratorTier.ONE -> {
                        4
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
                        10
                    }
                    GeneratorTier.TWO -> {
                        5
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
                        return 40
                    }
                    else if (currentTier == GeneratorTier.TWO) {
                        return 20
                    }
                } else {
                    return when (currentTier) {
                        GeneratorTier.ONE -> {
                            25
                        }
                        GeneratorTier.TWO -> {
                            20
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
}