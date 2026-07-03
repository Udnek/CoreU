package me.udnek.coreu.custom.recipe.choice;

import me.udnek.coreu.custom.item.CustomItem;
import org.bukkit.Material;

import java.util.Set;

@org.jspecify.annotations.NullMarked public class CustomSingleRecipeChoice extends CustomCompatibleRecipeChoice{

    public CustomSingleRecipeChoice(CustomItem customItem) {
        super(Set.of(customItem), Set.of());
    }
    public CustomSingleRecipeChoice(Material material) {
        super(Set.of(), Set.of(material));
    }
}
