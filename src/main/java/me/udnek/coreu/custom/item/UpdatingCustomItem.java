package me.udnek.coreu.custom.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.papermc.paper.datacomponent.DataComponentTypes.*;

@org.jspecify.annotations.NullMarked public  interface UpdatingCustomItem extends CustomItem{

    default boolean isUpdateMaterial(){return true;}

    default void getComponentsToUpdate(ComponentConsumer consumer){
        consumer.accept(DataComponentTypes.MAX_DAMAGE);
        consumer.accept(DataComponentTypes.CONSUMABLE);
        consumer.accept(DataComponentTypes.RARITY);
        consumer.accept(DataComponentTypes.LORE);
        consumer.accept(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        consumer.accept(DataComponentTypes.FOOD);
        consumer.accept(DataComponentTypes.ITEM_MODEL);
        consumer.accept(DataComponentTypes.REPAIRABLE);
        consumer.accept(DataComponentTypes.ITEM_NAME);
        consumer.accept(DataComponentTypes.EQUIPPABLE);
        consumer.accept(DataComponentTypes.MAX_STACK_SIZE);
        consumer.accept(DataComponentTypes.RECIPES);
        consumer.accept(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        consumer.accept(DataComponentTypes.USE_REMAINDER);
        consumer.accept(DataComponentTypes.TOOLTIP_DISPLAY);
        consumer.accept(DataComponentTypes.DAMAGE_RESISTANT);
    }

    @Override
    default ItemStack update(ItemStack itemStack) {
        ItemStack relevantItem = getItem();
        if (isUpdateMaterial()) itemStack = itemStack.withType(relevantItem.getType());
        @NotNull ItemStack finalItemStack = itemStack;
        getComponentsToUpdate(new ComponentConsumer() {
            @Override
            public <T> void accept(DataComponentType.Valued<T> type) {
                T data = relevantItem.getData(type);
                if (data == null) finalItemStack.unsetData(type);
                else finalItemStack.setData(type, data);
            }

            @Override
            public void accept(DataComponentType.NonValued type) {
                boolean relevantHas = relevantItem.hasData(type);
                boolean currentHas = finalItemStack.hasData(type);
                if (currentHas && !relevantHas) finalItemStack.unsetData(type);
                else if (!currentHas && relevantHas) finalItemStack.setData(type);
            }
        });
        return itemStack;
    }

    interface ComponentConsumer{
        <T> void accept(DataComponentType.Valued<T> type);
        void accept(DataComponentType.NonValued type);
    }
}
