package me.udnek.coreu.resourcepack.path;

import com.google.common.base.Preconditions;
import me.udnek.coreu.resourcepack.FileManager;
import me.udnek.coreu.resourcepack.FileType;
import me.udnek.coreu.resourcepack.ResourcePackablePlugin;
import me.udnek.coreu.resourcepack.VirtualResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RpPath {

    protected final @Nullable VirtualResourcePack resourcePack;
    protected final @NotNull String path;

    public RpPath(@Nullable VirtualResourcePack resourcePack, @NotNull String path){
        this.resourcePack = resourcePack;
        this.path = FileManager.removeSlashes(path);
    }
    public RpPath(@NotNull String path){
        this(null, path);
    }

    public @Nullable ResourcePackablePlugin.Priority getPriority(){
        return resourcePack == null ? null : resourcePack.getPlugin().getPriority();
    }
    public int getPriorityValue(){
        return getPriority() == null ? -1 : getPriority().value;
    }


    public @NotNull RpPath withAdded(@NotNull String added){
        String newPath = FileManager.joinPaths(path, added);
        return new RpPath(resourcePack, newPath);
    }

    public @NotNull RpPath withRenamedLast(@NotNull String newName){
        if (path.isEmpty()) return this;
        return withLayerUp().withAdded(newName);
    }

    public @NotNull RpPath withLayerUp(){
        String newPath = FileManager.layerUp(path);
        return new RpPath(resourcePack, newPath);
    }

    public @NotNull RpPath withMergeId(@Nullable Integer mergeId){
        if (mergeId == null) return this;
        return withRenamedLast("(MANUAL_MERGE_" + resourcePack.getName() + "_" + mergeId + ")" + getLast());
    }

    public @NotNull List<RpPath> findFiles(){
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

    public boolean isBelow(@NotNull RpPath other){
        return path.matches(other.path.replace("*", ".*") + ".*");
    }

    public @NotNull String getPath() {
        return path;
    }
    public @NotNull String getLast(){
        if (path.isEmpty()) return "";
        int i = path.lastIndexOf("/");
        if (i == -1) return path;
        return path.substring(i);
    }

    public @NotNull FileType getFileType(){
        return FileType.get(path);
    }
    @Override
    public @NotNull String toString() {
        return "RPPath{ " + path + " (" + (resourcePack == null ? null : resourcePack.getName()) + ") }";
    }

    public @NotNull InputStream getInputStream(){
        return resourcePack.getInputStream(this);
    }

    public @NotNull String getExtractPath(@NotNull String extract){
        return FileManager.joinPaths(extract, path);
    }

    public boolean isSame(@NotNull RpPath other){
        return FileManager.removeSlashes(this.path).equals(FileManager.removeSlashes(other.path));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RpPath rpPath)) return false;
        return isSame(rpPath);
    }
}
