package me.udnek.coreu.custom.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.udnek.coreu.custom.event.CustomItemGeneratedEvent;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
@org.jspecify.annotations.NullMarked
public class VanillaBasedCustomItem extends AbstractRegistrableComponentable<CustomItem>implements UpdatingCustomItem{

    protected @Nullable ItemStack itemStack;
    protected @Nullable RepairData repairData = null;
    protected final Material material;

    public VanillaBasedCustomItem(Material material){
        this.material = material;
    }

    @Override
    public String getRawId() {return material.name().toLowerCase();}

    @Override
    public ItemStack getItem() {
        if (itemStack == null){
            ItemStack newItemStack = new ItemStack(material);
            CustomItemGeneratedEvent event = new CustomItemGeneratedEvent(this, newItemStack, null, null);
            event.callEvent();
            event.getLoreBuilder().buildAndApply(event.getItemStack());
            repairData = event.getRepairData();
            itemStack = event.getItemStack();
            if (repairData != null) itemStack.setData(DataComponentTypes.REPAIRABLE, repairData.getSuitableVanillaRepairable());
        }
        return itemStack.clone();
    }

    @Override
    public @Nullable RepairData getRepairData() {
        return repairData;
    }

    @Override
    public void setCooldown(Player player, int ticks) {player.setCooldown(getItem(), ticks);}

    @Override
    public int getCooldown(Player player) {return player.getCooldown(getItem());}

    @Override
    public boolean isTagged(Tag<Material> tag) {return tag.isTagged(material);}

    @Override
    public String translationKey() {
        return material.translationKey();
    }
}
