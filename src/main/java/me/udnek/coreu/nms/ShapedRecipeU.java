package me.udnek.coreu.nms;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.util.Reflex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@org.jspecify.annotations.NullMarked public class ShapedRecipeU extends ShapedRecipe{

    protected final ShapedRecipePattern pattern;

    public ShapedRecipeU(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);
        this.pattern = pattern;
    }

    protected List<Optional<Ingredient>> pattern_ingredients(){
        return Reflex.getFieldValue(pattern, "ingredients");
    }
    protected int pattern_ingredientCount(){
        return Reflex.getFieldValue(pattern, "ingredientCount");
    }
    protected int pattern_width(){
        return Reflex.getFieldValue(pattern, "width");
    }
    protected int pattern_height(){
        return Reflex.getFieldValue(pattern, "height");
    }
    protected boolean pattern_symmetrical(){
        return Reflex.getFieldValue(pattern, "symmetrical");
    }


    @Override
    public boolean matches(CraftingInput input, Level level) {
        return pattern_matches(input);
    }

    public boolean pattern_matches(CraftingInput input) {
        if (input.ingredientCount() != pattern_ingredientCount()) {
            return false;
        } else {
            if (input.width() == pattern_width() && input.height() == pattern_height()) {
                if (!pattern_symmetrical() && pattern_matches(input, true)) {
                    return true;
                }

                if (pattern_matches(input, false)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean pattern_matches(CraftingInput input, boolean symmetrical) {
        for(int i = 0; i < pattern_height(); ++i) {
            for(int i1 = 0; i1 < pattern_width(); ++i1) {
                Optional<Ingredient> optional;
                if (symmetrical) {
                    optional = pattern_ingredients().get(pattern_width() - i1 - 1 + i * pattern_width());
                } else {
                    optional = pattern_ingredients().get(i1 + i * pattern_width());
                }
                ItemStack item = input.getItem(i1, i);
                if (!testOptionalIngredient(optional, item)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* ORIGINAL CODE IN Ingredient.class
    public static boolean testOptionalIngredient(Optional<Ingredient> ingredient, ItemStack stack) {
        Optional var10000 = ingredient.map((ingredient1) -> ingredient1.test(stack));
        Objects.requireNonNull(stack);
        return (Boolean)var10000.orElseGet(stack::isEmpty);
    }
     */
    protected static boolean testOptionalIngredient(Optional<Ingredient> ingredient, ItemStack stack){
        Objects.requireNonNull(stack);
        org.bukkit.inventory.ItemStack bukkitStack = CraftItemStack.asBukkitCopy(stack);
        CustomItem customItem = CustomItem.get(bukkitStack);
        Optional<Boolean> test;
        if (customItem != null){
            test = ingredient.map(ingredient2 ->
            {
                Set<ItemStack> items = ingredient2.itemStacks();
                // VANILLA MATERIALS
                if (items == null) return false;
                // ANY STACK IS CUSTOM
                return items.stream().anyMatch(item -> CustomItem.get(CraftItemStack.asBukkitCopy(item)) == customItem);
            });
        } else {
            test = ingredient.map((ingredient1) -> ingredient1.test(stack));
        }
        return test.orElseGet(stack::isEmpty);
    }
}
