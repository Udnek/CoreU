package me.udnek.coreu.nms.structure;

import me.udnek.coreu.nms.MobCategoryWrapper;
import me.udnek.coreu.nms.NmsWrapper;
import me.udnek.coreu.util.Reflex;
import net.minecraft.world.entity.MobCategory;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StructureWrapper implements NmsWrapper<@NotNull Structure> {

    protected @NotNull Structure structure;

    public StructureWrapper(@NotNull Structure structure){
        this.structure = structure;
    }

    protected @NotNull Structure.StructureSettings getSettingsNms(){
        return Reflex.getFieldValue(structure, "settings");
    }

    public void editSpawnOverrides(@NotNull Function<HashMap<MobCategoryWrapper, StructureSpawnOverrideWrapper>, HashMap<MobCategoryWrapper, StructureSpawnOverrideWrapper>> edit){
        HashMap<MobCategoryWrapper, StructureSpawnOverrideWrapper> overrides = getSpawnOverrides();
        overrides = edit.apply(overrides);
        setSpawnOverrides(overrides);
    }

    public @NotNull HashMap<MobCategoryWrapper, StructureSpawnOverrideWrapper> getSpawnOverrides(){
        HashMap<MobCategoryWrapper, StructureSpawnOverrideWrapper> overrides = new HashMap<>();
        for (Map.Entry<MobCategory, StructureSpawnOverride> entry : structure.spawnOverrides().entrySet()) {
            overrides.put(new MobCategoryWrapper(entry.getKey()), new StructureSpawnOverrideWrapper(entry.getValue()));
        }
        return overrides;
    }

    public void setSpawnOverrides(@NotNull Map<MobCategoryWrapper, StructureSpawnOverrideWrapper> overrides){
        Map<MobCategory, StructureSpawnOverride> nmsOverrides = new HashMap<>();
        overrides.forEach((k, v) -> nmsOverrides.put(k.getNms(), v.getNms()));
        Reflex.setRecordFieldValue(getSettingsNms(), "spawnOverrides", nmsOverrides);
    }

    @Override
    public @NotNull Structure getNms() {
        return structure;
    }
}
