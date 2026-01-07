package me.udnek.coreu.mixin;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Ingredient.class)
abstract public class IngredientMixin {
    @Inject(method = "testOptionalIngredient", at = @At(value = "HEAD"))
    private static void testOptionalIngredient(Optional<Ingredient> ingredient, ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        System.out.println("called test ingredient with: "+ stack);
    }
}
