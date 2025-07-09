package me.udnek.coreu;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityType;
import me.udnek.coreu.mgu.MGUItems;
import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.command.MGUCommand;
import me.udnek.coreu.custom.attribute.ClearAttributeCommand;
import me.udnek.coreu.custom.attribute.CustomAttributeCommand;
import me.udnek.coreu.custom.effect.CustomEffectCommand;
import me.udnek.coreu.custom.entitylike.block.CustomBlockManager;
import me.udnek.coreu.custom.entitylike.block.command.LoadedCustomBlocksCommand;
import me.udnek.coreu.custom.entitylike.block.command.SetCustomBlockCommand;
import me.udnek.coreu.custom.entitylike.entity.CustomEntityManager;
import me.udnek.coreu.custom.entitylike.entity.command.LoadedCustomEntitiesCommand;
import me.udnek.coreu.custom.entitylike.entity.command.SummonCustomEntityCommand;
import me.udnek.coreu.custom.help.CustomHelpCommand;
import me.udnek.coreu.custom.hud.CustomHudManager;
import me.udnek.coreu.custom.inventory.CustomInventoryListener;
import me.udnek.coreu.custom.inventory.InventoryInspectionComand;
import me.udnek.coreu.custom.item.*;
import me.udnek.coreu.custom.loot.LootTableUtils;
import me.udnek.coreu.custom.recipe.RecipeManager;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.CustomRegistry;
import me.udnek.coreu.custom.registry.InitializationProcess;
import me.udnek.coreu.custom.sound.CustomSoundCommand;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import me.udnek.coreu.nms.PacketHandler;
import me.udnek.coreu.resourcepack.ResourcePackCommand;
import me.udnek.coreu.resourcepack.ResourcePackablePlugin;
import me.udnek.coreu.resourcepack.host.RpHost;
import me.udnek.coreu.resourcepack.host.RpUtils;
import me.udnek.coreu.serializabledata.SerializableDataManager;
import me.udnek.coreu.util.LogUtils;
import net.kyori.adventure.key.Key;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class CoreU extends JavaPlugin implements ResourcePackablePlugin {
    private static Plugin instance;

    public static @NotNull Plugin getInstance(){
        return instance;
    }

    public static @NotNull Key getKey(@NotNull String value){
        return new NamespacedKey(getInstance(), value);
    }

    @Override
    public void onEnable() {
        instance = this;

        CustomRegistry<CustomRegistry<?>> registry = CustomRegistries.REGISTRY;
        CustomItem item = MGUItems.COORDINATE_WAND;
        CustomEntityType entityType = CustomEntityType.BLOCK_DISPLAY;

        // EVENTS
        new CustomItemListener(this);
        new CraftListener(this);
        new CustomInventoryListener(this);
        VanillaItemManager.getInstance();
        Bukkit.getPluginManager().registerEvents(new LootTableUtils(), this);
        RecipeManager.getInstance();


        // COMMANDS
        getCommand("giveu").setExecutor(new CustomItemCommand());
        getCommand("summonu").setExecutor(new SummonCustomEntityCommand());
        getCommand("resourcepacku").setExecutor(new ResourcePackCommand());
        getCommand("helpu").setExecutor(CustomHelpCommand.getInstance());
        getCommand("attributeu").setExecutor(new CustomAttributeCommand());
        getCommand("clear_attribute_modifiers").setExecutor(new ClearAttributeCommand());
        getCommand("custom_entities").setExecutor(new LoadedCustomEntitiesCommand());
        getCommand("set_blocku").setExecutor(new SetCustomBlockCommand());
        getCommand("custom_block_entities").setExecutor(new LoadedCustomBlocksCommand());
        getCommand("play_soundu").setExecutor(new CustomSoundCommand());
        getCommand("effectu").setExecutor(new CustomEffectCommand());
        getCommand("mgu").setExecutor(new MGUCommand());
        getCommand("inventory_inspection").setExecutor(new InventoryInspectionComand(this));

        // TICKERS
        CustomEntityManager.getInstance().start(this);
        CustomBlockManager.getInstance().start(this);
        CustomHudManager.getInstance().start(this);

        PacketHandler.initialize();

        SerializableDataManager.loadConfig();
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
            public void run() {
                InitializationProcess.start();
            }
        });

        new RpHost().start();
    }

    @Override
    public void onDisable() {}

    @Override
    public @NotNull Priority getPriority() {
        return Priority.TECHNICAL;
    }
}
