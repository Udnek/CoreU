package me.udnek.coreu.custom.item.instace;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.instance.SpawnEggItem;
import me.udnek.coreu.custom.component.instance.TranslatableThing;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.custom.item.ConstructableCustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UniversalSpawnEgg extends ConstructableCustomItem {

    public static final NamespacedKey PDC_KEY = new NamespacedKey(CoreU.getInstance(), "spawn_entity_id");

    public static ItemStack getWithEntityId(String entityId) {
        ItemStack item = Items.ABSTRACT_SPAWN_EGG.getItem();
        item.editPersistentDataContainer(container -> container.set(PDC_KEY, PersistentDataType.STRING, entityId));
        return item;
    }

    public static @Nullable CustomEntityType getEntity(ItemStack item) {
        String id = item.getPersistentDataContainer().get(PDC_KEY, PersistentDataType.STRING);
        if (id == null) return null;
        return  CustomEntityType.get(id);
    }

    @Override
    public String getRawId() {
        return "abstract_spawn_egg";
    }

    @Override
    public @Nullable TranslatableThing getTranslations() {
        return TranslatableThing.ofEngAndRu("Universal Spawn Egg", "Универсальное яйцо спавна");
    }

    @Override
    public void initializeComponents() {
        super.initializeComponents();
        getComponents().set(new UniversalSpawnEggItem());
    }

    public static class UniversalSpawnEggItem extends SpawnEggItem {
        public UniversalSpawnEggItem(){
            super();
        }

        @Override
        protected @Nullable CustomEntityType getEntityType(PlayerInteractEvent event) {
            ItemStack item = event.getItem();
            assert item != null;
            return UniversalSpawnEgg.getEntity(item);
        }
    }
}
