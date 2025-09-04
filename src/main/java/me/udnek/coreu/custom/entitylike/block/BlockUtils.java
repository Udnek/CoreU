package me.udnek.coreu.custom.entitylike.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class BlockUtils {

    public static void safeSet(@NotNull Block block, @NotNull BlockData blockData, boolean updatePhysics){
        CustomBlockType custom = CustomBlockType.get(block);
        if (custom != null) custom.destroy(block.getLocation());
        block.setBlockData(blockData, updatePhysics);
    }

    public static void safeSet(@NotNull Block block, @NotNull BlockData blockData){
        safeSet(block, blockData, true);
    }

    public static void safeSet(@NotNull Block block, @NotNull Material material, boolean updatePhysics){
        safeSet(block, material.createBlockData(), updatePhysics);
    }

    public static void safeSet(@NotNull Block block, @NotNull Material material){
        safeSet(block, material, true);
    }
}
