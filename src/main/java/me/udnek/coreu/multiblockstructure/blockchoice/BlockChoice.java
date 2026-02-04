package me.udnek.coreu.multiblockstructure.blockchoice;

import org.bukkit.Material;
import org.bukkit.block.Block;

@org.jspecify.annotations.NullMarked public  interface BlockChoice{

    boolean isAppropriate(Block block);
    Material getExample();
}
