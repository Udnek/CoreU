package me.udnek.coreu.multiblockstructure.blockchoice;

import org.bukkit.Material;
import org.bukkit.block.Block;

@org.jspecify.annotations.NullMarked public class MaterialBlockChoice implements BlockChoice{

    public final Material material;

    public MaterialBlockChoice(Material material){
        this.material = material;
    }

    @Override
    public boolean isAppropriate(Block block) {
        return (block.getType() == material);
    }

    @Override
    public Material getExample() {
        return material;
    }

}
