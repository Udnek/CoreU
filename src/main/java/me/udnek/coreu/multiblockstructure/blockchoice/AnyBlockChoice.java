package me.udnek.coreu.multiblockstructure.blockchoice;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jspecify.annotations.NullMarked;

@NullMarked public class AnyBlockChoice implements BlockChoice{

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

