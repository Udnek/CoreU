package me.udnek.coreu.mgu.game;

import me.udnek.coreu.mgu.MGUId;
import me.udnek.coreu.mgu.command.MGUCommandContext;
import me.udnek.coreu.mgu.command.MGUCommandType;
import me.udnek.coreu.mgu.map.MGUMap;

import java.util.List;

@org.jspecify.annotations.NullMarked public  interface MGUGameInstance{

    MGUGameType getType();

    MGUMap getMap();

    MGUId getId();

    boolean isRunning();

    void stop();

    MGUCommandType.ExecutionResult executeCommand(MGUCommandContext context);
    List<String> getCommandOptions(MGUCommandContext context);
    boolean testCommandArgs(MGUCommandContext context);
}
