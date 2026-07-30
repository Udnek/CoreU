package me.udnek.coreu.custom.recipe;

import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import org.jspecify.annotations.NullMarked;

@NullMarked public class CustomRecipeType<Recipe extends CustomRecipe> extends AbstractRegistrableComponentable<CustomRecipeType<Recipe>>{

    private final String rawId;

    public CustomRecipeType(String rawId){
        this.rawId = rawId;
    }

    @Override
    public String getRawId() {
        return rawId;
    }
}
