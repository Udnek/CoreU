package me.udnek.coreu.mgu.command;

import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public enum MGUCommandType {
    START("start", 1,false),
    STOP("stop", 1,false),
    JOIN("join", 1,true),
    LEAVE("leave", 0,true) {
        @Override
        public boolean testArgs(@NotNull CommandSender sender, @NotNull String[] args) {
            return true;
        }
        @Override
        public @NotNull List<String> getOptions(@NotNull CommandSender sender, @NotNull String[] args) {
            return List.of();
        }

        @Override
        public @NotNull ExecutionResult execute(@NotNull CommandSender sender, @NotNull String[] args) {
            MGUPlayer mguPlayer = MGUManager.get().getPlayer(((Player) sender));
            if (mguPlayer == null) return new ExecutionResult(ExecutionResult.Type.FAIL, "executor is not mguPlayer");
            MGUGameInstance game = mguPlayer.getGame();
            return game.executeCommand(new MGUCommandContext(this, sender, args, game, game.getType()));
        }
    },
    DEBUG("debug",1, false),
    LIST("list", 0, false){
        @Override
        public boolean testArgs(@NotNull CommandSender sender, @NotNull String[] args) {return true;}
        @Override
        public @NotNull List<String> getOptions(@NotNull CommandSender sender, @NotNull String[] args) {return List.of();}
        @Override
        public @NotNull ExecutionResult execute(@NotNull CommandSender sender, @NotNull String[] args) {
            sender.sendMessage("------------------");
            sender.sendMessage("Currently active games:");
            for (String id : MGUManager.get().getActiveStringIds()) {
                sender.sendMessage(id);
            }
            sender.sendMessage("------------------");
            return ExecutionResult.SUCCESS;
        }
    },
    COORDINATE_WAND("coordinate_wand", 1, true),
    EXECUTE("execute", 1, false);

    public final String name;
    public final int minLength;
    public final boolean playerOnly;

    MGUCommandType(@NotNull String name, int minLength, boolean playerOnly){
        this.name = name;
        this.playerOnly = playerOnly;
        this.minLength = minLength;
    }

    public boolean testArgs(@NotNull CommandSender sender, @NotNull String[] args){
        if (args.length < 2) return false;
        MGUGameInstance game = MGUManager.get().getGame(args[1]);
        if (game == null) return false;
        return game.testCommandArgs(new MGUCommandContext(this, sender, args, game, game.getType()));
    }
    public @NotNull List<String> getOptions(@NotNull CommandSender sender, @NotNull String[] args){
        if (args.length > 2){
            MGUGameInstance game = MGUManager.get().getGame(args[1]);
            if (game == null) return List.of();
            return game.getCommandOptions(new MGUCommandContext(this, sender, args, game, game.getType()));
        }
        return MGUManager.get().getActiveStringIds();
    }
    public @NotNull ExecutionResult execute(@NotNull CommandSender sender, @NotNull String[] args){
        MGUGameInstance game = Objects.requireNonNull(MGUManager.get().getGame(args[1]));
        return game.executeCommand(new MGUCommandContext(this, sender, args, game, game.getType()));
    }


    public static @Nullable MGUCommandType getType(@NotNull String[] args){
        MGUCommandType commandType = null;
        if (args.length == 0) return null;
        for (MGUCommandType type : MGUCommandType.values()) {
            if (type.name.equals(args[0])){
                commandType = type;
                break;
            }
        }
        if (commandType == null) return null;
        return (args.length - 1) >= commandType.minLength ? commandType : null;
    }

    public record ExecutionResult(MGUCommandType.ExecutionResult.@NotNull Type type, @NotNull String message) {

        public static final ExecutionResult SUCCESS = new ExecutionResult(Type.SUCCESS, "success");

        public enum Type {
            SUCCESS,
            FAIL
        }
    }
}
