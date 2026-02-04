package me.udnek.coreu.custom.event;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.RepairData;
import me.udnek.coreu.util.LoreBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public class CustomItemGeneratedEvent extends CustomEvent{
    protected CustomItem customItem;
    protected ItemStack itemStack;
    protected LoreBuilder loreBuilder;
    protected @Nullable RepairData repairData;

    public CustomItemGeneratedEvent(CustomItem customItem, ItemStack itemStack, @Nullable LoreBuilder loreBuilder, @Nullable RepairData repairData){
        this.customItem = customItem;
        this.itemStack = itemStack;
        this.repairData = repairData;
        if (loreBuilder == null) this.loreBuilder = new LoreBuilder();
        else this.loreBuilder = loreBuilder;
    }
    public CustomItem getCustomItem() {
        return customItem;
    }
    public ItemStack getItemStack() {
        return itemStack;
    }
    public LoreBuilder getLoreBuilder() {
        return loreBuilder;
    }
    public @Nullable RepairData getRepairData(){return repairData;}
    public void setRepairData(@Nullable RepairData repairData) {this.repairData = repairData;}

    public void setItemStack(ItemStack itemStack){
        this.itemStack = itemStack;
    }
}
