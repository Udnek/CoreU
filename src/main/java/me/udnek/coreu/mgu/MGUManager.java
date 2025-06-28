package me.udnek.coreu.mgu;

import me.udnek.coreu.mgu.game.MGUGameInstance;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MGUManager{

    private static MGUManager instance;
    private final HashMap<MGUId, MGUGameInstance> games = new HashMap<>();
    private final Map<Player, MGUPlayer> players = new HashMap<>();

    public MGUManager(){}

    public static @NotNull MGUManager get() {
        if (instance == null) instance = new MGUManager();
        return instance;
    }

    // GAME
    public void registerGame(@NotNull MGUGameInstance game){
        this.games.put(game.getId(), game);
    }

    public @NotNull List<String> getActiveStringIds(){
        List<String> ids = new ArrayList<>();
        games.forEach((id, game) -> ids.add(id.asString()));
        return ids;
    }

    public @NotNull Map<MGUId, MGUGameInstance> getGames(){
        return new HashMap<>(games);
    }

    public @Nullable MGUGameInstance getGame(@NotNull String id){
        for (Map.Entry<MGUId, MGUGameInstance> entry : games.entrySet()) {
            if (entry.getKey().asString().equals(id)) return entry.getValue();
        }
        return null;
    }

    // PLAYER

    public @Nullable MGUPlayer getPlayer(@NotNull Player player){
        return players.get(player);
    }

    public void registerPlayer(@NotNull MGUPlayer player){
        this.players.put(player.getPlayer(), player);
    }

    public void unregisterPlayer(@NotNull MGUPlayer player){
        this.players.remove(player.getPlayer());
    }
}
















