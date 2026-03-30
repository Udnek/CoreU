package me.udnek.coreu.custom.component.instance;

import io.papermc.paper.event.player.PlayerPickBlockEvent;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.item.CustomItem;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MiddleClickableBlock extends MiddleClickable implements CustomComponent<CustomBlockType>{

    public static final MiddleClickableBlock DEFAULT = new MiddleClickableBlock();

    public void onMiddleClick(CustomBlockType block, PlayerPickBlockEvent event){
        CustomItem item = block.getItem();
        if (item == null) return;
        onMiddleClick(item.getItem(), event);
    }

    @Override
    public final CustomComponentType<CustomBlockType, ? extends CustomComponent<CustomBlockType>> getType(){
        return CustomComponentType.MIDDLE_CLICKABLE_BLOCK;
    }
}
