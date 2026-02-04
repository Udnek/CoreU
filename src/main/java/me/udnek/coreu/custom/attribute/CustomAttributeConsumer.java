package me.udnek.coreu.custom.attribute;

import java.util.function.BiConsumer;

@org.jspecify.annotations.NullMarked public  interface CustomAttributeConsumer extends BiConsumer<CustomAttribute, CustomAttributeModifier>{
    void accept(CustomAttribute attribute, CustomAttributeModifier modifier);
}
