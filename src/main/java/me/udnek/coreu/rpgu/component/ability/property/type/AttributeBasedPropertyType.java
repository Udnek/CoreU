package me.udnek.coreu.rpgu.component.ability.property.type;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.registry.AbstractRegistrable;
import me.udnek.coreu.rpgu.component.ability.RPGUItemAbility;
import me.udnek.coreu.rpgu.component.ability.property.AttributeBasedProperty;
import me.udnek.coreu.rpgu.component.ability.property.function.Modifiers;
import me.udnek.coreu.rpgu.component.ability.property.function.MultiLineDescription;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class AttributeBasedPropertyType extends AbstractRegistrable implements CustomComponentType<RPGUItemAbility<?>, AttributeBasedProperty> {

    protected AttributeBasedProperty defaultComponent;
    protected String rawId;
    protected CustomAttribute attribute;
    protected String translation;
    protected boolean divideValueBy20;

    public AttributeBasedPropertyType(@NotNull String rawId, @NotNull CustomAttribute attribute, double defaultValue, @NotNull String translation, boolean divideValueBy20) {
        this.rawId = rawId;
        this.translation = translation;
        this.divideValueBy20 = divideValueBy20;
        this.attribute = attribute;
        defaultComponent = new AttributeBasedProperty(defaultValue, this);
    }

    public AttributeBasedPropertyType(@NotNull String rawId, @NotNull CustomAttribute attribute, double defaultValue, @NotNull String translation) {
        this(rawId, attribute, defaultValue, translation, false);
    }

    public @NotNull CustomAttribute getAttribute() {return attribute;}

    public void describe(@NotNull AttributeBasedProperty attributeBasedProperty, @NotNull AbilityLorePart componentable) {
        Function<Double, Double> modifier = divideValueBy20 ? Modifiers.TICKS_TO_SECONDS() : Function.identity();
        MultiLineDescription description = attributeBasedProperty.getFunction().describeWithModifier(modifier);
        if (description.getLines().isEmpty()) return;
        componentable.addAbilityStat(Component.translatable(translation, description.getLines().getFirst()));
        @NotNull List<Component> components = description.getLines();
        for (int i = 1; i < components.size(); i++) {
            Component component = components.get(i);
            componentable.addAbilityStat(component);
        }
    }

    @Override
    public @NotNull AttributeBasedProperty getDefault() {
        return defaultComponent;
    }

    @Override
    public @NotNull AttributeBasedProperty createNewDefault() {
        return defaultComponent;
    }

    @Override
    @NotNull
    public String getRawId() {
        return rawId;
    }
}













