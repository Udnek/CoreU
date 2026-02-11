package me.udnek.coreu.nms;

import com.mojang.datafixers.util.Either;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.util.Reflex;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@NullMarked
public class NmsUtils{

    // CHAT
    public static Component toNmsComponent(@Nullable net.kyori.adventure.text.Component component){
        if (component == null) component = net.kyori.adventure.text.Component.empty();
        return CraftChatMessage.fromJSON(JSONComponentSerializer.json().serialize(component));
    }

    // REGISTRY
    public static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> registry){
        return MinecraftServer.getServer().registryAccess().lookup(registry).orElseThrow();
    }

    public static Identifier toNms(Key key){
        return Objects.requireNonNull(Identifier.tryBuild(key.namespace(), key.value()));
    }

    public static <T> ResourceKey<T> toNmsResourceKey(ResourceKey<Registry<T>> registry, Key key){
        return ResourceKey.create(registry, toNms(key));
    }

    public static <T> Holder<T> toNms(ResourceKey<Registry<T>> registry, Keyed keyed){
        return toNms(registry, keyed.key());
    }

    public static <T> Holder<T> toNms(ResourceKey<Registry<T>> registry, Key key){
        return getRegistry(registry).get(toNms(key)).orElseThrow();
    }

    public static <T> HolderSet<T> toNms(ResourceKey<Registry<T>> registryKey, Set<Key> keys){
        Registry<T> registry = getRegistry(registryKey);
        List<Holder.Reference<T>> holders = keys.stream()
                .map(id -> registry.get(toNms(id)).orElseThrow()).toList();
//        List<Holder.Reference<T>> holders = keys.stream()
//                .map(id -> Holder.Reference.createStandAlone(
//                        registry, ResourceKey.create(registryKey, NmsUtils.toNmsIdentifier(id)))).toList();

        return HolderSet.direct(holders);
    }

    public static <T> void modifyRegistry(ResourceKey<Registry<T>> registryKey, BiConsumer<Registry<T>, Map<TagKey<T>, HolderSet.Named<T>>> consumer){
        Registry<T> registry = getRegistry(registryKey);

        Reflex.setFieldValue(registry, "frozen", false);

        Map<TagKey<T>, HolderSet.Named<T>> frozenTags = Reflex.getFieldValue(registry, "frozenTags");
        Object allTags = Reflex.getFieldValue(registry, "allTags");

        // 'unfreezing'
        Class<?> tagSetClass;
        try {tagSetClass = Class.forName("net.minecraft.core.MappedRegistry$TagSet");
        } catch (ClassNotFoundException e) {throw new RuntimeException(e);}
        Method unboundMethod = Reflex.getMethod(tagSetClass, "unbound");
        Object unboundTags = Reflex.invokeMethod(null, unboundMethod);
        Reflex.setFieldValue(registry, "allTags", unboundTags);

        // modifying
        consumer.accept(registry, frozenTags);

        // freezing
        registry.freeze();
    }

    public static <T> void addValueToTag(ResourceKey<Registry<T>> registryKey,
                                         TagKey<T> tagKey,
                                         @NotNull T value)
    {
        modifyRegistry(registryKey, (registry, tags) -> {
            HolderSet.Named<T> holders = tags.get(tagKey);
            @Nullable List<Holder<T>> contents = Reflex.getFieldValue(holders, "contents");
            if (contents == null){
                contents = new ArrayList<>();
            } else {
                contents = new ArrayList<>(contents);
            }
            contents.add(registry.wrapAsHolder(value));
            Reflex.setFieldValue(holders, "contents", contents);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Holder<T> registerInIntrusiveRegistry(ResourceKey<Registry<T>> registryKey, @NotNull T object, Key key){
        final Holder<T>[] holder = new Holder[1];
        modifyRegistry(registryKey, (registry, tags) -> {
            // this shit makes me not able to register sometimes
            Reflex.setFieldValue(registry, "unregisteredIntrusiveHolders", new IdentityHashMap<T, Holder.Reference<T>>());
            holder[0] = Registry.registerForHolder(registry, toNms(key), object);
        });
        return holder[0];
    }

    @SuppressWarnings("unchecked")
    public static <T> Holder<T> registerInRegistry(ResourceKey<Registry<T>> registryKey, @NotNull T object, Key key){
        final Holder<T>[] holder = new Holder[1];
        modifyRegistry(registryKey, (registry, allTags) -> {
            holder[0] = Registry.registerForHolder(registry, toNms(key), object);
        });
        return holder[0];
    }

    public static <T> HolderSet<T> createHolderSet(ResourceKey<Registry<T>> registry, @Nullable Iterable<? extends Keyed> keys){
        if (keys == null) return HolderSet.direct();
        List<Holder<T>> list = new ArrayList<>();
        for (Keyed key : keys) {
            list.add(toNms(registry, key));
        }
        return HolderSet.direct(list);
    }

    // ITEM
    public static net.minecraft.world.item.ItemStack toNmsItemStack(@Nullable ItemStack itemStack){
        return CraftItemStack.asNMSCopy(itemStack);
    }
    public static Item toNmsMaterial(Material material){
        return CraftMagicNumbers.getItem(material);
    }
    public static Block toNmsBlock(Material material){
        return CraftMagicNumbers.getBlock(material);
    }
    public static ItemStack toBukkitItemStack(net.minecraft.world.item.ItemStack itemStack){
        return CraftItemStack.asBukkitCopy(itemStack);
    }
    // ENTITY
    public static Entity toNmsEntity(org.bukkit.entity.Entity entity){
        return ((CraftEntity) entity).getHandle();
    }
    public static LivingEntity toNmsEntity(org.bukkit.entity.LivingEntity entity){
        return ((CraftLivingEntity) entity).getHandle();
    }
    public static ServerPlayer toNmsPlayer(Player player){
        return ((CraftPlayer) player).getHandle();
    }
    public static void sendPacket(Player player, Packet<ClientGamePacketListener> packet){
        toNmsPlayer(player).connection.send(packet);
    }
    // WORLD
    public static Direction toNmsDirection(BlockFace face){
        return switch (face){
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
            case SOUTH -> Direction.SOUTH;
            case NORTH -> Direction.NORTH;
            default -> throw new RuntimeException("BlockFace does not match any of Nms: " + face);
        };
    }

    public static ServerLevel toNmsWorld(World world){
        return ((CraftWorld) world).getHandle();
    }

    public static BlockPos toNmsBlockPos(org.bukkit.block.Block block){
        return new BlockPos(block.getX(), block.getY(), block.getZ());
    }

    public static BlockState toNmsBlockState(org.bukkit.block.BlockState blockState){
        return ((CraftBlockState) blockState).getHandle();
    }

    public static Location fromNmsBlockPos(@Nullable World world, BlockPos blockPos){
        return new Location(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static DedicatedPlayerList getNmsServer(){
        return ((CraftServer) Bukkit.getServer()).getHandle();
    }

    // Attribute
    public static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation toNmsOperation(AttributeModifier.Operation bukkit){
        return switch (bukkit){
            case ADD_NUMBER -> net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
            case ADD_SCALAR -> net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case MULTIPLY_SCALAR_1 -> net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        };
    }

    public static float averageFromNumberProvider(NumberProvider provider, float fallback){
        return switch (provider) {
            case ConstantValue(float value) -> value;
            case UniformGenerator generator ->
                    (averageFromNumberProvider(Reflex.getFieldValue(generator, "max"), 1)
                            + averageFromNumberProvider(Reflex.getFieldValue(generator, "min"), 1)) / 2f;
            case BinomialDistributionGenerator(NumberProvider n, NumberProvider p) ->
                    averageFromNumberProvider(n, 1) * averageFromNumberProvider(p, 1);
            default -> fallback;
        };
    }
    ///////////////////////////////////////////////////////////////////////////

    public static Holder<Enchantment> registerEnchantment(Enchantment enchantment, Key key){
        return registerInRegistry(Registries.ENCHANTMENT, enchantment, key);
    }
    ///////////////////////////////////////////////////////////////////////////

    public static Holder<MobEffect> registerEffect(MobEffect effect, Key key){
        return registerInRegistry(Registries.MOB_EFFECT, effect, key);
    }

    public static MobEffectInstance toNmsEffect(PotionEffect effect){
        return new MobEffectInstance(toNms(Registries.MOB_EFFECT, effect.getType()),
                effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
    }
    ///////////////////////////////////////////////////////////////////////////
    public static LootTable toNmsLootTable(org.bukkit.loot.LootTable lootTable){
        return ((CraftLootTable) lootTable).getHandle();
    }
    public static List<LootPool> getPools(LootTable lootTable){
        return Reflex.getFieldValue(lootTable, NmsFields.POOLS);
    }
    public static LootPoolEntry getEntry(LootPoolSingletonContainer container){
        return Reflex.getFieldValue(container, "entry");
    }
    public static List<LootPoolEntryContainer> getEntries(LootPool lootPool){
        return Reflex.getFieldValue(lootPool, NmsFields.ENTRIES);
    }
    // containers
    public static List<LootPoolSingletonContainer> getAllSingletonContainers(LootTable lootTable){
        List<LootPoolSingletonContainer> containers = new ArrayList<>();
        NmsUtils.getAllSingletonContainers(lootTable, containers::add);
        return containers;
    }
    public static void getAllSingletonContainers(LootPoolEntryContainer container, Consumer<LootPoolSingletonContainer> consumer){
        if (container instanceof NestedLootTable){
            LootTable lootTable;
            Either<ResourceKey<LootTable>, LootTable> either = Reflex.getFieldValue(container, NmsFields.CONTENTS);
            if (either.left() != null && either.left().isPresent()){
                lootTable = getLootTable(either.left().get());
            } else lootTable = either.right().get();

            getAllSingletonContainers(lootTable, consumer);
        }
        else if (container instanceof LootPoolSingletonContainer singletonContainer) {
            consumer.accept(singletonContainer);
        } else {
            List<LootPoolEntryContainer> childrenContainers = Reflex.getFieldValue(container, NmsFields.CHILDREN);
            childrenContainers.forEach(container1 -> getAllSingletonContainers(container1, consumer));
        }
    }
    public static void getAllSingletonContainers(LootPool lootPool, Consumer<LootPoolSingletonContainer> consumer){
        List<LootPoolEntryContainer> containers = Reflex.getFieldValue(lootPool, NmsFields.ENTRIES);
        containers.forEach(container -> getAllSingletonContainers(container, consumer));
    }
    public static void getAllSingletonContainers(LootTable lootTable, Consumer<LootPoolSingletonContainer> consumer){
        List<LootPool> lootPools = Reflex.getFieldValue(lootTable, NmsFields.POOLS);
        lootPools.forEach(lootPool -> getAllSingletonContainers(lootPool, consumer));
    }
    // possible loot
    public static void getPossibleLoot(LootTable lootTable, Consumer<net.minecraft.world.item.ItemStack> consumer){
        getAllSingletonContainers(lootTable, container -> getPossibleLoot(container, consumer));
    }

    public static void getPossibleLoot(LootPoolSingletonContainer container, Consumer<net.minecraft.world.item.ItemStack> consumer){
        if (container instanceof LootItem){
            Item item = Reflex.<Holder<Item>>getFieldValue(container, "item").value();
            if (item == Items.MAP){
                List<LootItemFunction> functions = Reflex.getFieldValue(container, "functions");
                boolean containsMapFunction = functions.stream().anyMatch(function -> function instanceof ExplorationMapFunction);
                if (containsMapFunction){
                    net.minecraft.world.item.ItemStack map = new net.minecraft.world.item.ItemStack(Items.FILLED_MAP);
                    functions.forEach(f -> {
                        if (f instanceof ExplorationMapFunction) return;
                        f.apply(map, Nms.get().getGenericLootContext());
                    });
                    consumer.accept(map);
                    return;
                }
            }
        }
        getPossibleLoot(getEntry(container), consumer);
    }
    public static void getPossibleLoot(LootPoolEntry entry, Consumer<net.minecraft.world.item.ItemStack> consumer){
        entry.createItemStack(itemStack -> {
            if (itemStack.getCount() <= 0) itemStack.setCount(1);
            consumer.accept(itemStack);
        }, Nms.get().getGenericLootContext());
    }
    // misc
    public static @Nullable LootPool getLootPoolByPredicate(LootTable lootTable, Predicate<net.minecraft.world.item.ItemStack> predicate){
        List<LootPool> pools = getPools(lootTable);
        AtomicBoolean found = new AtomicBoolean(false);
        for (LootPool pool : pools) {
            found.set(false);
            List<LootPoolSingletonContainer> containers = new ArrayList<>();
            getAllSingletonContainers(pool, containers::add);
            containers.forEach(container -> getPossibleLoot(container, itemStack -> {
                if (predicate.test(itemStack)) found.set(true);
            }));
            if (found.get()) return pool;
        }
        return null;
    }

    public static @Nullable LootPoolSingletonContainer getSingletonContainerByPredicate(LootTable lootTable, Predicate<net.minecraft.world.item.ItemStack> predicate){
        List<LootPoolSingletonContainer> containers = getAllSingletonContainers(lootTable);
        AtomicBoolean found = new AtomicBoolean(false);
        for (LootPoolSingletonContainer container : containers) {
            getPossibleLoot(container, itemStack -> {
                if (predicate.test(itemStack)) found.set(true);
            });
            if (found.get()) return container;
        }
        return null;
    }
    public static ResourceKey<LootTable> getResourceKeyLootTable(String id){
        Identifier resourceLocation = Identifier.parse(id);
        return ResourceKey.create(Registries.LOOT_TABLE, resourceLocation);
    }
    public static @Nullable LootTable getLootTable(ResourceKey<LootTable> key){
        ReloadableServerRegistries.Holder registries = MinecraftServer.getServer().reloadableRegistries();
        return registries.lookup().lookup(Registries.LOOT_TABLE).flatMap((registryLookup) -> registryLookup.get(key)).map(Holder::value).orElse(null);
    }
}














