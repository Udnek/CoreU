package me.udnek.coreu.nms;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import me.udnek.coreu.util.LogUtils;
import me.udnek.coreu.util.Reflex;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
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
import net.minecraft.world.level.storage.loot.LootTable;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class NmsStructureProceeder {

    private final StructureTemplateManager structureManager;
    private final Registry<StructureTemplatePool> poolRegistry;
    private final Method getTemplateMethod;

    private final List<StructureTemplate> templates;
    private final Set<String> alreadyCheckedPoolIds;
    Collection<ResourceKey<LootTable>> lootTables;

    NmsStructureProceeder(){
        structureManager = NmsUtils.toNmsWorld(Bukkit.getWorlds().getFirst()).getStructureManager();
        RegistryAccess.Frozen registryAccess = ((CraftServer) Bukkit.getServer()).getHandle().getServer().registryAccess();
        poolRegistry = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL);
        getTemplateMethod = Reflex.getMethod(SinglePoolElement.class, "getTemplate");
        templates = new ArrayList<>();
        alreadyCheckedPoolIds = new HashSet<>();
        lootTables = new HashSet<>();
    }

    private void extractLootTables(){
        HashMap<String, ResourceKey<LootTable>> lootTables = new HashMap<>();
        for (StructureTemplate template : new HashSet<>(templates)) {
            for (StructureTemplate.Palette palette : template.palettes) {
                for (StructureTemplate.StructureBlockInfo info : palette.blocks()) {
                    CompoundTag nbt = info.nbt();
                    if (nbt == null) continue;

                    ResourceKey<LootTable> lootTable = nbt.read("LootTable", LootTable.KEY_CODEC).orElse(null);
                    if (lootTable == null){
                        // VAULT
                        Codec<VaultConfig> vaultCodec = Reflex.getFieldValue(VaultConfig.class, "CODEC");
                        VaultConfig config = nbt.read("config", vaultCodec).orElse(null);
                        if (config == null) continue;
                        lootTable = config.lootTable();
                    }
                    lootTables.put(lootTable.toString(), lootTable);
                }
            }
        }
        this.lootTables = lootTables.values();
    }

    // EXTRACTION

    public void proceed(org.bukkit.generator.structure.Structure bukkitStructure){
        Structure rawStruct = ((CraftStructure) bukkitStructure).getHandle();
        System.out.println(rawStruct);
        if (!(rawStruct instanceof JigsawStructure struct)) return;
        System.out.println("INSTANCE OF JIGSAW");

        Instant start = Instant.now();
        extractTemplatesFromPool(struct.getStartPool().value(), JigsawStructure.MAX_DEPTH);
        Duration took = Duration.between(start, Instant.now());

        for (String poolId : alreadyCheckedPoolIds) {
            System.out.println(poolId);
        }
        System.out.println("Total added: " + templates.size());
        System.out.println("Unique only: " + new HashSet<>(templates).size());
        System.out.println("Took (millis): "+  took.toMillis());

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
