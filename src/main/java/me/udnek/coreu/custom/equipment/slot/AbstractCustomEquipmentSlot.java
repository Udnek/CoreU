package me.udnek.coreu.custom.equipment.slot;

import me.udnek.coreu.custom.registry.AbstractRegistrable;
import org.jspecify.annotations.NullMarked;

@NullMarked public abstract class AbstractCustomEquipmentSlot extends AbstractRegistrable implements CustomEquipmentSlot{
    protected final String rawId;

    public AbstractCustomEquipmentSlot(String id){
        this.rawId = id;
    }
    @Override
    public String getRawId() {
        return rawId;
    }
}
