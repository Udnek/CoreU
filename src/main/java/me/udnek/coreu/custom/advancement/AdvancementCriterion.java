package me.udnek.coreu.custom.advancement;

import com.google.common.base.Preconditions;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.nms.NmsUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface AdvancementCriterion extends Supplier<Criterion<?>>{

    AdvancementCriterion TICK = () -> CriteriaTriggers.TICK.createCriterion(PlayerTrigger.TriggerInstance.tick().triggerInstance());
    AdvancementCriterion IMPOSSIBLE = () -> CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance());
    InventoryChange INVENTORY_CHANGE = new InventoryChange(null, null);
    EnterBlock ENTER_BLOCK = new EnterBlock(null);


    class EnterBlock implements AdvancementCriterion{
        private final Material material;

        private EnterBlock(Material material){
            this.material = material;
        }

        public @NotNull AdvancementCriterion.EnterBlock create(@NotNull Material material){
            return new EnterBlock(material);
        }

        @Override
        public Criterion<?> get() {
            Preconditions.checkArgument(material != null,"Create criterion first!");
            return EnterBlockTrigger.TriggerInstance.entersBlock(NmsUtils.toNmsBlock(material));
        }
    }

    class InventoryChange implements AdvancementCriterion{
        private final ItemStack itemStack;
        private final Material material;

        private InventoryChange(ItemStack itemStack, Material material){
            this.itemStack = itemStack;
            this.material = material;
        }

        public @NotNull AdvancementCriterion create(@NotNull ItemStack itemStack){
            return new InventoryChange(itemStack, null);
        }

        public @NotNull AdvancementCriterion create(@NotNull Material material){
            return new InventoryChange(null, material);
        }

        @Override
        public Criterion<?> get(){
            Preconditions.checkArgument(itemStack != null || material != null,"Create criterion first!");
            ItemPredicate.Builder predicate;
            if (material != null){
                predicate = ItemPredicate.Builder.item()
                        .of(NmsUtils.getRegistry(Registries.ITEM), NmsUtils.toNmsMaterial(material));
            }
            else{
                net.minecraft.world.item.ItemStack nmsItemStack = NmsUtils.toNmsItemStack(itemStack);

                if (CustomItem.isCustom(itemStack)){
                    predicate = ItemPredicate.Builder.item()
                            .of(NmsUtils.getRegistry(Registries.ITEM), nmsItemStack.getItem())
                            .withComponents(
                                    DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.builder().expect(
                                            DataComponents.CUSTOM_DATA,
                                            nmsItemStack.getComponents().get(DataComponents.CUSTOM_DATA)
                                    ).build()).build()
                            );
                } else {
                    predicate = ItemPredicate.Builder.item()
                            .of(NmsUtils.getRegistry(Registries.ITEM), nmsItemStack.getItem())
                            .withComponents(
                                    DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.allOf(nmsItemStack.getComponents())).build());
                }
            }
            return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(
                    Optional.empty(),
                    InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                    List.of(predicate.build()))
            );
        }
    }

}
