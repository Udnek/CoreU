package me.udnek.coreu.resourcepack;

import org.bukkit.plugin.Plugin;

@org.jspecify.annotations.NullMarked
public interface ResourcePackablePlugin extends Plugin {

    Priority getPriority();

    enum Priority {
        MAIN(0),
        BASE(1),
        TECHNICAL(2),
        RUNTIME(3);

        public final int value;

        Priority(int value){
            this.value = value;
        }
    }
}
