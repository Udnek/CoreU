package me.udnek.coreu.mixin;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.nms.NmsUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.*;


@NullMarked
@Mixin(Ingredient.class)
abstract class IngredientMixin{

    @Final
    @Shadow
    public HolderSet<Item> values;

    // now used only to display
    @Shadow
    private @Nullable Set<ItemStack> itemStacks;
    @Unique
    public @Nullable Set<CustomItem> coreu$customItems;


    /**
     * @author Udnek
     * @reason cause why not lol
     */
    @Overwrite
    public static Ingredient ofStacks(List<ItemStack> stacks){
        var customItems = new ArrayList<CustomItem>();
        for (var stack : stacks) {
            var customItem = CustomItem.get(NmsUtils.toBukkit(stack));
            if (customItem != null)
                customItems.add(customItem);
        }

        var result = Ingredient.of(stacks.stream().map(ItemStack::getItem));
        var ingredientMixin = (IngredientMixin) (Object) result;
        ingredientMixin.coreu$customItems = new LinkedHashSet<>(customItems);
        ingredientMixin.itemStacks = ItemStackLinkedSet.createTypeAndComponentsSet();
        ingredientMixin.itemStacks.addAll(stacks);
        return result;
    }

    /**
     * @author Udnek
     * @reason cause why not lol
     */
    @Overwrite
    public boolean test(ItemStack stack){
        var customItem = CustomItem.get(NmsUtils.toBukkit(stack));
        if (customItem != null){ // custom item matching
            if (coreu$customItems == null) return false;
            return coreu$customItems.contains(customItem);
        }
//        if (itemStacks != null){ // exact matching
//            return itemStacks.contains(stack);
//        }
        return stack.is(values); // material matching
    }
}
