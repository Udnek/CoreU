package me.udnek.coreu.nms;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import me.udnek.coreu.util.LogUtils;
import me.udnek.coreu.util.Reflex;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class NmsStructureProceeder {

    private final StructureTemplateManager structureManager;
    private final Registry<StructureTemplatePool> poolRegistry;
    private final Method getTemplateMethod;
    private final Structure structure;

    private final List<StructureTemplate> templates;
    private final Set<String> alreadyCheckedPoolIds;
    //Collection<ResourceKey<LootTable>> lootTables;
    private final NamespacedKey structureId;

    NmsStructureProceeder(@NotNull NamespacedKey structureId, @NotNull Structure structure){
        this.structure = structure;
        this.structureId = structureId;
        structureManager = NmsUtils.toNmsWorld(Bukkit.getWorlds().getFirst()).getStructureManager();
        poolRegistry = NmsUtils.getRegistry(Registries.TEMPLATE_POOL);
        getTemplateMethod = Reflex.getMethod(SinglePoolElement.class, "getTemplate");
        templates = new ArrayList<>();
        alreadyCheckedPoolIds = new HashSet<>();
    }

    public @NotNull Collection<ResourceKey<LootTable>> extractLootTables(){
        Codec<VaultConfig> vaultCodec = Reflex.getFieldValue(VaultConfig.class, "CODEC");
        HashMap<String, ResourceKey<LootTable>> lootTables = new HashMap<>();
        for (StructureTemplate template : new HashSet<>(templates)) {
            for (StructureTemplate.Palette palette : template.palettes) {
                for (StructureTemplate.StructureBlockInfo info : palette.blocks()) {
                    CompoundTag nbt = info.nbt();
                    if (nbt == null) continue;

                    // ANY LOOTABLE
                    final ResourceKey<LootTable> lootTable = nbt.read("LootTable", LootTable.KEY_CODEC).orElse(null);
                    if (lootTable != null){
                        lootTables.put(lootTable.toString(), lootTable);
                        continue;
                    }

                    Block block = info.state().getBlock();
                    if (block == Blocks.VAULT){
                        VaultConfig config = nbt.read("config", vaultCodec).orElse(null);
                        if (config != null){
                            lootTables.put(config.lootTable().toString(), config.lootTable());
                        }
                    } else if (block == Blocks.TRIAL_SPAWNER){
                        for (String key : List.of("normal_config", "ominous_config")) {
                            Tag tag = nbt.get(key);
                            if (tag == null) continue;
                            TrialSpawnerConfig config;
                            if (tag.getType() == StringTag.TYPE){
                                config = nbt.read(key, NmsUtils.getRegistry(Registries.TRIAL_SPAWNER_CONFIG).byNameCodec()).orElse(null);
                            } else {
                                config = nbt.read(key, TrialSpawnerConfig.DIRECT_CODEC).orElse(null);
                            }
                            if (config == null) continue;
                            for (Weighted<ResourceKey<LootTable>> weighted : config.lootTablesToEject().unwrap()) {
                                lootTables.put(weighted.value().toString(), weighted.value());
                            }
                        }
                    }
                }
            }
        }
        return lootTables.values();
    }

    public void iterateThroughBlocks(@NotNull Function<StructureTemplate.StructureBlockInfo, Boolean> takeAndContinue){
        for (StructureTemplate template : new HashSet<>(templates)) {
            for (StructureTemplate.Palette palette : template.palettes) {
                for (StructureTemplate.StructureBlockInfo info : palette.blocks()) {
                    Boolean continue_ = takeAndContinue.apply(info);
                    if (!continue_) return;
                }
            }
        }
    }

    // EXTRACTION
    public void extractAllTemplates(){
        if (!(structure instanceof JigsawStructure structure)) return;

        Instant start = Instant.now();
        extractTemplatesFromPool(structure.getStartPool().value(), JigsawStructure.MAX_DEPTH);
        Duration took = Duration.between(start, Instant.now());

        LogUtils.pluginLog(String.format("Took (millis): %d (structure: %s)", took.toMillis(), structureId.asString()));

        extractLootTables();
    }

    private void extractTemplatesFromPool(@NotNull StructureTemplatePool pool, int depth){
        for (Pair<StructurePoolElement, Integer> pair : pool.getFallback().value().getTemplates()) {
            extractTemplatesFromElement(pair.getFirst(), depth);
        }
        for (Pair<StructurePoolElement, Integer> pair : pool.getTemplates()) {
            extractTemplatesFromElement(pair.getFirst(), depth);
        }
    }

    private void extractTemplatesFromElement(@NotNull StructurePoolElement rawElement, int depth){
        if (depth < 0) return;

        if (rawElement instanceof SinglePoolElement single){
            StructureTemplate template = Reflex.invokeMethod(single, getTemplateMethod, structureManager);
            templates.add(template);

            if (depth == 0) return;
            depth -= 1;

            for (StructureTemplate.Palette palette : template.palettes) {
                extractTemplatesFromPalette(palette, depth);
            }
        } else if (rawElement instanceof ListPoolElement list){
            for (StructurePoolElement element : list.getElements()) {
                extractTemplatesFromElement(element, depth);
            }
        }
    }

    private void extractTemplatesFromPalette(@NotNull StructureTemplate.Palette palette, int depth){
        for (StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()) {

            // NBT EXAMPLE
            // final_state:"minecraft:smooth_sandstone",
            // id:"minecraft:jigsaw",
            // joint:"aligned",
            // name:"minecraft:empty",
            // pool:"minecraft:village/desert/camel",
            // target:"minecraft:empty"

            BlockState state = blockInfo.state();
            if (state.getBlock() != Blocks.JIGSAW) continue;
            CompoundTag nbt = blockInfo.nbt();
            if (nbt == null) continue;

            ResourceKey<StructureTemplatePool> foundPoolKey = nbt.read(JigsawBlockEntity.POOL, JigsawBlockEntity.POOL_CODEC).orElse(null);
            if (foundPoolKey == null) continue;

            String id = foundPoolKey.location().toString();
            if (alreadyCheckedPoolIds.contains(id)) continue;
            alreadyCheckedPoolIds.add(id);

            StructureTemplatePool foundPool = poolRegistry.getOptional(foundPoolKey).orElse(null);
            if (foundPool == null){
                LogUtils.pluginWarning("Unknown pool found during extraction: "+foundPoolKey);
                continue;
            }

            extractTemplatesFromPool(foundPool, depth);
        }
    }
}
