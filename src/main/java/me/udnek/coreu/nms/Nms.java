package me.udnek.coreu.nms;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.udnek.coreu.custom.enchantment.NmsEnchantmentContainer;
import me.udnek.coreu.nms.loot.LootContextBuilder;
import me.udnek.coreu.nms.loot.entry.NmsCustomLootEntryBuilder;
import me.udnek.coreu.nms.loot.table.NmsDefaultLootTableContainer;
import me.udnek.coreu.nms.loot.table.NmsLootTableContainer;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.util.LogUtils;
import me.udnek.coreu.util.Reflex;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
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
import org.bukkit.craftbukkit.v1_21_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftMob;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R3.map.CraftMapCursor;
import org.bukkit.craftbukkit.v1_21_R3.util.CraftLocation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class Nms {

    private LootContext genericLootContext;

    private static Nms instance;
    public static Nms get(){
        if (instance == null) instance = new Nms();
        return instance;
    }

    public @NotNull LootContext getGenericLootContext(){
        if (genericLootContext == null){
            ServerLevel serverLevel = NmsUtils.toNmsWorld(Bukkit.getWorld("world"));

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
    // ITEMS
    ///////////////////////////////////////////////////////////////////////////

    public boolean canAttackBlock(@NotNull org.bukkit.block.BlockState blockState, @NotNull Player player, @NotNull ItemStack itemStack){
        return NmsUtils.toNmsItemStack(itemStack).getItem().canAttackBlock(
                NmsUtils.toNmsBlockState(blockState), NmsUtils.toNmsWorld(blockState.getWorld()), NmsUtils.toNmsBlockPos(blockState.getBlock()), NmsUtils.toNmsPlayer(player));
    }

    public int getMaxAmountCanFitInBundle(@NotNull io.papermc.paper.datacomponent.item.BundleContents contents, @NotNull ItemStack itemStack){
        List<net.minecraft.world.item.ItemStack> nmsItems = new ArrayList<>();
        contents.contents().forEach(item -> nmsItems.add(NmsUtils.toNmsItemStack(item)));
        BundleContents.Mutable mutable = new BundleContents.Mutable(new BundleContents(nmsItems));
        Method method = Reflex.getMethod(BundleContents.Mutable.class, "getMaxAmountToAdd", net.minecraft.world.item.ItemStack.class);
        return Reflex.invokeMethod(mutable, method, NmsUtils.toNmsItemStack(itemStack));
    }

    public void triggerEnchantedItem(@NotNull Player player, @NotNull ItemStack itemStack, int levels){
        CriteriaTriggers.ENCHANTED_ITEM.trigger(NmsUtils.toNmsPlayer(player), NmsUtils.toNmsItemStack(itemStack), levels);
    }

    ///////////////////////////////////////////////////////////////////////////
    // BLOCKS
    ///////////////////////////////////////////////////////////////////////////

    public @NotNull Item simulateDropperDrop(@NotNull ItemStack itemStack, @NotNull Block block){
        return simulateDropperDrop(itemStack, block, ((Directional) block.getBlockData()).getFacing());
    }

    public @NotNull Item simulateDropperDrop(@NotNull ItemStack itemStack, @NotNull Block block, @NotNull BlockFace face){
        ServerLevel nmsWorld = NmsUtils.toNmsWorld(block.getWorld());
        BlockSource blockSource = new BlockSource(
                nmsWorld,
                NmsUtils.toNmsBlockPos(block),
                NmsUtils.toNmsBlockState(block.getState()),
                nmsWorld.getBlockEntity(NmsUtils.toNmsBlockPos(block), BlockEntityType.DISPENSER).orElse(null)
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

    public @Nullable Location findAnchorStandUpLocation(@NotNull EntityType entityType, @NotNull Location anchorLocation){
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
    public void setProjectileItemsCanFire(@NotNull Predicate<ItemStack> predicate){
        Predicate<net.minecraft.world.item.ItemStack> nmsPredicate =
                itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack));
        Reflex.setStaticFinalFieldValue(ProjectileWeaponItem.class, "ARROW_ONLY", nmsPredicate);
    }

    public record BlockPlaceResult(boolean isSuccess, @Nullable ItemStack resultingItem){
    }

    public @NotNull Nms.BlockPlaceResult placeBlockFromItem(@NotNull Player player, @Nullable ItemStack itemStack, @NotNull EquipmentSlot hand, @NotNull Location hitPos, @NotNull BlockFace blockFace, @NotNull Block clicked){
        return placeBlockFromItem(player, itemStack, hand, hitPos, blockFace, clicked, false);
    }



    public @NotNull Nms.BlockPlaceResult placeBlockFromItem(@NotNull Player player, @Nullable ItemStack itemStack, @NotNull EquipmentSlot hand, @NotNull Location hitPos, @NotNull BlockFace blockFace, @NotNull Block clicked, boolean isInside){
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

    public @Nullable ItemStack getSpawnEggByType(@NotNull EntityType type){
        net.minecraft.world.entity.EntityType<?> aClass = CraftEntityType.bukkitToMinecraft(type);
        SpawnEggItem item = SpawnEggItem.byId(aClass);
        if (item == null) return null;
        return CraftItemStack.asNewCraftStack(item);
    }

    public float getBreakProgressPerTick(@NotNull Player player, @NotNull Material material){
        BlockState state = Reflex.getFieldValue(material.createBlockData().createBlockState(), "data");
        return state.getDestroyProgress(NmsUtils.toNmsPlayer(player), null, null);
    }
    public int getBreakSpeed(@NotNull Player player, @NotNull Material material){
        return (int) (1 / getBreakProgressPerTick(player, material));
    }

    public @NotNull OptionalInt getColorByEffects(@NotNull Iterable<PotionEffect> effects){
        List<MobEffectInstance> nmsEffects = new ArrayList<>();
        for (PotionEffect effect : effects) {
            nmsEffects.add(NmsUtils.toNmsEffect(effect));
        }
        return PotionContents.getColorOptional(nmsEffects);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ENCHANTMENT
    ///////////////////////////////////////////////////////////////////////////

    public @NotNull NmsEnchantmentContainer getEnchantment(@NotNull Enchantment bukkitEnchantment){
        Registry<net.minecraft.world.item.enchantment.Enchantment> registry = NmsUtils.getRegistry(Registries.ENCHANTMENT);
        net.minecraft.world.item.enchantment.Enchantment enchantment = registry.getValue(NmsUtils.toNmsResourceLocation(bukkitEnchantment.getKey()));
        return new NmsEnchantmentContainer(enchantment);
    }


    ///////////////////////////////////////////////////////////////////////////
    // LOOT
    ///////////////////////////////////////////////////////////////////////////
    public List<MerchantRecipe> getAllPossibleTrades(){
        List<MerchantRecipe> recipes = new ArrayList<>();

        Entity entity = ((CraftEntity) Bukkit.getOnlinePlayers().toArray()[0]).getHandle();
        for (Map.Entry<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> proffesionEntry : VillagerTrades.TRADES.entrySet()) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> tradesMap = proffesionEntry.getValue();
            for (VillagerTrades.ItemListing[] itemListings : tradesMap.values()) {
                for (VillagerTrades.ItemListing itemListing : itemListings) {
                    MerchantOffer merchantOffer = itemListing.getOffer(entity, null);
                    if (merchantOffer == null) continue;
                    MerchantRecipe merchantRecipe = new MerchantRecipe(
                            NmsUtils.toBukkitItemStack(merchantOffer.getResult()),
                            merchantOffer.getMaxUses()
                    );
                    List<ItemStack> ingredients = new ArrayList<>();
                    ingredients.add(NmsUtils.toBukkitItemStack(merchantOffer.getCostA()));
                    ingredients.add(NmsUtils.toBukkitItemStack(merchantOffer.getCostB()));
                    merchantRecipe.setIngredients(ingredients);

                    recipes.add(merchantRecipe);
                }
            }
        }
        return recipes;
    }


    public @NotNull NmsLootTableContainer getLootTableContainer(@NotNull org.bukkit.loot.LootTable lootTable){
        return new NmsDefaultLootTableContainer(NmsUtils.toNmsLootTable(lootTable));
    }

    public void removeAllEntriesContains(@NotNull org.bukkit.loot.LootTable bukkitLootTable, @NotNull Predicate<ItemStack> predicate){
        replaceAllEntriesContains(bukkitLootTable, predicate, null);
    }
    public void replaceAllEntriesContains(@NotNull org.bukkit.loot.LootTable bukkitLootTable, @NotNull Predicate<ItemStack> predicate, @Nullable NmsCustomLootEntryBuilder newEntry){
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
                } else { newContainers.add(container); }

            }
            if (changed){
                LogUtils.pluginLog("Changed loot entry container from: " + lootTable.craftLootTable.getKey());
                Reflex.setFieldValue(pool, NmsFields.ENTRIES, newContainers);
            }
        }
    }

    public @Nullable org.apache.commons.lang3.tuple.Pair<Integer, Integer> getWeightAndQuality(@NotNull org.bukkit.loot.LootTable bukkitLootTable, @NotNull Predicate<ItemStack> predicate){
        LootTable lootTable = NmsUtils.toNmsLootTable(bukkitLootTable);

        final Integer[] result = {null, null};
        AtomicBoolean found = new AtomicBoolean(false);
        for (LootPoolSingletonContainer container : NmsUtils.getAllSingletonContainers(lootTable)) {
            LootPoolEntry entry = NmsUtils.getEntry(container);
            NmsUtils.getPossibleLoot(entry, itemStack -> {
                if (found.get()) return;
                if (predicate.test(NmsUtils.toBukkitItemStack(itemStack))) {
                    int weight = Reflex.getFieldValue(container, "weight");
                    int quality = Reflex.getFieldValue(container, "quality");
                    result[0] = weight;
                    result[1] = quality;
                    found.set(true);
                }
            });

            if (found.get()) return org.apache.commons.lang3.tuple.Pair.of(result[0], result[1]);
        }
        return null;
    }

    public @NotNull List<String> getRegisteredLootTableIds(){
        List<String> ids = new ArrayList<>();
        ReloadableServerRegistries.Holder registries = ((CraftServer) Bukkit.getServer()).getServer().reloadableRegistries();
        Collection<ResourceLocation> keys = registries.getKeys(Registries.LOOT_TABLE);
        for (ResourceLocation key : keys) {
            ids.add(key.toString());
        }
        return ids;
    }
    public @NotNull List<org.bukkit.loot.LootTable> getRegisteredLootTables(){
        List<org.bukkit.loot.LootTable> lootTables = new ArrayList<>();
        ReloadableServerRegistries.Holder registries = ((CraftServer) Bukkit.getServer()).getServer().reloadableRegistries();
        Collection<ResourceLocation> keys = registries.getKeys(Registries.LOOT_TABLE);
        for (ResourceLocation key : keys) {
            lootTables.add(registries.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(key.toString()))).craftLootTable);
        }
        return lootTables;
    }
    public @NotNull org.bukkit.loot.LootTable getLootTable(@NotNull String id){
        ResourceLocation resourceLocation = ResourceLocation.parse(id);
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, resourceLocation);
        return NmsUtils.getLootTable(key).craftLootTable;
    }

    public @NotNull List<ItemStack> getPossibleLoot(@NotNull org.bukkit.loot.LootTable lootTable) {
        List<ItemStack> result = new ArrayList<>();
        LootTable nmsLootTable = NmsUtils.toNmsLootTable(lootTable);
        NmsUtils.getPossibleLoot(nmsLootTable, itemStack -> result.add(NmsUtils.toBukkitItemStack(itemStack)));
        return result;
    }
    public @Nullable org.bukkit.loot.LootTable getDeathLootTable(@NotNull org.bukkit.entity.LivingEntity bukkitEntity){
        LivingEntity entity = NmsUtils.toNmsEntity(bukkitEntity);
        Optional<ResourceKey<LootTable>> lootTable = entity.getLootTable();
        if (lootTable.isEmpty()) return null;
        return NmsUtils.getLootTable(lootTable.get()).craftLootTable;
    }

    public void setDeathLootTable(@NotNull org.bukkit.entity.Mob bukkitMob, @Nullable org.bukkit.loot.LootTable lootTable){
        Mob mob = ((CraftMob) bukkitMob).getHandle();
        if (lootTable == null){
            mob.lootTable = Optional.empty();
        } else {
            mob.lootTable = Optional.of(NmsUtils.getResourceKeyLootTable(lootTable.getKey().toString()));
        }
    }

    public @NotNull List<ItemStack> populateLootTable(@NotNull org.bukkit.loot.LootTable bukkitTable, @NotNull LootContextBuilder contextBuilder){
        LootTable lootTable = NmsUtils.toNmsLootTable(bukkitTable);
        List<ItemStack> itemStacks = new ArrayList<>();
        lootTable.getRandomItems(contextBuilder.getNmsParams()).forEach(itemStack ->
                itemStacks.add(NmsUtils.toBukkitItemStack(itemStack)));
        return itemStacks;
    }

    ///////////////////////////////////////////////////////////////////////////
    // STRUCTURE
    ///////////////////////////////////////////////////////////////////////////
    public @Nullable ItemStack generateExplorerMap(Location location, org.bukkit.generator.structure.Structure bukkitStructure, int radius, boolean skipKnownStructures){
        return generateExplorerMap(location, bukkitStructure, radius, skipKnownStructures, MapCursor.Type.RED_X);
    }
    public @Nullable ItemStack generateExplorerMap(Location location, org.bukkit.generator.structure.Structure bukkitStructure, int radius, boolean skipKnownStructures, MapCursor.Type type){
        ServerLevel serverLevel = NmsUtils.toNmsWorld(location.getWorld());

        CraftStructure craftStructure = (CraftStructure) bukkitStructure;
        if (craftStructure == null){
            LogUtils.pluginLog("structure not found in registry!");
            return null;
        }
        Structure structure = craftStructure.getHandle();
        HolderSet.Direct<Structure> holders = HolderSet.direct(Holder.direct(structure));

        Pair<BlockPos, Holder<Structure>> pair =
                serverLevel.getChunkSource().getGenerator().findNearestMapStructure(
                        serverLevel, holders, CraftLocation.toBlockPosition(location), radius, skipKnownStructures);
        if (pair == null){
            LogUtils.pluginLog("structure not found in world!");
            return null;
        }

        Holder<MapDecorationType> mapDecorationTypeHolder = CraftMapCursor.CraftType.bukkitToMinecraftHolder(type);

        BlockPos structureLocation = pair.getFirst();
        net.minecraft.world.item.ItemStack mapItem = MapItem.create(serverLevel, structureLocation.getX(), structureLocation.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(serverLevel, mapItem);
        MapItemSavedData.addTargetDecoration(mapItem, structureLocation, "+", mapDecorationTypeHolder);

        return NmsUtils.toBukkitItemStack(mapItem);
    }
    public void generateBiomePreviewMap(World world, ItemStack itemStack){
        ServerLevel serverLevel = NmsUtils.toNmsWorld(world);
        MapItem.renderBiomePreviewMap(serverLevel, NmsUtils.toNmsItemStack(itemStack));
    }
    public boolean containStructure(Chunk bukitChunk, org.bukkit.generator.structure.Structure bukkitStructure){
        Structure targerStructure = ((CraftStructure) bukkitStructure).getHandle();
        ChunkAccess chunk = ((CraftChunk) bukitChunk).getHandle(ChunkStatus.FEATURES);
        return chunk.getReferencesForStructure(targerStructure) != null;
    }
    // TODO: 6/21/2024 REMOVE DEBUG 
    public boolean isInStructure(Location location, org.bukkit.generator.structure.Structure bukkitStructure, StructureStartSearchMethod method, int radius){

        long startTime = System.nanoTime();

        StructureStart startForStructure;

        if (method == StructureStartSearchMethod.LOCATE_STRUCTURE){
            StructureSearchResult searchResult = location.getWorld().locateNearestStructure(location, bukkitStructure, radius, false);
            if (searchResult == null){
                LogUtils.log("search result is null");
                return false;
            }
            Location searchLocation = searchResult.getLocation();

            Structure structure = ((CraftStructure) bukkitStructure).getHandle();

            ChunkAccess chunk = ((CraftChunk) searchLocation.getChunk()).getHandle(ChunkStatus.FULL);
            startForStructure = chunk.getStartForStructure(structure);
        }
        else {
            Structure structure = ((CraftStructure) bukkitStructure).getHandle();
            startForStructure = findStructureStartIn((NmsUtils.toNmsWorld(location.getWorld())), structure, location.getChunk().getX(), location.getChunk().getZ(), radius);
        }
        
        LogUtils.log("time taken:" + (System.nanoTime() - startTime));


        if (startForStructure == null){
            LogUtils.log("start is null");
            return false;
        }

        BlockPos position = CraftLocation.toBlockPosition(location);

        for (StructurePiece piece : startForStructure.getPieces()) {
            if (piece.getBoundingBox().isInside(position)) return true;
        }
        LogUtils.log("not in bounds");
        return false;
    }
    private StructureStart findStructureStartIn(ServerLevel world, Structure structure, int xCenter, int zCenter, int quadRadius){
        for (int x = xCenter-quadRadius; x <= xCenter+quadRadius; x++) {
            for (int z = zCenter-quadRadius; z <= zCenter+quadRadius; z++) {
                LevelChunk chunk = world.getChunk(x, z);
                StructureStart startForStructure = chunk.getStartForStructure(structure);
                if (startForStructure != null) return startForStructure;
            }
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////
    public void showDebugBlock(@NotNull Location location, int color, int time, @NotNull String name){
        for (Player player : Bukkit.getOnlinePlayers()) {
            showDebugBlock(player, location, color, time, name);
        }
    }
    public void showDebugBlock(@NotNull Player player, @NotNull Location location, int color, int time, @NotNull String name){
        Color rgb = Color.fromRGB(color);
        color = rgb.getBlue() | (rgb.getGreen() << 8) | (rgb.getRed() << 16) | (rgb.getAlpha() << 24);
        GameTestAddMarkerDebugPayload payload = new GameTestAddMarkerDebugPayload(CraftLocation.toBlockPosition(location), color, name, time * 1000/20);
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundCustomPayloadPacket(payload));
    }
    public void showDebugBlock(@NotNull Player player, @NotNull Location location, int color, int time){
        showDebugBlock(player, location, color, time, "");
    }

    ///////////////////////////////////////////////////////////////////////////
    // ENTITY
    ///////////////////////////////////////////////////////////////////////////
    public void setSpinAttack(@NotNull Player player, int ticks, float damage, @Nullable ItemStack itemStack){
        NmsUtils.toNmsPlayer(player).startAutoSpinAttack(ticks, damage, NmsUtils.toNmsItemStack(itemStack));
    }
    public void resetSpinAttack(@NotNull Player player){
        setSpinAttack(player, 0, 0, null);
    }

    public void iterateTroughCooldowns(@NotNull Player player, @NotNull TriConsumer<Key, Integer, Integer> consumer){
        for (Map.Entry<ResourceLocation, ItemCooldowns.CooldownInstance> entry : NmsUtils.toNmsPlayer(player).getCooldowns().cooldowns.entrySet()) {
            ResourceLocation location = entry.getKey();
            consumer.accept(new NamespacedKey(location.getNamespace(), location.getPath()), entry.getValue().startTime(), entry.getValue().endTime());
        }
    }

    public void sendCooldown(@NotNull Player player, @NotNull Key key, int duration){
        NmsUtils.sendPacket(player, new ClientboundCooldownPacket(NmsUtils.toNmsResourceLocation(key), duration));
    }

    public boolean mayBuild(@NotNull Player player){
        return NmsUtils.toNmsPlayer(player).mayBuild();
    }


    public void moveTo(@NotNull org.bukkit.entity.Mob follower, @NotNull Location location){
        PathNavigation followerNavigation = ((Mob) ((CraftEntity) follower).getHandle()).getNavigation();
        Path path = followerNavigation.createPath(location.getX(), location.getY(), location.getZ(), 0);
        if (path != null) followerNavigation.moveTo(path, 1D);
    }

    public void stopMoving(@NotNull org.bukkit.entity.Mob mob) {
        ((Mob) NmsUtils.toNmsEntity(mob)).getNavigation().moveTo((Path) null, 1D);
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































