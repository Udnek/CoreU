package me.udnek.coreu.custom.registry;

import me.udnek.coreu.custom.event.InitializationEvent;
import me.udnek.coreu.custom.item.VanillaItemManager;
import me.udnek.coreu.util.LogUtils;

@org.jspecify.annotations.NullMarked public class InitializationProcess{

    private static Step step = Step.BEFORE_REGISTRIES_LOADED;

    public static Step getStep(){
        return step;
    }

    public static void start(){
        step = Step.BEFORE_REGISTRIES_LOADED;
        new InitializationEvent(step).callEvent();

        step = Step.GLOBAL_INITIALIZATION;
        new InitializationEvent(step).callEvent();

        LogUtils.pluginLog("Registries after initialization started");
        for (CustomRegistry<?> registry : CustomRegistries.REGISTRY.getAll()) {
            for (Registrable registrable : registry.getAll()) {
                registrable.globalInitialization();
            }
        }

        LogUtils.pluginLog("VanillaManager started");
        VanillaItemManager.getInstance().start();

        step = Step.AFTER_GLOBAL_INITIALIZATION;
        new InitializationEvent(step).callEvent();
    }



    public enum Step {
        BEFORE_REGISTRIES_LOADED,
        GLOBAL_INITIALIZATION,
        AFTER_GLOBAL_INITIALIZATION,
    }
}
