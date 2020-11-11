package me.mrfunny.plugins.paper.util

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

/**
 * Easily create itemstacks, without messing your hands.
 * *Note that if you do use this in one of your projects, leave this notice.*
 * *Please do credit me if you do use this in one of your projects.*
 * @author NonameSL, edit MrFunny
 */
class ItemBuilder {

    private var `is`: ItemStack

    /**
     * Create a new ItemBuilder over an existing itemstack.
     * @param is The itemstack to create the ItemBuilder over.
     */
    constructor(`is`: ItemStack) {
        this.`is` = `is`
    }
    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material of the item.
     * @param amount The amount of the item.
     */
    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material to create the ItemBuilder with.
     */
    @JvmOverloads
    constructor(m: Material?, amount: Int = 1) {
        `is` = ItemStack(m!!, amount)
    }

    /**
     * Create a new ItemBuilder from scratch.
     * @param m The material of the item.
     * @param amount The amount of the item.
     * @param durability The durability of the item.
     */
    constructor(m: Material?, amount: Int, durability: Byte) {
        `is` = ItemStack(m!!, amount, durability.toShort())
    }

    /**
     * Clone the ItemBuilder into a new one.
     * @return The cloned instance.
     */
    fun clone(): ItemBuilder {
        return ItemBuilder(`is`)
    }

    /**
     * Change the durability of the item.
     * @param dur The durability to set it to.
     */
    fun setDurability(dur: Short): ItemBuilder {
        `is`.durability = dur
        return this
    }

    /**
     * Set the displayname of the item.
     * @param name The name to change it to.
     */
    fun setName(name: String): ItemBuilder {
        val im = `is`.itemMeta
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name))
        `is`.itemMeta = im
        return this
    }

    /**
     * Add an unsafe enchantment.
     * @param ench The enchantment to add.
     * @param level The level to put the enchant on.
     */
    fun addUnsafeEnchantment(ench: Enchantment?, level: Int): ItemBuilder {
        `is`.addUnsafeEnchantment(ench!!, level)
        return this
    }

    /**
     * Remove a certain enchant from the item.
     * @param ench The enchantment to remove
     */
    fun removeEnchantment(ench: Enchantment?): ItemBuilder {
        `is`.removeEnchantment(ench!!)
        return this
    }

    /**
     * Set the skull owner for the item. Works on skulls only.
     * @param owner The name of the skull's owner.
     */
    fun setSkullOwner(owner: String?): ItemBuilder {
        try {
            val im = `is`.itemMeta as SkullMeta
            im.owner = owner
            `is`.setItemMeta(im)
        } catch (expected: ClassCastException) {
        }
        return this
    }

    /**
     * Add an enchant to the item.
     * @param ench The enchant to add
     * @param level The level
     */
    fun addEnchant(ench: Enchantment?, level: Int): ItemBuilder {
        val im = `is`.itemMeta
        im.addEnchant(ench!!, level, true)
        `is`.itemMeta = im
        return this
    }

    /**
     * Add multiple enchants at once.
     * @param enchantments The enchants to add.
     */
    fun addEnchantments(enchantments: Map<Enchantment?, Int?>?): ItemBuilder {
        `is`.addEnchantments(enchantments!!)
        return this
    }

    /**
     * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
     */
    fun setInfinityDurability(): ItemBuilder {
        `is`.durability = Short.MAX_VALUE
        return this
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    fun setLore(vararg lore: String?): ItemBuilder {
        val im = `is`.itemMeta
        im.lore = listOf(*lore)
        `is`.itemMeta = im
        return this
    }

    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    fun setLore(lore: List<String?>?): ItemBuilder {
        val im = `is`.itemMeta
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    fun removeLoreLine(line: String): ItemBuilder {
        val im = `is`.itemMeta
        val lore: MutableList<String> = ArrayList(im.lore)
        if (!lore.contains(line)) return this
        lore.remove(line)
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    /**
     * Remove a lore line.
     * @param index The index of the lore line to remove.
     */
    fun removeLoreLine(index: Int): ItemBuilder {
        val im = `is`.itemMeta
        val lore = arrayListOf<String>()
        if (index < 0 || index > lore.size) return this
        lore.removeAt(index)
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    fun setUnbreakable(u: Boolean): ItemBuilder{
        val im = `is`.itemMeta
        im.isUnbreakable = u
        return this
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    fun addLoreLine(line: String): ItemBuilder {
        val im = `is`.itemMeta
        var lore: MutableList<String?> = ArrayList()
        if (im.hasLore()) lore = ArrayList(im.lore)
        lore.add(Colorize.c(line))
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    /**
     * Add a lore line.
     * @param line The lore line to add.
     * @param pos The index of where to put it.
     */
    fun addLoreLine(line: String, pos: Int): ItemBuilder {
        val im = `is`.itemMeta
        val lore: MutableList<String> = ArrayList(im.lore)
        lore[pos] = line
        im.lore = lore
        `is`.itemMeta = im
        return this
    }

    /**
     * Sets the dye color on an item.
     * *** Notice that this doesn't check for item type, sets the literal data of the dyecolor as durability.**
     * @param color The color to put.
     */
    fun setDyeColor(color: DyeColor): ItemBuilder {
        `is`.durability = color.dyeData.toShort()
        return this
    }

    /**
     * Sets the dye color of a wool item. Works only on wool.
     * @see ItemBuilder@setDyeColor
     * @param color The DyeColor to set the wool item to.
     */
    @Deprecated(
        """As of version 1.2 changed to setDyeColor.
      """
    )
    fun setWoolColor(color: DyeColor): ItemBuilder {
        if (!`is`.type.toString().contains("WOOL")) return this
        `is`.durability = color.woolData.toShort()
        return this
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     * @param color The color to set it to.
     */
    fun setLeatherArmorColor(color: Color?): ItemBuilder {
        try {
            val im = `is`.itemMeta as LeatherArmorMeta
            im.setColor(color)
            `is`.setItemMeta(im)
        } catch (ignored: ClassCastException) {
        }
        return this
    }

    /**
     * Retrieves the itemstack from the ItemBuilder.
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    fun toItemStack(): ItemStack {
        return `is`
    }
}