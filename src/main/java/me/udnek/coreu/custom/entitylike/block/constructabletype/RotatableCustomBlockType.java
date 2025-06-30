package me.udnek.coreu.custom.entitylike.block.constructabletype;

import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class RotatableCustomBlockType extends DisplayBasedConstructableBlockType {

    @Override
    public @NotNull ItemDisplay placeAndReturnDisplay(@NotNull Location location, @NotNull CustomBlockPlaceContext context) {
        ItemDisplay display = super.placeAndReturnDisplay(location, context);
        display.teleport(display.getLocation().setDirection(chooseDirection(location, context)));
        return display;
    }

    public @NotNull Vector chooseDirection(@NotNull Location location, @NotNull CustomBlockPlaceContext context){
        if (context.player() == null) return new Vector(1, 0, 0);
        return context.player().getFacing().getOppositeFace().getDirection();
    }
}
