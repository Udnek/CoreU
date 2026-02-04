package me.udnek.coreu.nms;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.PaperDialog;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import me.udnek.coreu.nms.loot.LootContextBuilder;
import me.udnek.coreu.nms.loot.entry.NmsCustomEntry;
import me.udnek.coreu.nms.loot.table.DefaultLootTableWrapper;
import me.udnek.coreu.nms.loot.table.LootTableWrapper;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.nms.structure.StructureWrapper;
import me.udnek.coreu.util.LogUtils;
import me.udnek.coreu.util.Reflex;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DialogTags;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.impl.CraftVault;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NullMarked
public class Nms {

    private static @Nullable Nms instance;

    public static Nms get(){
        if (instance == null) instance = new Nms();
        return instance;
    }

    private @Nullable LootContext genericLootContext;

    public LootContext getGenericLootContext(){
        if (genericLootContext == null){
            ServerLevel serverLevel = NmsUtils.toNmsWorld(Objects.requireNonNull(Bukkit.getWorld("world")));

            LootParams.Builder paramsBuilder = new LootParams.Builder(serverLevel);
            ContextKeySet.Builder keyBuilder = new ContextKeySet.Builder();

            keyBuilder.required(LootContextParams.ORIGIN);
            paramsBuilder.withParameter(LootContextParams.ORIGIN, new Vec3(0, 0, 0));
            paramsBuilder.withLuck(1);

            LootContext.Builder contextBuilder = new LootContext.Builder(paramsBuilder.create(keyBuilder.build()));
            genericLootContext = contextBuilder.create(Optional.empty());
        }
        return genericLootContext;
    }

    ///////////////////////////////////////////////////////////////////////////
    // DIALOGS
    ///////////////////////////////////////////////////////////////////////////

    public void addDialogToQuickActions(NamespacedKey key, Dialog dialog){
        net.minecraft.server.dialog.Dialog nmsDialog = PaperDialog.bukkitToMinecraftHolder(dialog).value();
        NmsUtils.registerInRegistry(Registries.DIALOG, nmsDialog, key);
        NmsUtils.addValueToTag(Registries.DIALOG, DialogTags.QUICK_ACTIONS, nmsDialog);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ITEMS
    ///////////////////////////////////////////////////////////////////////////

    public @Nullable DyeColor getColorByDye(Material dye){
        if (!(NmsUtils.toNmsMaterial(dye) instanceof DyeItem dyeItem)) return null;
        net.minecraft.world.item.DyeColor dyeColor = dyeItem.getDyeColor();
        return new DyeColor(){
            @Override
            public String name() {return dyeColor.name();}
            @Override
            public Color textureDiffuseColor() {return Color.fromARGB(dyeColor.getTextureDiffuseColor());}
            @Override
            public Color fireworkColor() {return Color.fromRGB(dyeColor.getFireworkColor());}
            @Override
            public Color textColor() {return Color.fromRGB(dyeColor.getTextColor());}
        };
    }

    public boolean canAttackBlock(org.bukkit.block.BlockState blockState, Player player, ItemStack itemStack){
        return NmsUtils.toNmsItemStack(itemStack).getItem().canDestroyBlock(
                NmsUtils.toNmsItemStack(itemStack),
                NmsUtils.toNmsBlockState(blockState),
                NmsUtils.toNmsWorld(blockState.getWorld()),
                NmsUtils.toNmsBlockPos(blockState.getBlock()),
                NmsUtils.toNmsPlayer(player));
    }

    public int getMaxAmountCanFitInBundle(io.papermc.paper.datacomponent.item.BundleContents contents, ItemStack itemStack){
        List<net.minecraft.world.item.ItemStack> nmsItems = new ArrayList<>();
        contents.contents().forEach(item -> nmsItems.add(NmsUtils.toNmsItemStack(item)));
        BundleContents.Mutable mutable = new BundleContents.Mutable(new BundleContents(nmsItems));
        Method method = Reflex.getMethod(BundleContents.Mutable.class, "getMaxAmountToAdd", net.minecraft.world.item.ItemStack.class);
        return Reflex.invokeMethod(mutable, method, NmsUtils.toNmsItemStack(itemStack));
    }

    public void triggerEnchantedItem(Player player, ItemStack itemStack, int levels){
        CriteriaTriggers.ENCHANTED_ITEM.trigger(NmsUtils.toNmsPlayer(player), NmsUtils.toNmsItemStack(itemStack), levels);
    }

    ///////////////////////////////////////////////////////////////////////////
    // BLOCKS
    ///////////////////////////////////////////////////////////////////////////


    private static final Method GET_LIGHT_BLOCK = Objects.requireNonNull(Reflex.getMethod(BlockBehaviour.class, "getLightBlock"));

    public int getHowMuchLightBlockBlocks(Block bukkitBlock){
        BlockBehaviour block = ((CraftBlock) bukkitBlock).getHandle().getBlockIfLoaded(NmsUtils.toNmsBlockPos(bukkitBlock));
        BlockState blockState = NmsUtils.toNmsBlockState(bukkitBlock.getState());
        return Reflex.invokeMethod(block, GET_LIGHT_BLOCK, blockState);
    }

    public Item simulateDropperDrop(ItemStack itemStack, Block block){
        return simulateDropperDrop(itemStack, block, ((Directional) block.getBlockData()).getFacing());
    }

    public Item simulateDropperDrop(ItemStack itemStack, Block block, BlockFace face){
        ServerLevel nmsWorld = NmsUtils.toNmsWorld(block.getWorld());
        BlockSource blockSource = new BlockSource(
                nmsWorld,
                NmsUtils.toNmsBlockPos(block),
                NmsUtils.toNmsBlockState(block.getState()),
                nmsWorld.getBlockEntity(NmsUtils.toNmsBlockPos(block), BlockEntityType.DISPENSER).orElseThrow()
        );

        ItemEntity nmsItemEntity = Reflex.invokeMethod(null, Reflex.getMethod(DefaultDispenseItemBehavior.class, "prepareItem"),
                nmsWorld,
                NmsUtils.toNmsItemStack(itemStack),
                Reflex.getFieldValue(DefaultDispenseItemBehavior.class, "DEFAULT_ACCURACY"),
                NmsUtils.toNmsDirection(face),
                DispenserBlock.getDispensePosition(blockSource)
        );
        nmsWorld.addFreshEntity(nmsItemEntity);
        return (Item) nmsItemEntity.getBukkitEntity();
    }

    public @Nullable Location findAnchorStandUpLocation(EntityType entityType, Location anchorLocation){
        Optional<Vec3> standUpPosition = RespawnAnchorBlock.findStandUpPosition(
                CraftEntityType.bukkitToMinecraft(entityType),
                NmsUtils.toNmsWorld(anchorLocation.getWorld()),
                NmsUtils.toNmsBlockPos(anchorLocation.getBlock())
        );
        return standUpPosition.map(vec3 -> new Location(anchorLocation.getWorld(), vec3.x, vec3.y, vec3.z)).orElse(null);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // USAGES
    ///////////////////////////////////////////////////////////////////////////

    @ApiStatus.Experimental
    public void setProjectileItemsCanFire(Predicate<ItemStack> predicate){
        Predicate<net.minecraft.world.item.ItemStack> nmsPredicate =
                itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack));
        Reflex.setStaticFinalFieldValue(ProjectileWeaponItem.class, "ARROW_ONLY", nmsPredicate);
    }

    public record BlockPlaceResult(boolean isSuccess, @Nullable ItemStack resultingItem){
    }

    public Nms.BlockPlaceResult placeBlockFromItem(Player player, @Nullable ItemStack itemStack, EquipmentSlot hand, Location hitPos, BlockFace blockFace, Block clicked){
        return placeBlockFromItem(player, itemStack, hand, hitPos, blockFace, clicked, false);
    }

    public Nms.BlockPlaceResult placeBlockFromItem(Player player, @Nullable ItemStack itemStack, EquipmentSlot hand, Location hitPos, BlockFace blockFace, Block clicked, boolean isInside){
        net.minecraft.world.item.ItemStack stack = NmsUtils.toNmsItemStack(itemStack);
        if (!(stack.getItem() instanceof BlockItem blockItem)) return new BlockPlaceResult(false, itemStack);
        InteractionHand interactionHand = switch (hand){
            case HAND -> InteractionHand.MAIN_HAND;
            case OFF_HAND -> InteractionHand.OFF_HAND;
            default -> throw new RuntimeException("Equipment slot must be hand, given: " + hand);
        };
        Direction direction = NmsUtils.toNmsDirection(blockFace);
        InteractionResult placed = blockItem.useOn(new BlockPlaceContext(
                        NmsUtils.toNmsPlayer(player),
                        interactionHand,
                        stack,
                        new BlockHitResult(
                                new Vec3(hitPos.getX(), hitPos.getY(), hitPos.getZ()),
                                direction,
                                NmsUtils.toNmsBlockPos(clicked),
                                isInside)
                )
        );
        return new BlockPlaceResult(placed instanceof InteractionResult.Success, NmsUtils.toBukkitItemStack(stack));
    }

    public @Nullable ItemStack getSpawnEggByType(EntityType type){
        net.minecraft.world.entity.EntityType<?> aClass = CraftEntityType.bukkitToMinecraft(type);
        SpawnEggItem item = SpawnEggItem.byId(aClass);
        if (item == null) return null;
        return CraftItemStack.asNewCraftStack(item);
    }

    public float getBreakProgressPerTick(Player player, Material material){
        BlockState state = Reflex.getFieldValue(material.createBlockData().createBlockState(), "data");
        return state.getDestroyProgress(NmsUtils.toNmsPlayer(player), null, null);
    }

    public int getBreakSpeed(Player player, Material material){
        return (int) (1 / getBreakProgressPerTick(player, material));
    }

    public OptionalInt getColorByEffects(Iterable<PotionEffect> effects){
        List<MobEffectInstance> nmsEffects = new ArrayList<>();
        for (PotionEffect effect : effects) {
            nmsEffects.add(NmsUtils.toNmsEffect(effect));
        }
        return PotionContents.getColorOptional(nmsEffects);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ENCHANTMENT
    ///////////////////////////////////////////////////////////////////////////

    public EnchantmentWrapper getEnchantmentWrapper(Enchantment bukkitEnchantment){
        Registry<net.minecraft.world.item.enchantment.Enchantment> registry = NmsUtils.getRegistry(Registries.ENCHANTMENT);
        net.minecraft.world.item.enchantment.Enchantment enchantment = registry.getValue(NmsUtils.toNmsIdentifier(bukkitEnchantment.getKey()));
        assert enchantment != null;
        return new EnchantmentWrapper(enchantment);
    }

    ///////////////////////////////////////////////////////////////////////////
    // LOOT
    ///////////////////////////////////////////////////////////////////////////

    public LootTableWrapper getLootTableWrapper(org.bukkit.loot.LootTable lootTable){
        return new DefaultLootTableWrapper(NmsUtils.toNmsLootTable(lootTable));
    }

    public void removeAllEntriesContains(org.bukkit.loot.LootTable bukkitLootTable, Predicate<ItemStack> predicate){
        replaceAllEntriesContains(bukkitLootTable, predicate, null);
    }

    public void replaceAllEntriesContains(org.bukkit.loot.LootTable bukkitLootTable, Predicate<ItemStack> predicate, @Nullable NmsCustomEntry.Builder newEntry){
        LootTable lootTable = NmsUtils.toNmsLootTable(bukkitLootTable);

        List<LootPoolEntryContainer> toReplace = new ArrayList<>();
        for (LootPoolSingletonContainer container : NmsUtils.getAllSingletonContainers(lootTable)) {
            LootPoolEntry entry = NmsUtils.getEntry(container);
            AtomicBoolean contains = new AtomicBoolean(false);
            NmsUtils.getPossibleLoot(entry, itemStack -> {
                if (predicate.test(NmsUtils.toBukkitItemStack(itemStack))) {
                    contains.set(true);
                }
            });
            if (contains.get()) toReplace.add(container);
        }
        for (LootPool pool : NmsUtils.getPools(lootTable)) {
            List<LootPoolEntryContainer> newContainers = new ArrayList<>();
            boolean changed = false;
            for (LootPoolEntryContainer container : NmsUtils.getEntries(pool)) {
                if (toReplace.contains(container)){
                    changed = true;
                    if (newEntry != null) newContainers.add(newEntry.build());
                } else {
                    newContainers.add(container);
                }
            }
            if (changed){
                LogUtils.pluginLog("Changed loot entry container from: " + lootTable.craftLootTable.getKey());
                Reflex.setFieldValue(pool, NmsFields.ENTRIES, newContainers);
            }
        }
    }

    public List<String> getRegisteredLootTableIds(){
        List<String> ids = new ArrayList<>();
        ReloadableServerRegistries.Holder registries = ((CraftServer) Bukkit.getServer()).getServer().reloadableRegistries();
        Stream<Identifier> keys = registries.lookup().lookup(Registries.LOOT_TABLE).get().listElementIds().map(ResourceKey::identifier);
        keys.forEach(key -> ids.add(key.toString()));
        return ids;
    }

    public List<org.bukkit.loot.LootTable> getRegisteredLootTables(){
        List<org.bukkit.loot.LootTable> lootTables = new ArrayList<>();
        ReloadableServerRegistries.Holder registries = ((CraftServer) Bukkit.getServer()).getServer().reloadableRegistries();
        Stream<Identifier> keys = registries.lookup().lookup(Registries.LOOT_TABLE).get().listElementIds().map(ResourceKey::identifier);
        keys.forEach(key ->
            lootTables.add(registries.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(key.toString()))).craftLootTable));
        return lootTables;
    }

    public @Nullable org.bukkit.loot.LootTable getLootTable(NamespacedKey id){
        Identifier resourceLocation = CraftNamespacedKey.toMinecraft(id);
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, resourceLocation);
        LootTable lootTable = NmsUtils.getLootTable(key);
        if (lootTable == null) return null;
        return lootTable.craftLootTable;
    }

    public List<ItemStack> getPossibleLoot(org.bukkit.loot.LootTable lootTable) {
        List<ItemStack> result = new ArrayList<>();
        LootTable nmsLootTable = NmsUtils.toNmsLootTable(lootTable);
        NmsUtils.getPossibleLoot(nmsLootTable, itemStack -> result.add(NmsUtils.toBukkitItemStack(itemStack)));
        return result;
    }

    @SuppressWarnings("OptionalIsPresent")
    public @Nullable org.bukkit.loot.LootTable getDeathLootTable(org.bukkit.entity.LivingEntity bukkitEntity){
        LivingEntity entity = NmsUtils.toNmsEntity(bukkitEntity);
        Optional<ResourceKey<LootTable>> lootTable = entity.getLootTable();
        if (lootTable.isEmpty()) return null;
        return Objects.requireNonNull(NmsUtils.getLootTable(lootTable.get())).craftLootTable;
    }

    public void setDeathLootTable(org.bukkit.entity.Mob bukkitMob, @Nullable org.bukkit.loot.LootTable lootTable){
        Mob mob = ((CraftMob) bukkitMob).getHandle();
        if (lootTable == null){
            mob.lootTable = Optional.empty();
        } else {
            mob.lootTable = Optional.of(NmsUtils.getResourceKeyLootTable(lootTable.getKey().toString()));
        }
    }

    public List<ItemStack> populateLootTable(org.bukkit.loot.LootTable bukkitTable, LootContextBuilder contextBuilder){
        LootTable lootTable = NmsUtils.toNmsLootTable(bukkitTable);
        List<ItemStack> itemStacks = new ArrayList<>();
        lootTable.getRandomItems(contextBuilder.getNmsParams()).forEach(itemStack ->
                itemStacks.add(NmsUtils.toBukkitItemStack(itemStack)));
        return itemStacks;
    }

    ///////////////////////////////////////////////////////////////////////////
    // STRUCTURE
    ///////////////////////////////////////////////////////////////////////////

    public @Nullable StructureWrapper getStructureWrapper(NamespacedKey structureId){
        Structure structure = NmsUtils.getRegistry(Registries.STRUCTURE).getOptional(CraftNamespacedKey.toMinecraft(structureId)).orElse(null);
        if (structure == null) return null;
        return new StructureWrapper(structure);
    }

    @SuppressWarnings("ConstantValue")
    public void getAllPossibleLootTablesInStructure(NamespacedKey structureId, Consumer<org.bukkit.loot.LootTable> bukkitLootTables){
        Structure structure = NmsUtils.getRegistry(Registries.STRUCTURE).getOptional(CraftNamespacedKey.toMinecraft(structureId)).orElse(null);
        if (structure == null) return;
        NmsStructureProceeder proceeder = new NmsStructureProceeder(structureId, structure);
        proceeder.extractAllTemplates();
        for (ResourceKey<LootTable> lootTableKey : proceeder.extractLootTables()) {
            LootTable lootTable = NmsUtils.getLootTable(lootTableKey);
            if (lootTable == null || lootTable.craftLootTable == null){
                LogUtils.pluginWarning("null lootTable: " + lootTableKey);
                continue;
            }
            bukkitLootTables.accept(lootTable.craftLootTable);
        }
    }

    public void convertContainerWithVaultInStructure(NamespacedKey structureId, Function<org.bukkit.loot.LootTable, @Nullable ItemStack> lootTableToKey){
        Codec<VaultConfig> vaultCodec = Reflex.getFieldValue(VaultConfig.class, "CODEC");
        VaultConfig DEFAULT_CONFIG = Reflex.getFieldValue(VaultConfig.class, "DEFAULT");

        Set<net.minecraft.world.level.block.Block> copperChests = new HashSet<>();
        NmsUtils.getRegistry(Registries.BLOCK).getTagOrEmpty(BlockTags.COPPER_CHESTS)
                .forEach(h -> copperChests.add(h.value()));

        modifyStructure(structureId, new Function<>() {
            @Override
            public Boolean apply(StructureTemplate.StructureBlockInfo info) {
                if (info.nbt() == null) return true;
                System.out.println(info);
                net.minecraft.world.level.block.Block blockType = info.state().getBlock();
                if (!copperChests.contains(blockType)
                        && blockType != Blocks.CHEST
                        && blockType != Blocks.TRAPPED_CHEST
                        && blockType != Blocks.BARREL
                        && blockType != Blocks.SHULKER_BOX)
                {
                    return true;
                }
                ResourceKey<LootTable> lootTableKey = info.nbt().read(ChestBlockEntity.LOOT_TABLE_TAG, LootTable.KEY_CODEC).orElse(null);
                if (lootTableKey == null) return true;
                LootTable lootTable = NmsUtils.getLootTable(lootTableKey);
                if (lootTable == null) return true;
                ItemStack key = lootTableToKey.apply(lootTable.craftLootTable);
                if (key == null) return true;

                CraftVault vaultState = (CraftVault) Material.VAULT.createBlockData();

                info.state().getOptionalValue(HorizontalDirectionalBlock.FACING).ifPresent(direction -> {
                    vaultState.setFacing(CraftBlockData.toBukkit(direction, BlockFace.class));
                });

                Reflex.setRecordFieldValue(info, "state", vaultState.getState());
                info.nbt().remove(ChestBlockEntity.LOOT_TABLE_SEED_TAG);
                info.nbt().store("config", vaultCodec, new VaultConfig(
                        lootTableKey,
                        DEFAULT_CONFIG.activationRange(),
                        DEFAULT_CONFIG.deactivationRange(),
                        NmsUtils.toNmsItemStack(key),
                        DEFAULT_CONFIG.overrideLootTableToDisplay(),
                        DEFAULT_CONFIG.playerDetector(),
                        DEFAULT_CONFIG.entitySelector()
                ));
                return true;
            }
        });
    }

    public void modifyVaultsKeysInStructure(NamespacedKey structureId, Function<ItemStack, ItemStack> oldKeyToNew){
        Codec<VaultConfig> vaultCodec = Reflex.getFieldValue(VaultConfig.class, "CODEC");
        modifyStructure(structureId, new Function<>() {
            @Override
            public Boolean apply(StructureTemplate.StructureBlockInfo info) {
                if (info.nbt() == null) return true;
                if (info.state().getBlock() != Blocks.VAULT) return true;
                VaultConfig config = info.nbt().read("config", vaultCodec).orElse(null);
                if (config == null) return true;
                net.minecraft.world.item.ItemStack keyItem = config.keyItem();
                ItemStack bukkitKey = NmsUtils.toBukkitItemStack(keyItem);
                ItemStack newBukkitKey = oldKeyToNew.apply(bukkitKey);
                if (bukkitKey == newBukkitKey) return true;

                info.nbt().store("config", vaultCodec, new VaultConfig(
                        config.lootTable(),
                        config.activationRange(),
                        config.deactivationRange(),
                        NmsUtils.toNmsItemStack(newBukkitKey),
                        config.overrideLootTableToDisplay(),
                        config.playerDetector(),
                        config.entitySelector()
                ));
                return true;
            }
        });
    }

    private void modifyStructure(NamespacedKey structureId, Function<StructureTemplate.StructureBlockInfo, Boolean> takeAndContinue){
        Structure structure = NmsUtils.getRegistry(Registries.STRUCTURE).getOptional(CraftNamespacedKey.toMinecraft(structureId)).orElse(null);
        Preconditions.checkArgument(structure != null, "Structure not found: " + structureId);
        NmsStructureProceeder proceeder = new NmsStructureProceeder(structureId, structure);
        proceeder.extractAllTemplates();
        proceeder.iterateThroughBlocks(takeAndContinue);
    }

    public @Nullable ItemStack generateExplorerMap(Location location,
                                                   Set<Key> structures,
                                                   int chunkRadius,
                                                   boolean skipKnownStructures,
                                                   MapCursor.Type icon,
                                                   byte zoom)
    {
        return generateExplorerMap(location, structures, chunkRadius, skipKnownStructures, icon, zoom, true, true);
    }

    public @Nullable ItemStack generateExplorerMap(Location location,
                                                   Set<Key> structures,
                                                   int chunkRadius,
                                                   boolean skipKnownStructures,
                                                   MapCursor.Type icon,
                                                   byte zoom,
                                                   boolean trackingPosition,
                                                   boolean unlimitedTracking)
    {
        ServerLevel level = NmsUtils.toNmsWorld(location.getWorld());

        Registry<Structure> registry = NmsUtils.getRegistry(Registries.STRUCTURE);

        List<Holder.Reference<Structure>> holders = structures.stream()
                .map(id -> Holder.Reference.createStandAlone(
                        registry, ResourceKey.create(Registries.STRUCTURE, NmsUtils.toNmsIdentifier(id)))).toList();

        HolderSet.Direct<Structure> structureSet = HolderSet.direct(holders);
        @Nullable Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator().findNearestMapStructure(
                level,
                structureSet,
                NmsUtils.toNmsBlockPos(location.getBlock()),
                chunkRadius,
                skipKnownStructures
        );
        if (result == null) return null;

        BlockPos pos = result.getFirst();

        net.minecraft.world.item.ItemStack map = MapItem.create(level, pos.getX(), pos.getZ(), zoom, trackingPosition, unlimitedTracking);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, pos, "+", CraftMapCursor.CraftType.bukkitToMinecraftHolder(icon));

        return NmsUtils.toBukkitItemStack(map);
    }

    public void generateBiomePreviewMap(World world, ItemStack map){
        ServerLevel serverLevel = NmsUtils.toNmsWorld(world);
        MapItem.renderBiomePreviewMap(serverLevel, NmsUtils.toNmsItemStack(map));
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////

    // TODO IMPLEMENT SHOW BLOCK
//    public void showDebugBlock(@NotNull Location location, int color, int time, @NotNull String name){
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            showDebugBlock(player, location, color, time, name);
//        }
//    }
//    public void showDebugBlock(@NotNull Player player, @NotNull Location location, int color, int time, @NotNull String name){
//        Color rgb = Color.fromRGB(color);
//        color = rgb.getBlue() | (rgb.getGreen() << 8) | (rgb.getRed() << 16) | (rgb.getAlpha() << 24);
//
//        ClientboundGameTestHighlightPosPacket
//        //GameTestAddMarkerDebugPayload payload = new GameTestAddMarkerDebugPayload(CraftLocation.toBlockPosition(location), color, name, time * 1000/20);
//        NmsUtils.sendPacket(player, new ClientboundCustomPayloadPacket());
//        ((CraftPlayer) player).getHandle().connection.send(new ClientboundCustomPayloadPacket(payload));
//    }
//    public void showDebugBlock(@NotNull Player player, @NotNull Location location, int color, int time){
//        showDebugBlock(player, location, color, time, "");
//    }


    ///////////////////////////////////////////////////////////////////////////
    // ENTITY
    ///////////////////////////////////////////////////////////////////////////
    public void sendBlockUpdatePacket(Player player, org.bukkit.block.BlockState blockState){
        NmsUtils.sendPacket(player, new ClientboundBlockUpdatePacket(
                new BlockPos(blockState.getX(), blockState.getY(), blockState.getZ()),
                NmsUtils.toNmsBlockState(blockState))
        );
    }

    public void setSpinAttack(Player player, int ticks, float damage, @Nullable ItemStack itemStack){
        NmsUtils.toNmsPlayer(player).startAutoSpinAttack(ticks, damage, NmsUtils.toNmsItemStack(itemStack));
    }

    public void resetSpinAttack(Player player){
        setSpinAttack(player, 0, 0, null);
    }

    public void iterateTroughCooldowns(Player player, TriConsumer<Key, Integer, Integer> keyStartEndConsumer){
        for (Map.Entry<Identifier, ItemCooldowns.CooldownInstance> entry : NmsUtils.toNmsPlayer(player).getCooldowns().cooldowns.entrySet()) {
            Identifier location = entry.getKey();
            keyStartEndConsumer.accept(new NamespacedKey(location.getNamespace(), location.getPath()), entry.getValue().startTime(), entry.getValue().endTime());
        }
    }

    public void sendCooldown(Player player, Key key, int duration){
        NmsUtils.sendPacket(player, new ClientboundCooldownPacket(NmsUtils.toNmsIdentifier(key), duration));
    }

    public boolean mayBuild(Player player){
        return NmsUtils.toNmsPlayer(player).mayBuild();
    }

    public void moveWithPathfind(org.bukkit.entity.Mob follower, Location location){
        PathNavigation followerNavigation = ((Mob) ((CraftEntity) follower).getHandle()).getNavigation();
        Path path = followerNavigation.createPath(location.getX(), location.getY(), location.getZ(), 0);
        if (path != null) followerNavigation.moveTo(path, 1D);
    }

    public void moveNaturally(Entity entity, Vector velocity){
        NmsUtils.toNmsEntity(entity).move(MoverType.SELF, CraftVector.toVec3(velocity));
    }

    public void stopMovingWithPathfind(org.bukkit.entity.Mob mob) {
        ((Mob) NmsUtils.toNmsEntity(mob)).getNavigation().moveTo((Path) null, 1D);
    }

    public void sendFakeEquipment(Player entity, Player observer, EquipmentSlot slot, @Nullable ItemStack item) {
        NmsUtils.sendPacket(observer, new ClientboundSetEquipmentPacket(
                NmsUtils.toNmsEntity(entity).getId(),
                List.of(Pair.of(CraftEquipmentSlot.getNMS(slot), NmsUtils.toNmsItemStack(item)))
                ));
    }

    public void sendFakeDestroyEntities(List<Entity> entities, Player observer) {
        var idNmsEntities = new ArrayList<Integer>();
        entities.forEach(entity ->  idNmsEntities.add(NmsUtils.toNmsEntity(entity).getId()));
        NmsUtils.sendPacket(observer, new ClientboundRemoveEntitiesPacket(new IntImmutableList(idNmsEntities)));
    }

    ///////////////////////////////////////////////////////////////////////////
    // BIOME
    ///////////////////////////////////////////////////////////////////////////

    public DownfallType getDownfallType(Location location){
        BlockPos blockPosition = CraftLocation.toBlockPosition(location);
        Biome.Precipitation precipitation = NmsUtils.toNmsWorld(location.getWorld()).getBiome(blockPosition).value().getPrecipitationAt(blockPosition, location.getWorld().getSeaLevel());
        return DownfallType.fromNMS(precipitation);
    }
}































