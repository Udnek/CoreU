package me.udnek.coreu.resourcepack.path;

import me.udnek.coreu.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class SortedPathsContainer{

    private final List<RpPath> all = new ArrayList<>();
    private final List<SamePathsContainer> sames = new ArrayList<>();

    public SortedPathsContainer(){}

    public void add(RpPath newPath){

        for (SamePathsContainer container : sames) {
            if (container.canAdd(newPath)){
                //LogUtils.pluginWarning("Same path + " + newPath + " added to container");
                all.add(newPath);
                container.add(newPath);
                return;
            }
        }
        for (RpPath path : all) {
            if (path.isSame(newPath)){
                //LogUtils.pluginWarning("Found new same paths: " + newPath + ", " + path);
                sames.add(new SamePathsContainer(path, newPath));
                all.add(newPath);
                return;
            }
        }
        //LogUtils.pluginLog("Added path " + newPath);
        all.add(newPath);
    }

    public List<SamePathsContainer> getSames() {
        return sames;
    }

    public void debug(){
        LogUtils.pluginLog("Paths container:");
        LogUtils.pluginLog("Total: " + all.size());
        LogUtils.pluginLog("Same:");
        for (SamePathsContainer samePathsContainer : sames) {
            for (RpPath path : samePathsContainer.getAll()) {
                LogUtils.pluginLog(path);
            }
            LogUtils.pluginLog("----------");
        }
    }

    public List<RpPath> getAllExcludingSame(){
        List<RpPath> toExclude = new ArrayList<>();
        for (RpPath path : all) {
            for (SamePathsContainer same : sames) {
                if (same.getExample().isSame(path)) {
                    toExclude.add(path);
                    break;
                }
            }
        }
        all.removeAll(toExclude);
        return all;
    }


}
