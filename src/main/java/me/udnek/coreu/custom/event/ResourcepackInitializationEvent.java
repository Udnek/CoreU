package me.udnek.coreu.custom.event;

import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class ResourcepackInitializationEvent extends CustomEvent{

    protected List<VirtualRpJsonFile> files = new ArrayList<>();
    protected List<VirtualRpJsonFile> forced = new ArrayList<>();

    public ResourcepackInitializationEvent(){
    }

    public void addFile(VirtualRpJsonFile file){
        files.add(file);
    }
    public void forceAddFile(VirtualRpJsonFile file){
        forced.add(file);
    }

    public List<VirtualRpJsonFile> getFiles() {
        return files;
    }

    public List<VirtualRpJsonFile> getForcedFiles() {return forced;}
}
