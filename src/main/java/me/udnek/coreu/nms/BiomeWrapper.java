package me.udnek.coreu.nms;

import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BiomeWrapper implements NmsWrapper<Biome>{

    private final Biome biome;

    public BiomeWrapper(Biome biome){
        this.biome = biome;
    }

    public int skyColor(){
        return getAttribute(EnvironmentAttributes.SKY_COLOR);
    }
    public int waterColor(){
        return biome.getWaterColor();
    }
    public int foliageColor(){
        return biome.getFoliageColor();
    }
    public int dryFoliageColor(){
        return biome.getDryFoliageColor();
    }
    public int grassColor(int x, int z){
        return biome.getGrassColor(x, z);
    }

    @SuppressWarnings("SameParameterValue")
    private <T> T getAttribute(EnvironmentAttribute<T> attribute){
        return biome.getAttributes().applyModifier(attribute, attribute.defaultValue());
    }

    @Override
    public Biome getNms() {
        return biome;
    }
}
