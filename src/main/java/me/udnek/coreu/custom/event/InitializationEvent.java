package me.udnek.coreu.custom.event;

import me.udnek.coreu.custom.registry.InitializationProcess;
import org.jetbrains.annotations.NotNull;

public class InitializationEvent extends CustomEvent{

    protected final @NotNull InitializationProcess.Step step;

    public InitializationEvent(@NotNull InitializationProcess.Step step){
        this.step = step;
    }

    public @NotNull InitializationProcess.Step getStep() {return step;}
}
