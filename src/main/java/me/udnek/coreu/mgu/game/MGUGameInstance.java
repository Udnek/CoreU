package me.udnek.coreu.mgu.game;

import me.udnek.coreu.mgu.MGUId;
import me.udnek.coreu.mgu.command.MGUCommandContext;
import me.udnek.coreu.mgu.command.MGUCommandType;
import me.udnek.coreu.mgu.map.MGUMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MGUGameInstance {

    @NotNull MGUGameType getType();

    @NotNull MGUMap getMap();

    @NotNull MGUId getId();

    boolean isRunning();

    void stop();

    @NotNull MGUCommandType.ExecutionResult executeCommand(@NotNull MGUCommandContext context);
    @NotNull List<String> getCommandOptions(@NotNull MGUCommandContext context);
    boolean testCommandArgs(@NotNull MGUCommandContext context);
}
