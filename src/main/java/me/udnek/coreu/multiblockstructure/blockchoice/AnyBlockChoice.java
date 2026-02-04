package me.udnek.coreu.multiblockstructure.blockchoice;

import org.bukkit.Material;
import org.bukkit.block.Block;

@org.jspecify.annotations.NullMarked public class AnyBlockChoice implements BlockChoice{

    public AnyBlockChoice(){}

    @Override
    public boolean isAppropriate(Block block) {
        return true;
    }

    @Override
    public Material getExample() {
        return Material.AIR;
    }
}

