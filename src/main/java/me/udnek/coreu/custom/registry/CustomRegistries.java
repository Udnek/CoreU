package me.udnek.coreu.custom.registry;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.effect.CustomEffect;
import me.udnek.coreu.custom.enchantment.CustomEnchantment;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.recipe.CustomRecipeType;
import me.udnek.coreu.custom.sound.CustomSound;
import me.udnek.coreu.mgu.game.MGUGameType;
import org.bukkit.plugin.Plugin;

@org.jspecify.annotations.NullMarked public class CustomRegistries{
    public static final CustomRegistry<CustomRegistry<?>> REGISTRY;
    public static final CustomRegistry<CustomItem> ITEM;
    public static final CustomRegistry<CustomBlockType> BLOCK_TYPE;
    public static final CustomRegistry<CustomAttribute> ATTRIBUTE;
    public static final CustomRegistry<CustomEquipmentSlot> EQUIPMENT_SLOT;
    public static final CustomRegistry<CustomComponentType<?, ?>> COMPONENT_TYPE;
    public static final CustomRegistry<CustomEntityType> ENTITY_TYPE;
    public static final CustomRegistry<CustomEffect> EFFECT;
    public static final CustomRegistry<CustomEnchantment> ENCHANTMENT;
    public static final CustomRegistry<CustomSound> SOUND;
    public static final CustomRegistry<CustomRecipeType<?>> RECIPE_TYPE;
    public static final CustomRegistry<MGUGameType> MGU_GAME_TYPE;

    static {
        REGISTRY = new MappedCustomRegistry<>("registry");
        addRegistry(REGISTRY);

        COMPONENT_TYPE = addRegistry(new MappedCustomRegistry<>("component_type"));
        ITEM = addRegistry(new MappedCustomRegistry<>("item"));
        ENCHANTMENT = addRegistry(new MappedCustomRegistry<>("enchantment"));
        BLOCK_TYPE = addRegistry(new MappedCustomRegistry<>("block_type"));
        ATTRIBUTE = addRegistry(new MappedCustomRegistry<>("attribute"));
        EQUIPMENT_SLOT = addRegistry(new MappedCustomRegistry<>("equipment_slot"));
        ENTITY_TYPE = addRegistry(new MappedCustomRegistry<>("entity_type"));
        EFFECT = addRegistry(new MappedCustomRegistry<>("effect"));
        SOUND = addRegistry(new MappedCustomRegistry<>("sound"));
        RECIPE_TYPE = addRegistry(new MappedCustomRegistry<>("recipe_type"));
        MGU_GAME_TYPE = addRegistry(new MappedCustomRegistry<>("mgu_game_type"));
    }

    public static <T extends Registrable> CustomRegistry<T> addRegistry(Plugin plugin, CustomRegistry<T> registry){
        REGISTRY.register(plugin, registry);
        return registry;
    }

    private static <T extends Registrable> CustomRegistry<T> addRegistry(CustomRegistry<T> registry){
        return addRegistry(CoreU.getInstance(), registry);
    }
}
