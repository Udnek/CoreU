package me.udnek.coreu.custom.registry;

import me.udnek.coreu.custom.event.InitializationEvent;
import me.udnek.coreu.custom.item.VanillaItemManager;
import me.udnek.coreu.util.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InitializationProcess {

    private static InitializationProcess.Step step = null;

    public static @Nullable InitializationProcess.Step getStep(){
        return step;
    }

    public static void start(){
        step = Step.BEFORE_REGISTRIES_INITIALIZATION;
        new InitializationEvent(step).callEvent();

        LogUtils.pluginLog("Registries After Initialization started");
        for (CustomRegistry<?> registry : CustomRegistries.REGISTRY.getAll()) {
            for (Registrable registrable : registry.getAll()) {
                registrable.afterInitialization();
            }
        }
        step = Step.AFTER_REGISTRIES_INITIALIZATION;
        new InitializationEvent(step).callEvent();

        step = Step.BEFORE_VANILLA_MANAGER;
        new InitializationEvent(step).callEvent();
        LogUtils.pluginLog("VanillaManager started");
        VanillaItemManager.getInstance().start();

        step = Step.AFTER_VANILLA_MANGER;
        new InitializationEvent(step).callEvent();

        step = Step.END;
        new InitializationEvent(step).callEvent();
    }



    public enum Step{
        BEFORE_REGISTRIES_INITIALIZATION,
        AFTER_REGISTRIES_INITIALIZATION,
        BEFORE_VANILLA_MANAGER,
        AFTER_VANILLA_MANGER,
        END
    }
}
