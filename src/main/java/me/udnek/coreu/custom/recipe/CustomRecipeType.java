package me.udnek.coreu.custom.recipe;

import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import org.jetbrains.annotations.NotNull;

public class CustomRecipeType<Recipe extends CustomRecipe> extends AbstractRegistrableComponentable<CustomRecipeType<Recipe>> {

    private final @NotNull String rawId;

    public CustomRecipeType(@NotNull String rawId){
        this.rawId = rawId;
    }

    @Override
    public @NotNull String getRawId() {
        return rawId;
    }
}
