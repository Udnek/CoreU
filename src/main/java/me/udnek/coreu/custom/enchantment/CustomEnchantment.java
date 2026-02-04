package me.udnek.coreu.custom.enchantment;

import me.udnek.coreu.custom.attribute.CustomAttributeConsumer;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.Registrable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public  interface CustomEnchantment extends Registrable{

    static @Nullable CustomEnchantment get(Enchantment enchantment){
        return CustomRegistries.ENCHANTMENT.get(enchantment.getKey().toString());
    }

    static boolean isCustom(Enchantment enchantment){
        return get(enchantment) != null;
    }

    ItemStack createEnchantedBook(int level);
    void enchant(ItemStack itemStack, int level);
    void enchantBook(ItemStack itemStack, int level);
    Enchantment getBukkit();

    void getCustomAttributes(int level, CustomAttributeConsumer consumer);

}
