package me.udnek.coreu.custom.recipe.builder;

import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.recipe.RecipeManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("unchecked")
@NullMarked
public abstract class RecipeBuilder<Builder extends RecipeBuilder<?>> {
    protected static int recipeCounter = 0;

    protected boolean removeVanillaRecipes = true;
    protected @Nullable ItemStack result;
    protected @Nullable String key;

    public Builder removeVanillaRecipes(boolean remove){
        this.removeVanillaRecipes = remove;
        return (Builder) this;
    }

    public Builder result(ItemStack stack){
        this.result = stack;
        CustomItem customItem = CustomItem.get(stack);
        if (customItem != null){
            return key(customItem.getKey().getKey()+"_"+recipeCounter++);
        }
        return key(stack.getType().name().toLowerCase()+"_"+recipeCounter++);
    }

    public Builder result(Material material){
        return result(new ItemStack(material));
    }

    public Builder result(CustomItem customItem){
        return result(customItem.getItem());
    }

    public Builder key(String key){
        this.key = key;
        return (Builder) this;
    }

    public Builder resultAmount(int amount){
        assert this.result != null;
        this.result.setAmount(amount);
        return (Builder) this;
    }

    public void build(Plugin plugin){
        if (removeVanillaRecipes){
            RecipeManager.getInstance().getRecipesAsResult(result,
                    recipe -> {
                        if (!(recipe instanceof Keyed keyedRecipe)) return;
                        if (!keyedRecipe.key().namespace().equals(Key.MINECRAFT_NAMESPACE)) return;
                        RecipeManager.getInstance().unregister(recipe);
                    });
        }
        buildAndRegisterRecipe(plugin);
    }
    protected abstract void buildAndRegisterRecipe(Plugin plugin);
}
