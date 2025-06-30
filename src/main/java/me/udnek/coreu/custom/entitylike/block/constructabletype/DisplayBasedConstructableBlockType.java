package me.udnek.coreu.custom.entitylike.block.constructabletype;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import me.udnek.coreu.custom.particle.instance.BlockCracksParticle;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class DisplayBasedConstructableBlockType extends AbstactCustomBlockType {

    public static final BlockState DEFAULT_FAKE_STATE = Material.BARRIER.createBlockData().createBlockState();
    public static final TileState DEFAULT_REAL_STATE;
    
    static {
        ShulkerBox state = (ShulkerBox) Material.SHULKER_BOX.createBlockData().createBlockState();
        ItemStack lock = new ItemStack(Material.COMMAND_BLOCK);
        lock.setData(DataComponentTypes.ITEM_NAME, Component.text("LOCK"));
        state.setLockItem(lock);
        DEFAULT_REAL_STATE = state;
    }

    public abstract @NotNull ItemStack getFakeDisplay();

    @Override
    public @NotNull ItemStack getParticleBase() {return getFakeDisplay();}

    @Override
    public @NotNull TileState getRealState() {
        return DEFAULT_REAL_STATE;
    }

    @Override
    public @Nullable BlockState getFakeState() {return DEFAULT_FAKE_STATE;}

    @Override
    public void place(@NotNull Location location, @NotNull CustomBlockPlaceContext context) {
        super.place(location, context);
        placeAndReturnDisplay(location, context);
    }

    public @NotNull ItemDisplay placeAndReturnDisplay(@NotNull Location location, @NotNull CustomBlockPlaceContext context){
        Location displayLocation = location.toCenterLocation();
        displayLocation.setYaw(0);
        displayLocation.setPitch(0);
        ItemDisplay entity = (ItemDisplay) location.getWorld().spawnEntity(displayLocation, EntityType.ITEM_DISPLAY);
        entity.setItemStack(getFakeDisplay());
        if (doSlightlyBiggerModel()){
            Transformation transformation = entity.getTransformation();
            transformation.getScale().set(1.00001);
            entity.setTransformation(transformation);
        }
        return entity;
    }

    public boolean doSlightlyBiggerModel(){
        BlockState fakeState = getFakeState();
        if (fakeState == null) return true;
        return fakeState.getType() != Material.BARRIER;
    }

    @Override
    public void onGenericDestroy(@NotNull Block block) {
        super.onGenericDestroy(block);
        getDisplay(block).remove();
    }


    public @NotNull ItemDisplay getDisplay(@NotNull Block block){
        Collection<Entity> displays = block.getWorld().getNearbyEntities(block.getLocation().toCenterLocation(), 0.05, 0.05, 0.05);
        for (Entity display : displays) {
            if (display.getType() == EntityType.ITEM_DISPLAY) return (ItemDisplay) display;
        }
        throw new RuntimeException("can not find display entity for block: " + block);
    }

    @Override
    public void onDamage(@NotNull BlockDamageEvent event) {}

    @Override
    public void customBreakTickProgress(@NotNull Block block, @NotNull Player player, float progress) {
        super.customBreakTickProgress(block, player, progress);
        Location centerLocation = block.getLocation().toCenterLocation();
        new BlockCracksParticle((int) (progress*9)).play(centerLocation);
    }

    @Override
    public boolean doCustomBreakTimeAndAnimation() {
        BlockState fakeState = getFakeState();
        if (fakeState == null) return false;
        return fakeState.getType() == Material.BARRIER;
    }
}
