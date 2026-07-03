package me.udnek.coreu

import net.minecraft.world.item.ItemStack
import java.util.function.Function


data class HorizonBridge(
    val containsCustomItem: (List<ItemStack>) -> Boolean,
    val process: Function<List<ItemStack>>
) {

    companion object {
        lateinit var instance: HorizonBridge
    }
}
