package me.udnek.coreu.nms.loot.util;

import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

@org.jspecify.annotations.NullMarked public  interface ItemStackCreator{
    ItemStack createItemStack(LootContext lootContext);

    class Material implements ItemStackCreator{
        protected org.bukkit.Material material;
        public Material(org.bukkit.Material material){
            this.material= material;
        }
        @Override
        public ItemStack createItemStack(LootContext lootContext) {
            return new ItemStack(material);
        }
    }

    class Custom implements ItemStackCreator{
        protected CustomItem customItem;
        public Custom(CustomItem customItem){
            this.customItem = customItem;
        }
        @Override
        public ItemStack createItemStack(LootContext lootContext) {
            return customItem.getItem();
        }
    }
}
