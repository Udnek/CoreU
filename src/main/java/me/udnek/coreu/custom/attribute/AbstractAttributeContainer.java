package me.udnek.coreu.custom.attribute;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAttributeContainer<Attribute, Modifier, Self extends AbstractAttributeContainer<Attribute, Modifier, ?>> {

    protected final HashMap<Attribute, List<Modifier>> attributes = new HashMap<>();

    public @NotNull List<@NotNull Modifier> get(@NotNull Attribute attribute) {
        return attributes.get(attribute);
    }
    public @NotNull Map<@NotNull Attribute, @NotNull List<@NotNull Modifier>> getAll(){
        return attributes;
    }
//    public abstract @NotNull Self get(@NotNull Predicate<@NotNull CustomEquipmentSlot> predicate);
//    public @NotNull ExactType get(@NotNull CustomEquipmentSlot targetSlot){
//        return get(slot -> slot.intersects( , targetSlot));
//    }
    // USED TO NOT DOUBLE CALCULATE
//    public @NotNull Self getExact(@NotNull CustomEquipmentSlot targetSlot){
//        return get(slot -> targetSlot == slot);
//    }
//    public boolean contains(Attribute customAttribute){
//        return attributes.containsKey(customAttribute);
//    }
    protected void add(@NotNull Attribute attribute, @NotNull Modifier modifier){
        List<Modifier> modifiers = attributes.get(attribute);
        if (modifiers == null){
            modifiers = new ArrayList<>();
            attributes.put(attribute, modifiers);
        }
        modifiers.add(modifier);
    }

}
