package me.udnek.coreu.custom.inventory;

import me.udnek.coreu.custom.item.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public abstract class ConstructableCustomInventory implements CustomInventory{

    private @Nullable Inventory inventory;

    public abstract @Nullable Component getTitle();
    public abstract int getInventorySize();

    public Inventory generateInventory(int size, @Nullable Component title){
        Inventory inventory;
        if (title == null) inventory = Bukkit.createInventory(this, size);
        else inventory = Bukkit.createInventory(this, size, title);
        return inventory;
    }

    public void setItem(int slot, @Nullable ItemStack stack){
        getInventory().setItem(slot, stack);
    }

    public void setItem(int slot, CustomItem item){
        setItem(slot, item.getItem());
    }

    public void setItem(int slot, Material material){
        if (material.isItem()) setItem(slot, new ItemStack(material));
    }

    public void addItem(int slot, int amount){
        ItemStack item = getInventory().getItem(slot);
        if (item == null) return;
        setItem(slot, item.add(amount));
    }

    public void takeItem(int slot, int amount){
        addItem(slot, -amount);
    }

    @Override
    public final Inventory getInventory() {
        if (inventory == null) inventory = generateInventory(getInventorySize(), getTitle());
        return inventory;
    }
}
