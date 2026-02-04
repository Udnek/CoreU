package me.udnek.coreu.custom.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.udnek.coreu.util.SelfRegisteringListener;
import me.udnek.coreu.util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class CraftListener extends SelfRegisteringListener{

    public CraftListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        ItemStack result = event.getInventory().getResult();
        if (result == null) return;
        craft(
                event.getRecipe(),
                result,
                event.getInventory().getMatrix(),
                event.isRepair(),
                itemStack -> event.getInventory().setResult(itemStack)
        );

        Utils.consumeIfNotNull(CustomItem.get(event.getRecipe().getResult()), customItem ->
                customItem.onPrepareCraft(event, event.getInventory().getResult()));
    }

    @EventHandler
    public void onCrafterCraft(CrafterCraftEvent event) {
        craft(
                event.getRecipe(),
                event.getResult(),
                ((Crafter) event.getBlock().getState()).getInventory().getContents(),
                event.getRecipe().key().asString().equals("minecraft:repair_item"),
                event::setResult
        );
        Utils.consumeIfNotNull(CustomItem.get(event.getRecipe().getResult()), customItem ->
                customItem.onCrafterCraft(event));
    }

    public void craft(final Recipe recipe, final ItemStack result, final @Nullable ItemStack[] matrix, final boolean isRepair, final Consumer<ItemStack> resultConsumer){
        List<RecipeChoice> recipeChoices;

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            recipeChoices = new ArrayList<>(shapedRecipe.getChoiceMap().values());
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            recipeChoices = shapelessRecipe.getChoiceList();
        } else if (recipe instanceof TransmuteRecipe transmuteRecipe) {
            recipeChoices = List.of(transmuteRecipe.getInput(), transmuteRecipe.getMaterial());
        } else if (isRepair) {
            ItemStack component0 = null;
            ItemStack component1 = null;
            for (ItemStack itemStack : matrix) {
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                if (component0 == null) {
                    component0 = itemStack;
                }
                else {
                    component1 = itemStack;
                    break;
                }
            }
            Preconditions.checkArgument(component0 != null && component1 != null, "Repair item is null!");

            if (ItemUtils.isSameIds(component0, component1)){
                CustomItem customItem = CustomItem.get(component0);
                if (customItem == null) return;
                ItemStack newResult = customItem.getItem();
                newResult.setData(DataComponentTypes.DAMAGE, result.getDataOrDefault(DataComponentTypes.DAMAGE, 0));
                resultConsumer.accept(newResult);
            } else {
                resultConsumer.accept(new ItemStack(Material.AIR));
            }
            return;

        } else if (recipe instanceof ComplexRecipe) {
            for (@Nullable ItemStack itemStack : matrix) {
                if (itemStack == null) continue;
                if (CustomItem.isCustom(itemStack)) {
                    resultConsumer.accept(new ItemStack(Material.AIR));
                    return;
                }
            }
            return;
        } else return;

        int amountOfSemiCustomChoices = 0;
        int amountOfCustomChoices = 0;
        for (RecipeChoice recipeChoice : recipeChoices ) {
            if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
                boolean hasCustom = false;
                boolean hasVanilla = false;
                for (ItemStack choice : exactChoice.getChoices()) {
                    if (ItemUtils.isVanillaOrReplaced(choice)) hasVanilla = true;
                    else hasCustom = true;
                }
                if (hasVanilla && hasCustom) amountOfSemiCustomChoices += 1;
                else if (hasCustom) amountOfCustomChoices += 1;
            }
        }

        int amountOfCustomItems = 0;
        for (ItemStack itemStack : matrix) {
            if (itemStack == null) continue;
            if (!ItemUtils.isVanillaOrReplaced(itemStack)) amountOfCustomItems += 1;
        }

        if (!(amountOfCustomChoices <= amountOfCustomItems && amountOfCustomItems <= (amountOfCustomChoices + amountOfSemiCustomChoices))) {
            resultConsumer.accept(new ItemStack(Material.AIR));
        }
    }

    public boolean extraTestChoice(RecipeChoice recipeChoice, ItemStack stack){
        if (recipeChoice instanceof RecipeChoice.MaterialChoice){
            return !CustomItem.isCustom(stack) || VanillaItemManager.isReplaced(stack);
        } if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice){
            for (ItemStack choice : exactChoice.getChoices()) {
                if (ItemUtils.isSameIds(choice, stack)) return true;
            }
            return false;
        } else {
            throw new RuntimeException("Unsupported recipeChoice: " + recipeChoice);
        }
    }

    @EventHandler
    public void onFurnace(BlockCookEvent event){
        CookingRecipe<?> recipe = event.getRecipe();
        if (recipe == null) return;
        if (!extraTestChoice(recipe.getInputChoice(), event.getSource())) event.setCancelled(true);
    }

    @EventHandler
    public void onGrindstone(PrepareGrindstoneEvent event){
        ItemStack upperItem = event.getInventory().getUpperItem();
        ItemStack lowerItem = event.getInventory().getLowerItem();
        if (upperItem == null || lowerItem == null) return;
        if (ItemUtils.isSameIds(upperItem, lowerItem)) return;
        event.setResult(new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event){
        ItemStack firstItem = event.getInventory().getFirstItem();
        ItemStack secondItem = event.getInventory().getSecondItem();

        if (firstItem == null || secondItem == null) return;
        if (secondItem.getType() == Material.ENCHANTED_BOOK) return;
        if (ItemUtils.isSameIds(firstItem, secondItem)) return;

        if (!canBeRepaired(firstItem, secondItem)) event.setResult(new ItemStack(Material.AIR));
    }

    public boolean canBeRepaired(ItemStack toBeRepaired, ItemStack repairer){
        CustomItem toBeRepairedCustom = CustomItem.get(toBeRepaired);
        CustomItem repairerCustom = CustomItem.get(repairer);
        if (toBeRepairedCustom != null){
            RepairData repairData = toBeRepairedCustom.getRepairData();
            if (repairData == null) return true;
            return repairData.contains(repairer);
        }
        return repairerCustom == null;
    }
}















