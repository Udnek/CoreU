package me.udnek.coreu.rpgu.attribute;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.attribute.ConstructableCustomAttribute;
import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.registry.CustomRegistries;

@org.jspecify.annotations.NullMarked public class RPGUAttributes{

    public static final CustomAttribute ABILITY_CAST_RANGE = register(new ConstructableCustomAttribute("rpgu_ability_cast_range",1,0, 1024));
    public static final CustomAttribute ABILITY_COOLDOWN_TIME = register(new ConstructableCustomAttribute("rpgu_ability_cooldown_time",1,0, 1024, false));
    public static final CustomAttribute ABILITY_AREA_OF_EFFECT = register(new ConstructableCustomAttribute("rpgu_ability_area_of_effect",1,0, 1024));
    public static final CustomAttribute ABILITY_DURATION = register(new ConstructableCustomAttribute("rpgu_ability_duration",1,0, 1024));


    private static CustomAttribute register(CustomAttribute customAttributeType){
        return CustomRegistries.ATTRIBUTE.register(CoreU.getInstance(), customAttributeType);
    }

}
