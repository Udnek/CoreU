package me.udnek.coreu.nms.structure;

import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.util.Reflex;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StructureSpawnOverrideWrapper implements NmsWrapper<StructureSpawnOverride> {

    public static @NotNull StructureSpawnOverrideWrapper of(@NotNull BoundingBoxTypeWrapper box){
        return new StructureSpawnOverrideWrapper(new StructureSpawnOverride(box.getNms(), WeightedList.of()));
    }

    @NotNull
    protected final StructureSpawnOverride override;

    public StructureSpawnOverrideWrapper(@NotNull StructureSpawnOverride override){
        this.override = override;
    }

    @Override
    public @NotNull StructureSpawnOverride getNms() {
        return override;
    }

    public void setBoundingBoxType(@NotNull BoundingBoxTypeWrapper boxType){
        Reflex.setRecordFieldValue(override, "boundingBox", boxType.getNms());
    }

    public void setSpawns(@NotNull List<@NotNull SpawnEntry> spawns){
        List<Weighted<MobSpawnSettings.@NotNull SpawnerData>> nmsSpawns =
                spawns.stream().map(e -> {
                    MobSpawnSettings.SpawnerData spawnerData = new MobSpawnSettings.SpawnerData(
                            CraftEntityType.bukkitToMinecraft(e.entityType()),
                            e.min,
                            e.max
                    );
                    return new Weighted<>(spawnerData, e.weight);
                }).toList();
        WeightedList<MobSpawnSettings.@NotNull SpawnerData> weightedList = WeightedList.of(nmsSpawns);
        Reflex.setRecordFieldValue(override, "spawns", weightedList);
    }

    public void addSpawn(@NotNull SpawnEntry entry){
        ArrayList<@NotNull SpawnEntry> spawns = new ArrayList<>(getSpawns());
        spawns.add(entry);
        setSpawns(spawns);
    }


    public @NotNull List<@NotNull SpawnEntry> getSpawns(){
        return override.spawns().unwrap().stream().map(w -> {
            int weight = w.weight();
            MobSpawnSettings.SpawnerData spawnerData = w.value();
            return new SpawnEntry(weight,
                    CraftEntityType.minecraftToBukkit(spawnerData.type()),
                    spawnerData.minCount(),
                    spawnerData.maxCount()
            );
        }).toList();
    }

    public record SpawnEntry(int weight, @NotNull org.bukkit.entity.EntityType entityType, int min, int max){}
}
