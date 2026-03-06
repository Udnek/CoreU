package me.udnek.coreu.nms;

import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BiomeWrapper implements NmsWrapper<Biome>{

    private final Biome biome;

    public BiomeWrapper(Biome biome){
        this.biome = biome;
    }

    public int waterColor(){
        return biome.getWaterColor();
    }
    public int grassColor(int x, int z){
        return biome.getGrassColor(x, z);
    }
    public int foliageColor(){
        return biome.getFoliageColor();
    }

    @Override
    public Biome getNms() {
        return biome;
    }
}
