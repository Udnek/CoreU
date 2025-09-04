package me.udnek.coreu.mgu.component;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.ConstructableComponentType;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.jetbrains.annotations.NotNull;

public class MGUComponents {

    public static final CustomComponentType<MGUPlayer, MGUPlayerDataHolder> PLAYER_DATA =
            register(new ConstructableComponentType<>("mgu_player_data", MGUPlayerDataHolder.DEFAULT, MGUPlayerDataHolder::new));


    private static <T extends CustomComponentType<?, ?>> @NotNull T register(T type){
        return CustomRegistries.COMPONENT_TYPE.register(CoreU.getInstance(), type);
    }
}
