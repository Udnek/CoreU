package me.udnek.coreu.custom.particle;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.UnknownNullability;

@org.jspecify.annotations.NullMarked public abstract class CustomFlatParticle extends ConstructableCustomParticle<ItemDisplay>{
    protected @UnknownNullability ItemStack displayItem;

    protected abstract NamespacedKey getCurrentModelPath();

    @Override
    public EntityType getType() {
        return EntityType.ITEM_DISPLAY;
    }

    protected void updateModel(){
        displayItem.setData(DataComponentTypes.ITEM_MODEL, getCurrentModelPath());
    }

    protected void nextFrame(){
        updateModel();
        display.setItemStack(displayItem);
    }

    public void setStartTransformation() {
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(getScale(), getScale(), 0);
        display.setTransformation(transformation);
    }

    abstract public double getScale();

    protected abstract ItemStack createDisplayItem();

    @Override
    protected void spawn() {
        super.spawn();
        displayItem = createDisplayItem();
        display.setBillboard(Display.Billboard.CENTER);
        display.setItemStack(createDisplayItem());
        setStartTransformation();
    }
}
