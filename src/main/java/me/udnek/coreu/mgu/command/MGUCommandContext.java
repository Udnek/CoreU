package me.udnek.coreu.mgu.command;

import me.udnek.coreu.mgu.MGUManager;
import me.udnek.coreu.mgu.game.MGUGameInstance;
import me.udnek.coreu.mgu.game.MGUGameType;
import me.udnek.coreu.mgu.player.MGUPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@org.jspecify.annotations.NullMarked public  record MGUCommandContext(
        MGUCommandType commandType,
        CommandSender sender,
        String[] args,
        @Nullable Player player,
        @Nullable MGUPlayer mguPlayer,
        @Nullable MGUGameInstance gameInstance,
        @Nullable MGUGameType gameType){
    public MGUCommandContext(
            MGUCommandType type,
            CommandSender sender,
            String[] args,
            @Nullable MGUGameInstance gameInstance,
            @Nullable MGUGameType gameType)
    {
        this(type, sender, args,
                sender instanceof Player ? ((Player) sender) : null,
                sender instanceof Player ? MGUManager.get().getPlayer(((Player) sender)) : null,
                gameInstance, gameType);
    }

    public MGUCommandContext(
            MGUCommandType type,
            CommandSender sender,
            String[] args
    ){
        this(type, sender, args, null, null);
    }
}
