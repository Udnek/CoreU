package me.udnek.coreu.custom.entitylike.block.constructabletype;

import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public abstract class RotatableCustomBlockType extends DisplayBasedConstructableBlockType {

    protected static final String FACING_DATA_KEY = "face";

    @Override
    public @NotNull ItemDisplay placeAndReturnDisplay(@NotNull Location location, @NotNull CustomBlockPlaceContext context) {
        ItemDisplay display = super.placeAndReturnDisplay(location, context);
        setFacing(location.getBlock(), chooseFacing(location, context));
        return display;
    }

    public @NotNull BlockFace chooseFacing(@NotNull Location location, @NotNull CustomBlockPlaceContext context){
        if (context.player() == null) return loadData(getState(location), FACING_DATA_KEY, string -> {
            if (string == null) return BlockFace.NORTH;
            return BlockFace.valueOf(string);
        });
        return context.player().getFacing().getOppositeFace();
    }

    public void setFacing(@NotNull Block block, @NotNull BlockFace facing){
        storeData((TileState) block.getState(), FACING_DATA_KEY, facing.name());
        ItemDisplay display = getDisplay(block);
        if (display == null) return;
        display.teleport(display.getLocation().setDirection(facing.getDirection()));
    }
}
