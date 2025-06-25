package me.udnek.coreu.rpgu.component;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.ConstructableComponentType;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.rpgu.attribute.RPGUAttributes;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.component.ability.property.CastTimeProperty;
import me.udnek.coreu.rpgu.component.ability.property.EffectsProperty;
import me.udnek.coreu.rpgu.component.ability.property.MissUsageCooldownMultiplierProperty;
import me.udnek.coreu.rpgu.component.ability.property.type.AttributeBasedPropertyType;

public class RPGUComponents {

    public static final CustomComponentType<CustomItem, RPGUActiveAbilityItem> ACTIVE_ABILITY_ITEM;
    public static final CustomComponentType<CustomItem, RPGUPassiveAbilityItem> PASSIVE_ABILITY_ITEM;

    public static final AttributeBasedPropertyType ABILITY_COOLDOWN_TIME;
    public static final AttributeBasedPropertyType ABILITY_CAST_RANGE;
    public static final AttributeBasedPropertyType ABILITY_AREA_OF_EFFECT;
    public static final AttributeBasedPropertyType ABILITY_DURATION;
    public static final CustomComponentType<RPGUItemAbility<?>, CastTimeProperty> ABILITY_CAST_TIME;
    public static final CustomComponentType<RPGUItemAbility<?>, MissUsageCooldownMultiplierProperty> ABILITY_MISS_USAGE_COOLDOWN_MULTIPLIER;
    public static final CustomComponentType<RPGUItemAbility<?>, EffectsProperty> ABILITY_EFFECTS;

    static {
        ACTIVE_ABILITY_ITEM = register(new ConstructableComponentType<>("rpgu_active_ability_item", RPGUActiveAbilityItem.DEFAULT, RPGUActiveAbilityItem::new));
        PASSIVE_ABILITY_ITEM = register(new ConstructableComponentType<>("rpgu_passive_ability_item", RPGUPassiveAbilityItem.DEFAULT, RPGUPassiveAbilityItem::new));

        ABILITY_COOLDOWN_TIME = register(new AttributeBasedPropertyType("rpgu_ability_cooldown_time", RPGUAttributes.COOLDOWN_TIME, -1, "rpgu_ability_property.coreu.cooldown_time", true));
        ABILITY_CAST_RANGE = register(new AttributeBasedPropertyType("rpgu_ability_cast_range", RPGUAttributes.CAST_RANGE, -1, "rpgu_ability_property.coreu.cast_range"));
        ABILITY_AREA_OF_EFFECT = register(new AttributeBasedPropertyType("rpgu_ability_area_of_effect", RPGUAttributes.AREA_OF_EFFECT, -1, "rpgu_ability_property.coreu.area_of_effect"));
        ABILITY_DURATION = register(new AttributeBasedPropertyType("rpgu_ability_duration", RPGUAttributes.ABILITY_DURATION, -1, "rpgu_ability_property.coreu.duration", true));
        ABILITY_CAST_TIME = register(new ConstructableComponentType<>("rpgu_ability_cast_time", new CastTimeProperty(-1)));
        ABILITY_MISS_USAGE_COOLDOWN_MULTIPLIER = register(new ConstructableComponentType<>("rpgu_ability_miss_usage_cooldown_multiplier", new MissUsageCooldownMultiplierProperty(0.3)));
        ABILITY_EFFECTS =  register(new ConstructableComponentType<>("rpgu_ability_effects", EffectsProperty.DEFAULT));
    }

    private static <T extends CustomComponentType<?, ?>> T register(T type){
        return CustomRegistries.COMPONENT_TYPE.register(CoreU.getInstance(), type);
    }
}
