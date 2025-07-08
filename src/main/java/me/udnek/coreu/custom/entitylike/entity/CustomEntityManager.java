package me.udnek.coreu.custom.entitylike.entity;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.entitylike.EntityLikeManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CustomEntityManager extends EntityLikeManager<Entity, CustomEntityType, CustomEntity> implements Listener {

    private static CustomEntityManager instance;

    public static CustomEntityManager getInstance() {
        if (instance == null) {
            instance = new CustomEntityManager();
            Bukkit.getPluginManager().registerEvents(instance, CoreU.getInstance());
        }
        return instance;
    }

    private CustomEntityManager(){}

    @Override
    protected boolean equals(@NotNull Entity r1, @NotNull Entity r2) {
        return r1 == r2;
    }

    private void loadEntities(@NotNull List<Entity> entities){
        for (Entity entity : entities) {
            CustomEntityType entityType = CustomEntityType.get(entity);
            if (entityType != null) {
                loadAny(entityType, entity);
            }
        }
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event){
        for (Entity entity : event.getEntities()) {
            CustomEntityType entityType = CustomEntityType.get(entity);
            if (entityType != null) {
                unloadAny(entityType, entity);
            }
        }
    }

    @EventHandler
    public void onEntitiesLoaded(EntitiesLoadEvent event) {
        loadEntities(event.getEntities());
    }

    @EventHandler
    public void onServerRestart(ServerLoadEvent event){
        if (event.getType() != ServerLoadEvent.LoadType.RELOAD) return;

        for (World world : Bukkit.getWorlds()) {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                loadEntities(Arrays.asList(loadedChunk.getEntities()));
            }
        }
    }
}












