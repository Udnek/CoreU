package me.udnek.coreu.resourcepack.path;

import com.google.common.base.Preconditions;
import me.udnek.coreu.resourcepack.FileManager;
import me.udnek.coreu.resourcepack.FileType;
import me.udnek.coreu.resourcepack.ResourcePackablePlugin;
import me.udnek.coreu.resourcepack.VirtualResourcePack;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class RpPath{

    protected final @Nullable VirtualResourcePack resourcePack;
    protected final String path;

    public RpPath(@Nullable VirtualResourcePack resourcePack, String path){
        this.resourcePack = resourcePack;
        this.path = FileManager.removeSlashes(path);
    }
    public RpPath(String path){
        this(null, path);
    }

    public @Nullable ResourcePackablePlugin.Priority getPriority(){
        return resourcePack == null ? null : resourcePack.getPlugin().getPriority();
    }
    public int getPriorityValue(){
        return getPriority() == null ? -1 : getPriority().value;
    }


    public RpPath withAdded(String added){
        String newPath = FileManager.joinPaths(path, added);
        return new RpPath(resourcePack, newPath);
    }

    public RpPath withRenamedLast(String newName){
        if (path.isEmpty()) return this;
        return withLayerUp().withAdded(newName);
    }

    public RpPath withLayerUp(){
        String newPath = FileManager.layerUp(path);
        return new RpPath(resourcePack, newPath);
    }

    public RpPath withMergeId(@Nullable Integer mergeId){
        if (mergeId == null) return this;
        return withRenamedLast("(MANUAL_MERGE_" + resourcePack.getName() + "_" + mergeId + ")" + getLast());
    }

    public List<RpPath> findFiles(){
        //LogUtils.pluginLog(this);
        //LogUtils.pluginLog("Checking is file");
        Preconditions.checkArgument(resourcePack != null, "Resourcepack can not be null to find files: " + this);
        if (resourcePack.isFile(this)) return List.of(this);
        //LogUtils.pluginLog("Checking is empty directory");
        if (resourcePack.isDirectoryEmpty(this)) return List.of();

        //LogUtils.pluginLog("Checking for subdirectories");
        List<String> resources = resourcePack.getResources(this);
        //LogUtils.pluginLog("Found resources: " + resources);
        List<RpPath> subFiles = new ArrayList<>();
        for (String resource : resources) {
            subFiles.addAll(withAdded(resource).findFiles());
        }
        return subFiles;
    }

    public boolean isBelow(RpPath other){
        return path.matches(other.path.replace("*", ".*") + ".*");
    }

    public String getPath() {
        return path;
    }
    public String getLast(){
        if (path.isEmpty()) return "";
        int i = path.lastIndexOf("/");
        if (i == -1) return path;
        return path.substring(i);
    }

    public FileType getFileType(){
        return FileType.get(path);
    }
    @Override
    public String toString() {
        return "RPPath{ " + path + " (" + (resourcePack == null ? null : resourcePack.getName()) + ") }";
    }

    public InputStream getInputStream(){
        return resourcePack.getInputStream(this);
    }

    public String getExtractPath(String extract){
        return FileManager.joinPaths(extract, path);
    }

    public boolean isSame(RpPath other){
        return FileManager.removeSlashes(this.path).equals(FileManager.removeSlashes(other.path));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RpPath rpPath)) return false;
        return isSame(rpPath);
    }
}
