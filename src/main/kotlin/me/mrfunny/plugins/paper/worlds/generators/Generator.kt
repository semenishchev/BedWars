package me.mrfunny.plugins.paper.worlds.generators

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

class Generator(var location: Location, var type: GeneratorType) {

    var activated: Boolean = false

    fun spawn(){

        if(!activated) return

        val item: Item = location.world.spawnEntity(location, EntityType.DROPPED_ITEM) as Item
        when(type){
            GeneratorType.IRON -> {
                item.itemStack = ItemStack(Material.IRON_INGOT)
            }
            GeneratorType.GOLD -> {
                item.itemStack = ItemStack(Material.GOLD_INGOT)
            }
            GeneratorType.DIAMOND -> {
                item.itemStack = ItemStack(Material.DIAMOND)
            }
            GeneratorType.EMERALD -> {
                item.itemStack = ItemStack(Material.EMERALD)
            }
        }
    }

}