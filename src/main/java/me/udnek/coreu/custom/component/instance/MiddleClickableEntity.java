package me.udnek.coreu.custom.component.instance;

import io.papermc.paper.event.player.PlayerPickEntityEvent;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MiddleClickableEntity extends MiddleClickable implements CustomComponent<CustomEntityType>{

    public static final MiddleClickableEntity DEFAULT = new  MiddleClickableEntity(null);

    private final @Nullable ItemStack item;

    public MiddleClickableEntity(@Nullable ItemStack item){
        this.item = item;
    }

    public void onMiddleClick(CustomEntityType entity, PlayerPickEntityEvent event){
        if (item == null) return;
        onMiddleClick(item, event);
    }

    @Override
    public CustomComponentType<? super CustomEntityType, ? extends CustomComponent<? super CustomEntityType>> getType(){
        return CustomComponentType.MIDDLE_CLICKABLE_ENTITY;
    }
}
