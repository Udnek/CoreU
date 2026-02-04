package me.udnek.coreu.bootstrap;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.PluginInitializerManager;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.udnek.coreu.custom.attribute.ClearAttributeCommand;
import me.udnek.coreu.custom.attribute.CustomAttributeCommand;
import me.udnek.coreu.custom.effect.CustomEffectCommand;
import me.udnek.coreu.custom.entitylike.block.command.LoadedCustomBlocksCommand;
import me.udnek.coreu.custom.entitylike.block.command.SetCustomBlockCommand;
import me.udnek.coreu.custom.entitylike.entity.command.LoadedCustomEntitiesCommand;
import me.udnek.coreu.custom.entitylike.entity.command.SummonCustomEntityCommand;
import me.udnek.coreu.custom.equipment.CurrentEquipmentCommand;
import me.udnek.coreu.custom.help.CustomHelpCommand;
import me.udnek.coreu.custom.inventory.InventoryInspectionCommand;
import me.udnek.coreu.custom.item.CustomItemGiveCommand;
import me.udnek.coreu.custom.sound.CustomSoundCommand;
import me.udnek.coreu.mgu.command.MGUCommand;
import me.udnek.coreu.resourcepack.ResourcePackCommand;
import me.udnek.coreu.util.ResetCooldownCommand;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@org.jspecify.annotations.NullMarked
public class Bootstrap implements PluginBootstrap{

    @Override
    public void bootstrap(BootstrapContext context) {
        final LifecycleEventManager<BootstrapContext> lifecycleManager = context.getLifecycleManager();

        // COMMANDS
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            registrar.register("helpu", List.of("coreu_help", "?", "h"), CustomHelpCommand.getInstance());
            registrar.register("giveu", List.of("coreu_give"), new CustomItemGiveCommand());
            registrar.register("summonu", List.of("coreu_summon"), new SummonCustomEntityCommand());
            registrar.register("custom_entities", List.of(), new LoadedCustomEntitiesCommand());
            registrar.register("resourcepacku", List.of("coreu_resourcepack"), new ResourcePackCommand());
            registrar.register("attributeu", List.of("coreu_attribute"), new CustomAttributeCommand());
            registrar.register("clear_attribute_modifiers", List.of(), new ClearAttributeCommand());
            registrar.register("set_blocku", List.of("coreu_set_block"), new SetCustomBlockCommand());
            registrar.register("custom_block_entities", List.of(), new LoadedCustomBlocksCommand());
            registrar.register("play_soundu", List.of("coreu_play_sound"), new CustomSoundCommand());
            registrar.register("effectu", List.of("coreu_effect"), new CustomEffectCommand());
            registrar.register("mgu", List.of("coreu_minigame"), new MGUCommand());
            registrar.register("inventory_inspection", List.of(), new InventoryInspectionCommand());
            registrar.register("current_equipment", List.of(), new CurrentEquipmentCommand());
            registrar.register("reset_cooldown", List.of(), new ResetCooldownCommand());
        });

        // DATAPACKS
        lifecycleManager.registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY, (event) -> {
            Path pluginsPath = PluginInitializerManager.instance().pluginDirectoryPath().toAbsolutePath();
            Path extractPath = pluginsPath.resolve("CoreU/extracted_datapacks");

            if (Bukkit.getServer() == null){
                // EXTRACTING
                try {
                    Files.createDirectories(extractPath);
                    FileUtils.cleanDirectory(extractPath.toFile());

                    try (Stream<Path> stream = Files.list(pluginsPath)){
                        Iterator<Path> iterator = stream.iterator();
                        while (iterator.hasNext()){
                            Path path = iterator.next().toAbsolutePath();
                            // System.out.println("FILE PATH: " + path);
                            File file = path.toFile();
                            if (!file.isFile()) continue;
                            if (!file.getName().endsWith(".jar")) continue;
                            JarFile jarFile = new JarFile(file);
                            if (jarFile.getEntry("datapacks") == null) continue;
                            context.getLogger().info("found jar with datapacks: " + path);
                            Path localExtractPath = extractPath.resolve(file.getName().replace(".jar", "/"));
                            // System.out.println("localExtr: " + localExtractPath);
                            try {
                                extractDatapack(localExtractPath, jarFile);
                            } finally {
                                jarFile.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                // REGISTERING
                try (Stream<Path> datapacksPathsStream = Files.list(extractPath)) {
                    datapacksPathsStream.forEach(datapacksPath -> {
                        try (Stream<Path> entries = Files.list(datapacksPath)) {
                            entries.forEach(datapackPath -> {
                                try {
                                    event.registrar().discoverPack(datapackPath, datapackPath.getFileName().toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void extractDatapack(Path extractPath, JarFile jarFile) throws Exception {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            if (!entry.getName().startsWith("datapacks/")) continue;

            Path path = extractPath.resolve(entry.getName().replaceFirst("datapacks/", ""));
            Files.createDirectories(path);
            Files.copy(jarFile.getInputStream(entry), path, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
