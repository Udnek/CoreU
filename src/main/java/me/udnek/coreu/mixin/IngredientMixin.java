package me.udnek.coreu.mixin;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;


@Mixin(Ingredient.class)
abstract class IngredientMixin{

    @Final
    @Shadow
    public HolderSet<Item> values;
    @Shadow
    private @Nullable Set<ItemStack> itemStacks;
    @Unique
    private final boolean coreu$doCustomItemCheck;

    @Inject(method = "ofStacks", at = @At("RETURN"))
    private static void onOfStacks(List<ItemStack> stacks, CallbackInfoReturnable<Ingredient> cir){
          stacks.forEach(a);
    }

    /**
     * @author Udnek
     * @reason cause fuck it
     */
    @Overwrite
    public boolean test(ItemStack stack){

    }
}
