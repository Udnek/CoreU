package me.udnek.coreu.custom.event;


import me.udnek.coreu.resourcepack.file.RpJsonFile;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked
public class ResourcepackInitializationEvent extends CustomEvent{

    protected List<RpJsonFile> files = new ArrayList<>();
    protected List<RpJsonFile> forced = new ArrayList<>();

    public ResourcepackInitializationEvent(){
    }

    public void addFile(RpJsonFile file){
        files.add(file);
    }
    @Deprecated
    public void forceAddFile(RpJsonFile file){
        //forced.add(file);
        files.add(file);
    }

    public List<RpJsonFile> getFiles() {
        return files;
    }

    @Deprecated
    public List<RpJsonFile> getForcedFiles() {return forced;}
}
