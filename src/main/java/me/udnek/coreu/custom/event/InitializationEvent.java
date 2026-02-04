package me.udnek.coreu.custom.event;

import me.udnek.coreu.custom.registry.InitializationProcess;

@org.jspecify.annotations.NullMarked public class InitializationEvent extends CustomEvent{

    protected final InitializationProcess.Step step;

    public InitializationEvent(InitializationProcess.Step step){
        this.step = step;
    }

    public InitializationProcess.Step getStep() {return step;}
}
