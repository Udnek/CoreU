package me.udnek.coreu.nms.structure;

import me.udnek.coreu.nms.NmsWrapper;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;

@org.jspecify.annotations.NullMarked
public final class BoundingBoxTypeWrapper implements NmsWrapper<StructureSpawnOverride.BoundingBoxType>{

    public static final BoundingBoxTypeWrapper PIECE = new BoundingBoxTypeWrapper(StructureSpawnOverride.BoundingBoxType.PIECE);
    public static final BoundingBoxTypeWrapper FULL = new BoundingBoxTypeWrapper(StructureSpawnOverride.BoundingBoxType.STRUCTURE);

    private final StructureSpawnOverride.BoundingBoxType boxType;

    public BoundingBoxTypeWrapper(StructureSpawnOverride.BoundingBoxType boxType) {
        this.boxType = boxType;
    }

    @Override
    public StructureSpawnOverride.BoundingBoxType getNms() {
        return boxType;
    }
}
