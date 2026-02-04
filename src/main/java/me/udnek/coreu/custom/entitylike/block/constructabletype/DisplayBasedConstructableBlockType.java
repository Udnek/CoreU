package me.udnek.coreu.custom.entitylike.block.constructabletype;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.entitylike.entity.ConstructableCustomEntityType;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.custom.item.CustomItem;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@org.jspecify.annotations.NullMarked public abstract class DisplayBasedConstructableBlockType extends AbstractCustomBlockType{

    public static final BlockState DEFAULT_FAKE_STATE = Material.BARRIER.createBlockData().createBlockState();
    public static final TileState DEFAULT_REAL_STATE;

    static {
        ShulkerBox state = (ShulkerBox) Material.SHULKER_BOX.createBlockData().createBlockState();
        ItemStack lock = new ItemStack(Material.COMMAND_BLOCK);
        lock.setData(DataComponentTypes.ITEM_NAME, Component.text("LOCK"));
        state.setLockItem(lock);
        DEFAULT_REAL_STATE = state;
    }

    @Override
    public void load(TileState tileState) {
        if (getDisplay(tileState.getBlock()) != null) return;
        placeAndReturnDisplay(tileState.getLocation(), CustomBlockPlaceContext.EMPTY);
    }

    @Override
    public void unload(TileState tileState) {}

    public ItemStack getFakeDisplay() {
        CustomItem item = getItem();
        return item != null ? item.getItem() : new ItemStack(Material.BAMBOO_PLANKS);
    }

    @Override
    public ItemStack getParticleBase() {return getFakeDisplay();}

    @Override
    public TileState getRealState() {return DEFAULT_REAL_STATE;}

    @Override
    public @Nullable BlockState getFakeState() {return DEFAULT_FAKE_STATE;}

    @Override
    protected void internalPlace(Location location, CustomBlockPlaceContext context) {
        super.internalPlace(location, context);
        placeAndReturnDisplay(location, context);
    }

    public ItemDisplay placeAndReturnDisplay(Location location, CustomBlockPlaceContext context){
        Location displayLocation = location.toCenterLocation();
        displayLocation.setYaw(0);
        displayLocation.setPitch(0);
        ItemDisplay entity = (ItemDisplay) CustomEntityType.BLOCK_DISPLAY.spawn(displayLocation);
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
    public void onGenericDestroy(Block block) {
        super.onGenericDestroy(block);
        ItemDisplay display = getDisplay(block);
        if (display != null) display.remove();
    }


    public @Nullable ItemDisplay getDisplay(Block block){
        Collection<Entity> displays = block.getWorld().getNearbyEntities(block.getLocation().toCenterLocation(), 0.5, 0.5, 0.5);
        for (Entity display : displays) {
            if (CustomEntityType.get(display) == CustomEntityType.BLOCK_DISPLAY) return (ItemDisplay) display;
        }
        // TODO REMOVE OLD
        displays = block.getWorld().getNearbyEntities(block.getLocation().toCenterLocation(), 0.05, 0.05, 0.05);
        for (Entity display : displays) {
            if (display.getType() == EntityType.ITEM_DISPLAY) return (ItemDisplay) display;
        }
        return null;
        //throw new RuntimeException("can not find display entity for block: " + block);
    }

    @Override
    public void onDamage(BlockDamageEvent event) {}

    @Override
    public void customBreakTickProgress(Block block, Player player, float progress) {
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

    public static class DisplayEntity extends ConstructableCustomEntityType<ItemDisplay>{

        @Override
        public EntityType getVanillaType() {
            return EntityType.ITEM_DISPLAY;
        }

        @Override
        public void load(Entity entity) {
            if (CustomBlockType.get(entity.getLocation().getBlock()) != null) return;
            entity.remove();
        }

        @Override
        public void unload(Entity entity) {
            if (CustomBlockType.get(entity.getLocation().getBlock()) != null) return;
            entity.remove();
        }

        @Override
        public String getRawId() {
            return "block_display";
        }
    }
}
