package me.udnek.coreu.custom.attribute;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked public abstract class AbstractAttributeContainer<Attribute, Modifier, Self extends AbstractAttributeContainer<Attribute, Modifier, ?>>{

    protected final HashMap<Attribute, List<Modifier>> attributes = new HashMap<>();

    public List<Modifier> get(@NonNull Attribute attribute) {
        return attributes.get(attribute);
    }
    public Map<Attribute, List<Modifier>> getAll(){
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
    protected void add(@NonNull Attribute attribute, @NonNull Modifier modifier){
        List<Modifier> modifiers = attributes.get(attribute);
        if (modifiers == null){
            modifiers = new ArrayList<>();
            attributes.put(attribute, modifiers);
        }
        modifiers.add(modifier);
    }

}
