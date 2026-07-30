package me.udnek.coreu.custom.attribute;

import org.jspecify.annotations.NullMarked;

import java.util.function.BiConsumer;

@NullMarked public  interface CustomAttributeConsumer extends BiConsumer<CustomAttribute, CustomAttributeModifier>{
    void accept(CustomAttribute attribute, CustomAttributeModifier modifier);
}
