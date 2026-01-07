package me.udnek.coreu.nms.structure;

import me.udnek.coreu.nms.NmsWrapper;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class BoundingBoxTypeWrapper implements NmsWrapper<StructureSpawnOverride.BoundingBoxType> {

    public static final BoundingBoxTypeWrapper PIECE = new BoundingBoxTypeWrapper(StructureSpawnOverride.BoundingBoxType.PIECE);
    public static final BoundingBoxTypeWrapper FULL = new BoundingBoxTypeWrapper(StructureSpawnOverride.BoundingBoxType.STRUCTURE);

    private final StructureSpawnOverride.@NotNull BoundingBoxType boxType;

    public BoundingBoxTypeWrapper(@NotNull StructureSpawnOverride.BoundingBoxType boxType) {
        this.boxType = boxType;
    }

    @Override
    public @NotNull StructureSpawnOverride.BoundingBoxType getNms() {
        return boxType;
    }
}
