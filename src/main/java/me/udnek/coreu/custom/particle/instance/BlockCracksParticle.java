package me.udnek.coreu.custom.particle.instance;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.particle.ConstructableCustomParticle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Range;

@org.jspecify.annotations.NullMarked public class BlockCracksParticle extends ConstructableCustomParticle<ItemDisplay>{

    protected int strength;

    public BlockCracksParticle(@Range(from = 0, to = 9) int strength){
        this.strength = strength;
    }

    @Override
    public @Positive int getFramesAmount() {return 2;}

    @Override
    public EntityType getType() {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    protected void nextFrame() {}

    @Override
    protected void spawn() {
        super.spawn();
        ItemStack itemStack = new ItemStack(Material.GUNPOWDER);
        itemStack.setData(DataComponentTypes.ITEM_MODEL, new NamespacedKey(CoreU.getInstance(), "destroy_stage/"+strength));
        display.setItemStack(itemStack);
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(1.001);
        display.setTransformation(transformation);
    }
}
