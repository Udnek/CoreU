package me.udnek.coreu.rpgu.lore;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.lore.ability.PassiveAbilityLorePart;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class AttributesLorePart implements LoreBuilder.Componentable, PassiveAbilityLorePart{

    protected HashMap<CustomEquipmentSlot, Simple> attributeData = new HashMap<>();
    protected HashMap<CustomEquipmentSlot, Simple> passiveData = new HashMap<>();
    protected CustomEquipmentSlot abilitySlot;

    @Override
    public void toComponents(Consumer<Component> consumer) {
        if (isEmpty()) return;
        SortedMap<CustomEquipmentSlot, Simple> sorted = new TreeMap<>((o1, o2) -> {
            if (o1 == CustomEquipmentSlot.MAIN_HAND) return -1;
            if (o2 == CustomEquipmentSlot.MAIN_HAND) return 1;
            return Integer.compare(o1.getId().hashCode(), o2.getId().hashCode());
        });
        if (!attributeData.isEmpty() && !passiveData.isEmpty()){
            addLine(abilitySlot, Component.empty(), true, Position.PASSIVE);
        }
        for (Map.Entry<CustomEquipmentSlot, Simple> entry : passiveData.entrySet()) {
            Simple simple = entry.getValue();
            simple.toComponents(component -> addLine(entry.getKey(), component, false, Position.ATTRIBUTE));
        }

        sorted.putAll(attributeData);

        for (Map.Entry<CustomEquipmentSlot, Simple> entry : sorted.entrySet()) {
            consumer.accept(Component.empty());
            Simple componentable = entry.getValue();
            CustomEquipmentSlot slot = entry.getKey();

            consumer.accept(AttributeLoreGenerator.getHeader(slot));
            componentable.toComponents(consumer);
        }
    }
    public void addAttribute(CustomEquipmentSlot slot, Component component){
        addLine(slot, AttributeLoreGenerator.addTab(component), false, Position.ATTRIBUTE);
    }

    public void addFullDescription(CustomEquipmentSlot slot, CustomItem customItem, int linesAmount){
        for (int i = 0; i < linesAmount; i++) addDescription(slot, customItem, i);
    }
    public void addDescription(CustomEquipmentSlot slot, CustomItem customItem, int line){
        addDescription(slot, customItem.translationKey(), line);
    }
    public void addDescription(CustomEquipmentSlot slot, String rawItemName, int line){
        addLine(slot, AttributeLoreGenerator.addTab(Component.translatable(rawItemName + ".description." + line)).color(CustomAttribute.PLUS_COLOR), false, Position.ATTRIBUTE);
    }


    public void addLine(CustomEquipmentSlot slot, Component component, boolean asFirst, Position position){
        if (position == Position.ATTRIBUTE){
            Simple componentable = attributeData.getOrDefault(slot, new Simple());
            if (asFirst) componentable.addFirst(component);
            else componentable.add(component);
            attributeData.put(slot, componentable);
        } else {
            Simple componentable = passiveData.getOrDefault(slot, new Simple());
            if (asFirst) componentable.addFirst(component);
            else componentable.add(component);
            passiveData.put(slot, componentable);
        }
    }


    public @Nullable LoreBuilder.Componentable get(CustomEquipmentSlot slot){
        return attributeData.get(slot);
    }
    @Override
    public boolean isEmpty() {return attributeData.isEmpty() && passiveData.isEmpty();}

    @Override
    @Deprecated
    public void add(Component component) {
        throw new RuntimeException("Can not use add on AttributesLorePart");
    }
    @Override
    @Deprecated
    public void addFirst(Component component) {
        throw new RuntimeException("Can not use add on AttributesLorePart");
    }

    @Override
    public void setHeader(Component component) {
        addLine(abilitySlot,  AttributeLoreGenerator.addTab(component.color(PASSIVE_HEADER_COLOR).decoration(TextDecoration.ITALIC, false)), true, Position.PASSIVE);
    }

    @Override
    public void addAbilityProperty(Component component) {
        addWithAbilityFormat(component.color(PASSIVE_STATS_COLOR));
    }

    @Override
    public void addAbilityPropertyDoubleTab(Component component) {
        addAbilityProperty(AttributeLoreGenerator.addTab(component));
    }

    @Override
    public void addAbilityDescription(Component component) {
        addWithAbilityFormat(component.color(PASSIVE_DESCRIPTION_COLOR));
    }

    public void addWithAbilityFormat(Component component) {
        addLine(abilitySlot, AttributeLoreGenerator.addTab(AttributeLoreGenerator.addTab(component)), false, Position.PASSIVE);
    }

    @Override
    public void setEquipmentSlot(CustomEquipmentSlot slot) {
        abilitySlot = slot;
    }

    public enum Position{
        ATTRIBUTE,
        PASSIVE
    }
}
