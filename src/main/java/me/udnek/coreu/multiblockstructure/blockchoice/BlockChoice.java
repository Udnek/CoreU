package me.udnek.coreu.multiblockstructure.blockchoice;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jspecify.annotations.NullMarked;

@NullMarked public  interface BlockChoice{

    boolean isAppropriate(Block block);
    Material getExample();
}
