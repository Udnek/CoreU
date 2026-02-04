package me.udnek.coreu;

import com.sun.net.httpserver.HttpServer;
import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.entitylike.block.CustomBlockManager;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityManager;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.custom.equipment.EquipmentListener;
import me.udnek.coreu.custom.equipment.PlayerEquipmentManager;
import me.udnek.coreu.custom.equipment.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.hud.CustomHudManager;
import me.udnek.coreu.custom.inventory.CustomInventoryListener;
import me.udnek.coreu.custom.item.CraftListener;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.CustomItemListener;
import me.udnek.coreu.custom.item.VanillaItemManager;
import me.udnek.coreu.custom.recipe.RecipeManager;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.CustomRegistry;
import me.udnek.coreu.custom.registry.InitializationProcess;
import me.udnek.coreu.mgu.MGUItems;
import me.udnek.coreu.nms.PacketHandler;
import me.udnek.coreu.resourcepack.ResourcePackablePlugin;
import me.udnek.coreu.resourcepack.host.RpHost;
import me.udnek.coreu.rpgu.attribute.RPGUAttributes;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.rpgu.component.ability.property.type.AttributeBasedPropertyType;
import me.udnek.coreu.serializabledata.SerializableDataManager;
import me.udnek.coreu.util.LogUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;

@SuppressWarnings("unused")
@org.jspecify.annotations.NullMarked
public final class CoreU extends JavaPlugin implements ResourcePackablePlugin{

    private static @UnknownNullability Plugin instance;
    private static @UnknownNullability HttpServer rpHost;

    public static Plugin getInstance() {
        return instance;
    }

    public static Key getKey(String value) {
        return new NamespacedKey(getInstance(), value);
    }

    @Override
    public void onEnable() {
        instance = this;

        CustomRegistry<CustomRegistry<?>> registry = CustomRegistries.REGISTRY;
        CustomItem item = MGUItems.COORDINATE_WAND;
        CustomEntityType entityType = CustomEntityType.BLOCK_DISPLAY;
        CustomEquipmentSlot.Single slot = CustomEquipmentSlot.SADDLE;
        CustomAttribute abilityDuration = RPGUAttributes.ABILITY_DURATION;
        AttributeBasedPropertyType abilityCooldownTime = RPGUComponents.ABILITY_COOLDOWN_TIME;

        // EVENTS
        new CustomItemListener(this);
        new CraftListener(this);
        new CustomInventoryListener(this);
        new EquipmentListener(this);
        PlayerEquipmentManager.getInstance().start(this);
        VanillaItemManager.getInstance();
        RecipeManager.getInstance();

        // TICKERS
        CustomEntityManager.getInstance().start(this);
        CustomBlockManager.getInstance().start(this);
        CustomHudManager.getInstance().start(this);

        PacketHandler.initialize();

        SerializableDataManager.loadConfig();
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                InitializationProcess.start();
            }
        });

        rpHost = new RpHost().start();
    }

    @Override
    public void onDisable() {
        PlayerEquipmentManager.getInstance().stop();
        rpHost.stop(0);
        LogUtils.pluginLog("Resourcepack host stopped");
    }

    @Override
    public Priority getPriority() {
        return Priority.TECHNICAL;
    }
}
