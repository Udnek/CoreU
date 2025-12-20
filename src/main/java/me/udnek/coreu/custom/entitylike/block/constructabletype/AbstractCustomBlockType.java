package me.udnek.coreu.custom.entitylike.block.constructabletype;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.udnek.coreu.custom.component.instance.RightClickableBlock;
import me.udnek.coreu.custom.entitylike.block.CustomBlockManager;
import me.udnek.coreu.custom.entitylike.block.CustomBlockPlaceContext;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import me.udnek.coreu.nms.Nms;
import me.udnek.coreu.nms.loot.LootContextBuilder;
import me.udnek.coreu.util.Either;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.*;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractCustomBlockType extends AbstractRegistrableComponentable<CustomBlockType> implements CustomBlockType {

    public abstract @NotNull TileState getRealState();
    public abstract @Nullable ItemStack getParticleBase();

    public <T extends TileState> @NotNull T getState(@NotNull Location location){
        return getState(location.getBlock());
    }
    public <T extends TileState> @NotNull T getState(@NotNull Block block){
        return (T) block.getState();
    }

    @ApiStatus.Experimental
    public @Nullable SoundGroup getSoundGroup(){
        return getBreakSpeedBaseBlock().createBlockData().getSoundGroup();
    }

    public void storeData(@NotNull TileState state, @NotNull String name, @NotNull String value){
        state.getPersistentDataContainer().set(new NamespacedKey(key().namespace(), name), PersistentDataType.STRING, value);
        state.update();
    }
    public <T> @UnknownNullability T loadData(TileState state, String name, Function<@Nullable String, T> deserializer){
        return deserializer.apply(state.getPersistentDataContainer().get(new NamespacedKey(key().namespace(), name), PersistentDataType.STRING));
    }

    protected void internalPlace(@NotNull Location location, @NotNull CustomBlockPlaceContext context){
        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            location.getWorld().playSound(location.toCenterLocation(), soundGroup.getPlaceSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
        }
    }

    @Override
    public final void place(@NotNull Location location, @NotNull CustomBlockPlaceContext context){
        TileState blockState = (TileState) getRealState().copy(location);
        blockState.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.STRING, getId());
        blockState.update(true, false);
        internalPlace(location, context);
        CustomBlockManager.getInstance().loadAny(AbstractCustomBlockType.this, getState(location));
    }


    @ApiStatus.Experimental
    public @Nullable Either<LootTable, List<ItemStack>> getLoot(){
        CustomItem item = getItem();
        if (item == null) return null;
        return new Either<>(null, List.of(item.getItem()));
    }

    @Override
    public void afterInitialization() {
        CustomBlockType.super.afterInitialization();
        initializeComponents();
    }

    public abstract @NotNull Material getBreakSpeedBaseBlock();

    @Override
    public float getCustomBreakProgress(@NotNull Player player, @NotNull Block block) {
        return Nms.get().getBreakProgressPerTick(player, getBreakSpeedBaseBlock());
    }


    @Override
    public void destroy(@NotNull Location location) {
        onGenericDestroy(location.getBlock());
        location.getBlock().setType(Material.AIR);
    }

    @Override
    public void onTouchedByExplosion(@NotNull EntityExplodeEvent event, @NotNull Block block) {
        onTouchedByExplosion(event.getExplosionResult(), event.getYield(), block);
    }
    @Override
    public void onTouchedByExplosion(@NotNull BlockExplodeEvent event, @NotNull Block block) {
        onTouchedByExplosion(event.getExplosionResult(), event.getYield(), block);
    }

    protected void onTouchedByExplosion(@NotNull ExplosionResult explosionResult, float dropChance, @NotNull Block block){
        if (!(explosionResult == ExplosionResult.DESTROY || explosionResult == ExplosionResult.DESTROY_WITH_DECAY)) return;
        if (new Random().nextFloat() <= dropChance){
            // drop
            dropItems(block, ItemStack.empty());
        }
        onGenericDestroy(block);
        block.setType(Material.AIR);
    }

    @Override
    public void onDestroy(@NotNull BlockDestroyEvent event){
        event.setWillDrop(false);
        dropItems(event.getBlock(), new ItemStack(Material.AIR));
        onGenericDestroy(event.getBlock());
    }

    @MustBeInvokedByOverriders
    public void initializeComponents() {
        getComponents().set(new RightClickableBlock() {
            @Override
            public void onRightClick(@NotNull CustomBlockType customBlockType, @NotNull PlayerInteractEvent event) {
                event.setUseInteractedBlock(Event.Result.DENY);

                ItemStack item = event.getItem();
                if (item == null) return;
                if (!Nms.get().mayBuild(event.getPlayer())) return;

                Block relative = event.getClickedBlock().getRelative(event.getBlockFace());
                BlockState state = relative.getState(true);

                Nms.BlockPlaceResult placeResult = Nms.get().placeBlockFromItem(
                        event.getPlayer(),
                        event.getItem(),
                        event.getHand(),
                        event.getInteractionPoint(),
                        event.getBlockFace(),
                        event.getClickedBlock()
                );

                if (!placeResult.isSuccess()) return;

                boolean passed = new BlockPlaceEvent(
                        relative,
                        state,
                        event.getClickedBlock(),
                        item,
                        event.getPlayer(), true,
                        event.getHand()
                ).callEvent();

                if (!passed) {
                    event.getClickedBlock().setBlockData(state.getBlockData());
                } else {
                    event.getPlayer().getInventory().setItem(event.getHand(), placeResult.resultingItem());
                }
            }
        });
    }

    public void dropItems(@NotNull Block block, @NotNull ItemStack tool){
        @Nullable Either<LootTable, List<ItemStack>> loot = getLoot();
        if (loot == null) return;
        World world = block.getWorld();
        Location centerLocation = block.getLocation().toCenterLocation();
        List<ItemStack> drops = new ArrayList<>();
        loot.consumeEither(new Consumer<List<ItemStack>>() {
            @Override
            public void accept(List<ItemStack> stacks) {
                drops.addAll(stacks);
            }
        }, new Consumer<LootTable>() {
            @Override
            public void accept(LootTable lootTable) {
                List<ItemStack> stacks = Nms.get().populateLootTable(lootTable,
                        new LootContextBuilder(lootTable, world)
                                .blockState(block.getState())
                                .tool(tool)
                                .origin(centerLocation)
                );

                drops.addAll(stacks);
            }
        });

        for (ItemStack itemStack : drops) world.dropItemNaturally(centerLocation, itemStack);
    }

    @Override
    public void onDestroy(@NotNull BlockBreakEvent event){
        event.setExpToDrop(0);
        event.setDropItems(false);
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            dropItems(event.getBlock(), event.getPlayer().getInventory().getItemInMainHand());
        }
        onGenericDestroy(event.getBlock());
    }
    @Override
    public void onDestroy(@NotNull BlockBurnEvent event) {
        onGenericDestroy(event.getBlock());
    }
    @Override
    public void onDestroy(@NotNull BlockFadeEvent event) {
        onGenericDestroy(event.getBlock());
    }

    @Override
    public void customBreakTickProgress(@NotNull Block block, @NotNull Player player, float progress) {
        ItemStack particleBase = getParticleBase();
        if (particleBase == null) return;

        Location centerLocation = block.getLocation().toCenterLocation();
        ParticleBuilder builder = new ParticleBuilder(Particle.ITEM);
        builder.location(centerLocation);
        builder.data(particleBase);
        builder.count(2);
        builder.offset(0.3f, 0.3f, 0.3f);
        builder.extra(0);
        builder.force(false);
        builder.spawn();


        if (Bukkit.getCurrentTick() % 5 != 0 ) return;
        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            Location location = block.getLocation().toCenterLocation();
            location.getWorld().playSound(location, soundGroup.getHitSound(), SoundCategory.BLOCKS, soundGroup.getVolume()/5f, soundGroup.getPitch());
        }
    }

    @Override
    public void onPlayerFall(@NotNull Player player, @NotNull Location location, int particlesCount) {
        ItemStack particleBase = getParticleBase();
        if (particleBase != null){
            ParticleBuilder builder = new ParticleBuilder(Particle.ITEM);
            builder.location(location);
            builder.data(particleBase);
            builder.count(particlesCount);
            builder.offset(0f, 0f, 0f);
            builder.extra(0.15);
            builder.force(false);
            builder.receivers(player);
            builder.spawn();
        }
        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            location.getWorld().playSound(location, soundGroup.getFallSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void onGenericDestroy(@NotNull Block block){
        CustomBlockManager.getInstance().unloadAny(this, getState(block));

        ItemStack particleBase = getParticleBase();
        if (particleBase == null) return;

        Location centerLocation = block.getLocation().toCenterLocation();
        ParticleBuilder builder = new ParticleBuilder(Particle.ITEM);
        builder.location(centerLocation);
        builder.data(particleBase);
        builder.count(50);
        builder.offset(0.3f, 0.3f, 0.3f);
        builder.extra(0.07);
        builder.force(false);
        builder.spawn();

        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            Location location = block.getLocation().toCenterLocation();
            location.getWorld().playSound(location, soundGroup.getBreakSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
        }
    }
}
