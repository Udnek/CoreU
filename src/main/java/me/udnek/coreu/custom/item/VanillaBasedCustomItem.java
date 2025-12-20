package me.udnek.coreu.custom.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.udnek.coreu.custom.event.CustomItemGeneratedEvent;
import me.udnek.coreu.custom.recipe.RecipeManager;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public class VanillaBasedCustomItem extends AbstractRegistrableComponentable<CustomItem> implements UpdatingCustomItem {

    protected ItemStack itemStack;
    protected RepairData repairData = null;
    protected final Material material;

    public VanillaBasedCustomItem(@NotNull Material material){
        this.material = material;
    }

    @Override
    public @NotNull String getRawId() {return material.name().toLowerCase();}

    @Override
    public @NotNull ItemStack getItem() {
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
    public @NotNull NamespacedKey getNewRecipeKey() {
        AtomicInteger amount = new AtomicInteger(0);
        getRecipes(recipe -> amount.incrementAndGet());
        if (amount.get() == 0) getKey();
        return NamespacedKey.fromString(getId() + "_" + amount.get());
    }

    @Override
    public void setCooldown(@NotNull Player player, int ticks) {player.setCooldown(getItem(), ticks);}

    @Override
    public int getCooldown(@NotNull Player player) {return player.getCooldown(getItem());}

    @Override
    public boolean isTagged(@NotNull Tag<Material> tag) {return tag.isTagged(material);}

    @Override
    public void getRecipes(@NotNull Consumer<@NotNull Recipe> consumer) {
        RecipeManager.getInstance().getRecipesAsResult(getItem(), consumer);
    }

    @Override
    public void registerRecipe(@NotNull Recipe recipe) {
        RecipeManager.getInstance().register(recipe);
    }

    @Override
    public @NotNull String translationKey() {
        return material.translationKey();
    }
}
