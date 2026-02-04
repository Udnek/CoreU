package me.udnek.coreu.nms.structure;

import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.util.Reflex;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import org.bukkit.craftbukkit.entity.CraftEntityType;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class StructureSpawnOverrideWrapper implements NmsWrapper<StructureSpawnOverride>{

    public static StructureSpawnOverrideWrapper of(BoundingBoxTypeWrapper box){
        return new StructureSpawnOverrideWrapper(new StructureSpawnOverride(box.getNms(), WeightedList.of()));
    }

    protected final StructureSpawnOverride override;

    public StructureSpawnOverrideWrapper(StructureSpawnOverride override){
        this.override = override;
    }

    @Override
    public StructureSpawnOverride getNms() {
        return override;
    }

    public void setBoundingBoxType(BoundingBoxTypeWrapper boxType){
        Reflex.setRecordFieldValue(override, "boundingBox", boxType.getNms());
    }

    public void setSpawns(List<SpawnEntry> spawns){
        List<Weighted<MobSpawnSettings.SpawnerData>> nmsSpawns =
                spawns.stream().map(e -> {
                    MobSpawnSettings.SpawnerData spawnerData = new MobSpawnSettings.SpawnerData(
                            CraftEntityType.bukkitToMinecraft(e.entityType()),
                            e.min,
                            e.max
                    );
                    return new Weighted<>(spawnerData, e.weight);
                }).toList();
        WeightedList<MobSpawnSettings.SpawnerData> weightedList = WeightedList.of(nmsSpawns);
        Reflex.setRecordFieldValue(override, "spawns", weightedList);
    }

    public void addSpawn(SpawnEntry entry){
        ArrayList<SpawnEntry> spawns = new ArrayList<>(getSpawns());
        spawns.add(entry);
        setSpawns(spawns);
    }


    public List<SpawnEntry> getSpawns(){
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

    public record SpawnEntry(int weight, org.bukkit.entity.EntityType entityType, int min, int max){}
}
