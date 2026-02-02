package me.udnek.coreu.custom.effect;

import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;


@NullMarked
public class VanillaEffectManager {

    private static VanillaEffectManager instance;

    private final Map<PotionEffectType, CustomEffect> vanillaToReplaced = new HashMap<>();

    public static VanillaEffectManager getInstance() {
        if (instance == null) instance = new VanillaEffectManager();
        return instance;
    }

    private VanillaEffectManager(){}

    public CustomEffect replace(PotionEffectType vanilla){
        CustomEffect replaced = getReplaced(vanilla);
        if (replaced != null) return replaced;
        replaced = new VanillaBasedEffect(vanilla);
        vanillaToReplaced.put(vanilla, replaced);
        return replaced;
    }

    public @Nullable CustomEffect getReplaced(PotionEffectType vanilla){
        return vanillaToReplaced.getOrDefault(vanilla, null);
    }

}
