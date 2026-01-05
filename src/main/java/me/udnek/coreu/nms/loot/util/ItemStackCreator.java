package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.NotNull;

public interface ItemStackCreator {
    @NotNull ItemStack createItemStack(@NotNull LootContext lootContext);

    class Material implements ItemStackCreator{
        protected org.bukkit.Material material;
        public Material(@NotNull org.bukkit.Material material){
            this.material= material;
        }
        @Override
        public @NotNull ItemStack createItemStack(@NotNull LootContext lootContext) {
            return new ItemStack(material);
        }
    }

    class Custom implements ItemStackCreator{
        protected CustomItem customItem;
        public Custom(@NotNull CustomItem customItem){
            this.customItem = customItem;
        }
        @Override
        public @NotNull ItemStack createItemStack(@NotNull LootContext lootContext) {
            return customItem.getItem();
        }
    }
}
