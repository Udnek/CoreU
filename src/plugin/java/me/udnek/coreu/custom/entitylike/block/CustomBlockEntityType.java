package me.udnek.coreu.custom.entitylike.block;

import me.udnek.coreu.custom.entitylike.EntityLikeTickingType;
import org.bukkit.block.TileState;

@org.jspecify.annotations.NullMarked public  interface CustomBlockEntityType extends EntityLikeTickingType<TileState, CustomBlockEntity>, CustomBlockType{

}
