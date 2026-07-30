package me.udnek.coreu.custom.event;


import me.udnek.coreu.resourcepack.file.RpJsonFile;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ResourcepackInitializationEvent extends CustomEvent{

    protected List<RpJsonFile> files = new ArrayList<>();

    public ResourcepackInitializationEvent(){}

    public void addFile(RpJsonFile file){
        files.add(file);
    }

    public List<RpJsonFile> getFiles() {
        return files;
    }
}
