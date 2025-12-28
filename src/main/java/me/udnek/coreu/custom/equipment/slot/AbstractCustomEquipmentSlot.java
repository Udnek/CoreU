package me.udnek.coreu.custom.equipment.slot;

import me.udnek.coreu.custom.registry.AbstractRegistrable;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCustomEquipmentSlot extends AbstractRegistrable implements CustomEquipmentSlot {
    protected final String rawId;

    public AbstractCustomEquipmentSlot(@NotNull String id){
        this.rawId = id;
    }
    @NotNull
    @Override
    public String getRawId() {
        return rawId;
    }
}
