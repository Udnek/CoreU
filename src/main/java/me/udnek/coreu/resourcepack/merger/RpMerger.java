package me.udnek.coreu.resourcepack.merger;

import com.google.common.base.Preconditions;
import me.udnek.coreu.custom.component.ComponentHolder;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.event.ResourcepackInitializationEvent;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.CustomRegistry;
import me.udnek.coreu.custom.registry.Registrable;
import me.udnek.coreu.custom.sound.CustomSound;
import me.udnek.coreu.resourcepack.FileType;
import me.udnek.coreu.resourcepack.ResourcePackablePlugin;
import me.udnek.coreu.resourcepack.VirtualResourcePack;
import me.udnek.coreu.resourcepack.path.RpPath;
import me.udnek.coreu.resourcepack.path.SamePathsContainer;
import me.udnek.coreu.resourcepack.path.SortedPathsContainer;
import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;
import me.udnek.coreu.util.LogUtils;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class RpMerger{

    public static final RpPath LANG_DIRECTORY = new RpPath(null, "assets/*/lang");
    public static final RpPath SOUNDS_FILE = new RpPath(null, "assets/*/sounds.json");

    private static final RpPath[] MERGE_DIRECTORIES = new RpPath[]{LANG_DIRECTORY, SOUNDS_FILE};

    private final SortedPathsContainer container = new SortedPathsContainer();

    private @UnknownNullability String extractDirectory;

    public RpMerger(){}

    public @Nullable String checkExtractDirectoryAndError(@Nullable String extractDirectory){
        if (extractDirectory == null) return "Directory can not be null!";
        File file = new File(extractDirectory);
        if (!file.isDirectory()) return "Specified path is not a directory!";
        if (!file.isAbsolute()) return "Directory must be absolute!";
        if (!file.exists()) return "Directory does not exists!";
        if (!file.canWrite()) return "Directory can not be written!";
        return null;
    }

    public void startMergeInto(String extractDirectory){

        this.extractDirectory = extractDirectory;
        String error = checkExtractDirectoryAndError(extractDirectory);
        Preconditions.checkArgument(error == null, error);
        LogUtils.pluginLog("ResourcePack merging started");
        List<VirtualResourcePack> resourcePacks = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!(plugin instanceof ResourcePackablePlugin resourcePackable)) continue;
            LogUtils.pluginLog("Found resourcePackable plugin: " + plugin.getName());
            resourcePacks.add(resourcePackable.getResourcePack());
        }
        resourcePacks.forEach(VirtualResourcePack::initialize);

        List<RpPath> files = new ArrayList<>();

        for (VirtualResourcePack resourcePack : resourcePacks) {
            files.addAll(resourcePack.getAllFoundFiles());
        }

        LogUtils.pluginLog("AutoAdding...");
        List<VirtualRpJsonFile> toAdd = new ArrayList<>();
        for (CustomItem item : CustomRegistries.ITEM.getAll()) {
            toAdd.addAll(item.getComponents().getOrDefault(CustomComponentType.AUTO_GENERATING_FILES_ITEM).getFiles(item));
        }
        for (CustomSound sound : CustomRegistries.SOUND.getAll()) {
            files.addAll(sound.getComponents().getOrDefault(CustomComponentType.AUTO_GENERATING_FILES_SOUND).getFiles(sound));
        }
        for (CustomRegistry<?> registry : CustomRegistries.REGISTRY.getAll()) {
            for (Registrable registrable : registry.getAll()) {
                if (!(registrable instanceof ComponentHolder<?> holder)) continue;
                if (!(holder instanceof Translatable translatable)) continue;
                for (VirtualRpJsonFile file : holder.getComponents().getOrDefault(CustomComponentType.TRANSLATABLE_THING).getFiles(translatable, registrable)) {
                    LogUtils.pluginLog("TranslatableThing: " + file.getPath() + " " + file.getData());
                    files.add(file);
                }
            }
        }
        ResourcepackInitializationEvent event = new ResourcepackInitializationEvent();
        event.callEvent();
        toAdd.addAll(event.getFiles());
        for (VirtualRpJsonFile file : toAdd) {
            if (files.contains(file)) continue;
            files.add(file);
        }
        for (VirtualRpJsonFile file : event.getForcedFiles()) {
            files.add(file);
        }
        LogUtils.pluginLog("Finished AutoAdding");

        files.forEach(container::add);

        container.debug();
        for (SamePathsContainer containerSame : container.getSames()) {
            if (isInAutoMerge(containerSame.getExample())){
                autoMergeCopy(containerSame);
            } else {
                RpPath path = containerSame.getMostPrioritized().getFirst();
                copyFile(path, path);
                //manualMergeCopy(containerSame);
            }
        }
        for (RpPath rpPath : container.getAllExcludingSame()) {
            copyFile(rpPath, rpPath);
        }

        LogUtils.pluginLog("DONE!");

    }

    public void autoMergeCopy(SamePathsContainer container){
        RpFileMerger merger;
        if (container.getExample().isBelow(LANG_DIRECTORY) || container.getExample().isBelow(SOUNDS_FILE)){
            merger = new MapLikeMerger();
        } else {
            throw new RuntimeException("Directory can not be merged automatically");
        }
        for (RpPath rpPath : container.getAll()) {
            merger.add(newBufferedReader(rpPath));
            LogUtils.pluginLog("Auto merging: " + rpPath);
        }
        merger.merge();
        RpPath rpPath = container.getExample();
        saveText(rpPath, merger.getMergedAsString());
    }

    // TODO COMPLETELY REMOVE ???
    public void manualMergeCopy(SamePathsContainer container){
        if (extractFileExists(container.getExample())){
            LogUtils.pluginLog("Manual file already exists: " + container.getExample());
            return;
        }
        int mergeId = 0;
        for (RpPath rpPath : container.getAll()) {
            copyFile(rpPath, rpPath.withMergeId(mergeId));
            LogUtils.pluginWarning("Should be manually merged: " + rpPath.withMergeId(mergeId));
            mergeId++;
        }
    }
    public boolean isInAutoMerge(RpPath rpPath){
        for (RpPath mergeDirectory : MERGE_DIRECTORIES) {
            if (rpPath.isBelow(mergeDirectory)) return true;
        }
        return false;
    }
    public BufferedWriter newBufferedWriter(RpPath rpPath){
        Path path = Paths.get(rpPath.getExtractPath(extractDirectory));
        try {
            return Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        } catch (IOException e) {throw new RuntimeException(e);}
    }
    public BufferedReader newBufferedReader(RpPath rpPath){
        InputStream stream = rpPath.getInputStream();
        return new BufferedReader(new InputStreamReader(stream));
    }

    public boolean extractFileExists(RpPath rpPath){
        String filePath = rpPath.getExtractPath(extractDirectory);
        return new File(filePath).exists();
    }
    public void createNewFile(RpPath rpPath){
        if (extractFileExists(rpPath)) return;
        String filePath = rpPath.getExtractPath(extractDirectory);
        String folderPath = rpPath.withLayerUp().getExtractPath(extractDirectory);
        new File(folderPath).mkdirs();
        try {
            new File(filePath).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void copyFile(RpPath from, RpPath to){
        createNewFile(to);
        switch (from.getFileType()) {
            case PNG -> copyImage(from, to);
            case OGG -> copySound(from, to);
            default -> copyText(from, to);
        }
    }
    public void copySound(RpPath from, RpPath to){
        Preconditions.checkArgument(from.getFileType() == FileType.OGG, "File " + to + " is not a sound!");

        try {
            InputStream input = from.getInputStream();
            OutputStream out = new FileOutputStream(to.getExtractPath(extractDirectory));

            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            input.close();
            out.close();

        } catch (Exception exception){
            throw new RuntimeException(exception);
        }
    }

    public void copyImage(RpPath from, RpPath to){
        Preconditions.checkArgument(from.getFileType() == FileType.PNG, "File " + to + " is not an image!");
        try {
            InputStream inputStream = from.getInputStream();
            BufferedImage image = ImageIO.read(inputStream);
            inputStream.close();
            ImageIO.write(image, "png", new File(to.getExtractPath(extractDirectory)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void saveText(RpPath to, String text){
        createNewFile(to);

        BufferedWriter writer = newBufferedWriter(to);

        try {
            writer.write(text);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void copyText(RpPath from, RpPath to){
        try {
            InputStream inputStream = from.getInputStream();
            Files.copy(inputStream, Paths.get(to.getExtractPath(extractDirectory)), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
