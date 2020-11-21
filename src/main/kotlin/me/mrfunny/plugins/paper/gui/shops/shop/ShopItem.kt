package me.mrfunny.plugins.paper.gui.shops.shop

import javafx.scene.paint.Material
import org.bukkit.inventory.ItemStack

// todo: make upgrades with this system
abstract class ShopItem {
    abstract val price: Int
    abstract val currencyMaterial: Material
    abstract val category: ShopCategory
    abstract val item: ItemStack
}