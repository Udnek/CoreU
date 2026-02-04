package me.udnek.coreu.resourcepack;

import org.bukkit.plugin.Plugin;

@org.jspecify.annotations.NullMarked public  interface ResourcePackablePlugin extends Plugin{
    default VirtualResourcePack getResourcePack(){
        return new VirtualResourcePack(this);

    }

    Priority getPriority();

    enum Priority {

        MAIN(2),
        BASE(1),
        TECHNICAL(0);

        public final int value;

        Priority(int value){
            this.value = value;
        }

    }
}
