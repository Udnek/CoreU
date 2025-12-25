package me.udnek.coreu.custom.registry;

import me.udnek.coreu.custom.event.InitializationEvent;
import me.udnek.coreu.custom.item.VanillaItemManager;
import me.udnek.coreu.util.LogUtils;
import org.jetbrains.annotations.Nullable;

public class InitializationProcess {

    private static InitializationProcess.Step step = null;

    public static @Nullable InitializationProcess.Step getStep(){
        return step;
    }

    public static void start(){
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
