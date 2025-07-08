package me.udnek.coreu.custom.entitylike.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockUtils {

    public static void safeSet(@NotNull Block block, @NotNull Material material, boolean updatePhysics){
        CustomBlockType custom = CustomBlockType.get(block);
        if (custom != null) custom.destroy(block.getLocation());
        block.setType(material, updatePhysics);
    }

    public static void safeSet(@NotNull Block block, @NotNull Material material){
        safeSet(block, material, true);
    }
}
