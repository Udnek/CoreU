package me.udnek.coreu.custom.component;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.instance.*;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.Registrable;
import me.udnek.coreu.custom.sound.CustomSound;
import org.jetbrains.annotations.NotNull;

@org.jspecify.annotations.NullMarked public  interface CustomComponentType<HolderType, Component extends CustomComponent<HolderType>> extends Registrable{

    CustomComponentType<Object, TranslatableThing>
            TRANSLATABLE_THING = register(new ConstructableComponentType<>("translatable_thing", TranslatableThing.DEFAULT));

    CustomComponentType<CustomItem, CustomAttributedItem>
            CUSTOM_ATTRIBUTED_ITEM = register(new ConstructableComponentType<>("custom_attributed_item", CustomAttributedItem.EMPTY, CustomAttributedItem::new));

    CustomComponentType<CustomItem, RightClickableItem>
            RIGHT_CLICKABLE_ITEM = register(new ConstructableComponentType<>("right_clickable_item", RightClickableItem.EMPTY));

    CustomComponentType<CustomItem, LeftClickableItem>
            LEFT_CLICKABLE_ITEM = register(new ConstructableComponentType<>("left_clickable_item", LeftClickableItem.EMPTY));

    CustomComponentType<CustomItem, VanillaAttributedItem>
            VANILLA_ATTRIBUTED_ITEM = register(new ConstructableComponentType<>("vanilla_attributed_item", VanillaAttributedItem.EMPTY, VanillaAttributedItem::new));

    CustomComponentType<CustomItem, InventoryInteractableItem>
            INVENTORY_INTERACTABLE_ITEM = register(new ConstructableComponentType<>("inventory_interactable_item", InventoryInteractableItem.EMPTY));

    CustomComponentType<CustomItem, BlockPlacingItem>
            BLOCK_PLACING_ITEM = register(new ConstructableComponentType<>("block_placing_item", BlockPlacingItem.EMPTY));

    CustomComponentType<CustomItem, AutoGeneratingFilesItem>
            AUTO_GENERATING_FILES_ITEM = register(new ConstructableComponentType<>("auto_generating_files_item", AutoGeneratingFilesItem.GENERATED));

    CustomComponentType<CustomItem, DispensableItem>
            DISPENSABLE_ITEM = register(new ConstructableComponentType<>("dispensable_item", DispensableItem.DEFAULT));

    CustomComponentType<CustomBlockType, RightClickableBlock>
            RIGHT_CLICKABLE_BLOCK = register(new ConstructableComponentType<>("right_clickable_block", RightClickableBlock.EMPTY));

    CustomComponentType<CustomBlockType, MiddleClickableBlock>
            MIDDLE_CLICKABLE_BLOCK = register(new ConstructableComponentType<>("middle_clickable_block", MiddleClickableBlock.DEFAULT));

    CustomComponentType<CustomBlockType, HopperInteractingBlock>
            HOPPER_INTERACTING_BLOCK = register(new ConstructableComponentType<>("hopper_interacting_block", HopperInteractingBlock.DENY));

    CustomComponentType<CustomSound, AutoGeneratingFilesSound>
            AUTO_GENERATING_FILES_SOUND = register(new ConstructableComponentType<>("auto_generating_files_sound", AutoGeneratingFilesSound.DEFAULT));


    @NotNull Component getDefault();
    @NotNull Component createNewDefault();

    private static <T extends CustomComponentType<?, ?>> T register(T type){
        return CustomRegistries.COMPONENT_TYPE.register(CoreU.getInstance(), type);
    }
}
