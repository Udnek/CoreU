package me.udnek.coreu.resourcepack.legacy;

import me.udnek.coreu.resourcepack.ResourcePackablePlugin;
import me.udnek.coreu.resourcepack.legacy.path.RpPath;
import me.udnek.coreu.util.LogUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked
public class VirtualResourcePackLeg {

    private static final String ROOT_PATH = "resourcepack";

    protected final ResourcePackablePlugin plugin;
    protected List<RpPath> files = new ArrayList<>();

    public VirtualResourcePackLeg(ResourcePackablePlugin plugin){
        this.plugin = plugin;
    }

    public void initialize(){
        LogUtils.coreuLog("ResourcePack "+ plugin.getName() +" initialization started");
        RpPath rootPath = new RpPath(this, "");
        files = rootPath.findFiles();
        LogUtils.coreuLog("ResourcePack "+ plugin.getName() +" initialization ended");
    }

    public ResourcePackablePlugin getPlugin() {
        return plugin;
    }

    public List<String> getResources(RpPath path){
        return FileManager.getAllResources(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }
    public InputStream getInputStream(RpPath path){
        return FileManager.getInputStream(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }
    public boolean isFile(RpPath path){
        return FileManager.isFile(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }
    public boolean isDirectoryEmpty(RpPath path){
        return FileManager.isDirectoryEmpty(plugin.getClass(), FileManager.joinPaths(ROOT_PATH, path.getPath()));
    }

    public List<RpPath> getAllFoundFiles(){
        return new ArrayList<>(files);
    }

    public String getName(){return plugin.getName();}
}














