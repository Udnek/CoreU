package me.udnek.coreu.custom.recipe;

import me.udnek.coreu.custom.component.AbstractComponentHolder;

public abstract class AbstractCustomRecipe<T extends CustomRecipeType<?>> extends AbstractComponentHolder<CustomRecipe<T>> implements CustomRecipe<T>{
}
