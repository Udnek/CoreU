package me.udnek.coreu.rpgu.lore;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.lore.ability.PassiveAbilityLorePart;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public class AttributesLorePart implements LoreBuilder.Componentable, PassiveAbilityLorePart {

    protected HashMap<CustomEquipmentSlot, Simple> attributeData = new HashMap<>();
    protected HashMap<CustomEquipmentSlot, Simple> passiveData = new HashMap<>();
    protected CustomEquipmentSlot abilitySlot;

    @Override
    public void toComponents(@NotNull Consumer<Component> consumer) {
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
    public void addAttribute(@NotNull CustomEquipmentSlot slot, @NotNull Component component){
        addLine(slot, AttributeLoreGenerator.addTab(component), false, Position.ATTRIBUTE);
    }

    public void addFullDescription(@NotNull CustomEquipmentSlot slot, @NotNull CustomItem customItem, int linesAmount){
        for (int i = 0; i < linesAmount; i++) addDescription(slot, customItem, i);
    }
    public void addDescription(@NotNull CustomEquipmentSlot slot, @NotNull CustomItem customItem, int line){
        addDescription(slot, customItem.translationKey(), line);
    }
    public void addDescription(@NotNull CustomEquipmentSlot slot, @NotNull String rawItemName, int line){
        addLine(slot, AttributeLoreGenerator.addTab(Component.translatable(rawItemName + ".description." + line)).color(CustomAttribute.PLUS_COLOR), false, Position.ATTRIBUTE);
    }
    

    public void addLine(@NotNull CustomEquipmentSlot slot, @NotNull Component component, boolean asFirst, @NotNull Position position){
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


    public void remove(@NotNull CustomEquipmentSlot slot){
        attributeData.remove(slot);
    }
    public @Nullable LoreBuilder.Componentable get(@NotNull CustomEquipmentSlot slot){
        return attributeData.get(slot);
    }
    @Override
    public boolean isEmpty() {return attributeData.isEmpty() && passiveData.isEmpty();}
    @Override
    @Deprecated
    public void add(@NotNull Component component) {
        throw new RuntimeException("Can not use add on AttributesLorePart");
    }
    @Override
    @Deprecated
    public void addFirst(@NotNull Component component) {
        throw new RuntimeException("Can not use add on AttributesLorePart");
    }

    @Override
    public void setHeader(@NotNull Component component) {
        addLine(abilitySlot,  AttributeLoreGenerator.addTab(component.color(PASSIVE_HEADER_COLOR).decoration(TextDecoration.ITALIC, false)), true, Position.PASSIVE);
    }

    @Override
    public void addAbilityStat(@NotNull Component component) {
        addWithAbilityFormat(component.color(PASSIVE_STATS_COLOR));
    }

    @Override
    public void addAbilityStatDoubleTab(@NotNull Component component) {
        addAbilityStat(AttributeLoreGenerator.addTab(component));
    }

    @Override
    public void addAbilityDescription(@NotNull Component component) {
        addWithAbilityFormat(component.color(PASSIVE_DESCRIPTION_COLOR));
    }

    @Override
    public void addAbilityDescription(@NotNull String rawItemName, int line) {
        addAbilityDescription(Component.translatable(rawItemName + ".passive_ability." + line));
    }

    @Override
    public void addWithAbilityFormat(@NotNull Component component) {
        addLine(abilitySlot, AttributeLoreGenerator.addTab(AttributeLoreGenerator.addTab(component)), false, Position.PASSIVE);
    }

    @Override
    public void setEquipmentSlot(@NotNull CustomEquipmentSlot slot) {
        abilitySlot = slot;
    }

    public enum Position{
        ATTRIBUTE,
        PASSIVE
    }
}
