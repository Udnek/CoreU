package me.udnek.coreu.custom.entitylike.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

@org.jspecify.annotations.NullMarked public class BlockUtils{

    public static void safeSet(Block block, BlockData blockData, boolean updatePhysics){
        CustomBlockType custom = CustomBlockType.get(block);
        if (custom != null) custom.destroy(block.getLocation());
        block.setBlockData(blockData, updatePhysics);
    }

    public static void safeSet(Block block, BlockData blockData){
        safeSet(block, blockData, true);
    }

    public static void safeSet(Block block, Material material, boolean updatePhysics){
        safeSet(block, material.createBlockData(), updatePhysics);
    }

    public static void safeSet(Block block, Material material){
        safeSet(block, material, true);
    }
}
