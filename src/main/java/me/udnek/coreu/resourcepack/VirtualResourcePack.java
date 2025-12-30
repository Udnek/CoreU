package me.udnek.coreu.resourcepack;

import me.udnek.coreu.resourcepack.path.RpPath;
import me.udnek.coreu.util.LogUtils;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VirtualResourcePack {

    private static final String ROOT_PATH = "resourcepack";

    protected final ResourcePackablePlugin plugin;
    protected List<RpPath> files = new ArrayList<>();

    public VirtualResourcePack(@NotNull ResourcePackablePlugin plugin){
        this.plugin = plugin;
    }

    public void initialize(){
        LogUtils.pluginLog("ResourcePack "+ plugin.getName() +" initialization started");
        RpPath rootPath = new RpPath(this, "");
        files = rootPath.findFiles();
        LogUtils.pluginLog("ResourcePack "+ plugin.getName() +" initialization ended");
    }

    public @NotNull ResourcePackablePlugin getPlugin() {
        return plugin;
    }

    public @NotNull List<String> getResources(@NotNull RpPath path){
        return FileManager.getAllResources(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }
    public @NotNull InputStream getInputStream(@NotNull RpPath path){
        return FileManager.getInputStream(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }
    public boolean isFile(@NotNull RpPath path){
        return FileManager.isFile(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }
    public boolean isDirectoryEmpty(@NotNull RpPath path){
        return FileManager.isDirectoryEmpty(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }

    public @NotNull List<RpPath> getAllFoundFiles(){
        return new ArrayList<>(files);
    }

    public @NotNull String getName(){return plugin.getName();}
}














