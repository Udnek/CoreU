package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.attribute.CustomKeyedAttributeModifier;
import me.udnek.coreu.custom.attribute.VanillaAttributesContainer;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.equipment.EquippableItem;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipment.universal.BaseUniversalSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.LoreProvidingItemComponent;
import me.udnek.coreu.rpgu.lore.AttributeLoreGenerator;
import me.udnek.coreu.rpgu.lore.AttributesLorePart;
import me.udnek.coreu.util.LoreBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class VanillaAttributedItem implements LoreProvidingItemComponent, EquippableItem {

    public static final VanillaAttributedItem EMPTY = new VanillaAttributedItem(){
        @Override
        public void addAttribute(@NotNull Attribute attribute, @NotNull CustomKeyedAttributeModifier modifier) {
            throwCanNotChangeDefault();
        }
    };

    protected VanillaAttributesContainer container;

    public VanillaAttributedItem(){
        this(VanillaAttributesContainer.empty());
    }

    public VanillaAttributedItem(@NotNull VanillaAttributesContainer container){
        this.container = container;
    }

    public @NotNull VanillaAttributesContainer getAttributes() {return container;}

    public void addAttribute(@NotNull Attribute attribute, @NotNull CustomKeyedAttributeModifier modifier){
        container = new VanillaAttributesContainer.Builder().add(container).add(attribute, modifier).build();
    }

    @Override
    public void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder builder) {
        LoreBuilder.Componentable componentable = builder.get(LoreBuilder.Position.ATTRIBUTES);
        AttributesLorePart attributesLorePart;
        if (componentable instanceof AttributesLorePart){
            attributesLorePart = (AttributesLorePart) componentable;
        } else {
            attributesLorePart = new AttributesLorePart();
            builder.set(LoreBuilder.Position.ATTRIBUTES, attributesLorePart);
        }

        for (Map.Entry<Attribute, List<CustomKeyedAttributeModifier>> entry : getAttributes().getAll().entrySet()) {
            Attribute attribute = entry.getKey();
            for (CustomKeyedAttributeModifier modifier : entry.getValue()) {
                if (modifier.getAmount() == 0) continue;
                CustomEquipmentSlot slot = modifier.getEquipmentSlot();
                attributesLorePart.addAttribute(slot, AttributeLoreGenerator.getAttributeLine(attribute, modifier.getAmount(), modifier.getOperation(), slot));
            }
        }

    }

    @Override
    public @NotNull CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return CustomComponentType.VANILLA_ATTRIBUTED_ITEM;
    }

    @Override
    public boolean isAppropriate(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot) {
        for (List<@NotNull CustomKeyedAttributeModifier> modifiers : container.getAll().values()) {
            for (CustomKeyedAttributeModifier modifier : modifiers) {
                if (modifier.getEquipmentSlot().intersects(player, slot)) return true;
            }
        }
        return false;
    }

    @Override
    public void onEquipped(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot) {
        container.getAll().forEach((attribute, modifiers) -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance == null) return;

            for (CustomKeyedAttributeModifier customModifier : modifiers) {
                if (!customModifier.getEquipmentSlot().intersects(player, slot)) {
                    continue;
                }

                AttributeModifier vanillaModifier = customModifier.toVanillaWitAdjustedKey(
                        "_" + customModifier.getKey().toString().replace(':', '_')
                );
                System.out.println(vanillaModifier.getKey());

                if (attributeInstance.getModifier(vanillaModifier.getKey()) == null) {
                    attributeInstance.addModifier(vanillaModifier);
                }
            }
        });
    }

    @Override
    public void onUnequipped(@NotNull CustomItem item, @NotNull Player player, @NotNull BaseUniversalSlot slot) {
        container.getAll().forEach((attribute, modifiers) -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance == null) return;

            for (CustomKeyedAttributeModifier customModifier : modifiers) {
                if (!customModifier.getEquipmentSlot().intersects(player, slot)) {
                    continue;
                }

                AttributeModifier vanillaModifier = customModifier.toVanillaWitAdjustedKey(
                        "_" + customModifier.getKey().toString().replace(':', '_')
                );
                System.out.println(vanillaModifier.getKey());

                if (attributeInstance.getModifier(vanillaModifier.getKey()) != null) {
                    attributeInstance.removeModifier(vanillaModifier);
                }
            }
        });
    }

    @Override
    public void tick(@NotNull CustomItem customItem, @NotNull Player player, @NotNull BaseUniversalSlot slot, int tickDelay) {}
}
