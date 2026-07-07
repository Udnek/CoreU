package me.udnek.coreu.mgu.game;

import me.udnek.coreu.mgu.CoordinateWand;
import me.udnek.coreu.mgu.MGUId;
import me.udnek.coreu.mgu.command.MGUCommandContext;
import me.udnek.coreu.mgu.command.MGUCommandType;
import me.udnek.coreu.mgu.player.MGUPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@org.jspecify.annotations.NullMarked public abstract class MGUAbstractGame implements MGUGameInstance{

    private final MGUId id = MGUId.generateNew(this);

    @Override
    public final MGUId getId() {return id;}

    @Override
    public MGUCommandType.ExecutionResult executeCommand(MGUCommandContext context) {
        return switch (context.commandType()) {
            case START -> {
                if (isRunning()) yield MGUCommandType.ExecutionResult.Failure("game is running");
                yield start(context);
            }
            case STOP -> {
                if (!isRunning()) yield MGUCommandType.ExecutionResult.Failure("game is not running");
                yield stop(context);
            }
            case JOIN -> {
                if (isRunning()) yield MGUCommandType.ExecutionResult.Failure("game is running");
                yield join(Objects.requireNonNull(context.player()), context);
            }
            case LEAVE -> leave(Objects.requireNonNull(context.mguPlayer()), context);
            case DEBUG -> {
                getDebug(context).forEach(component -> context.sender().sendMessage(component));
                yield MGUCommandType.ExecutionResult.SUCCESS;
            }
            case COORDINATE_WAND -> {
                Objects.requireNonNull(context.player()).getInventory().addItem(createCoordinateWand());
                yield MGUCommandType.ExecutionResult.SUCCESS;
            }
            case EXECUTE -> execute(context);
            default -> MGUCommandType.ExecutionResult.SUCCESS;
        };
    }

    protected MGUCommandType.ExecutionResult execute(MGUCommandContext context){
        context.sender().sendMessage("This command doesn't do anything yet, override 'execute' method to add functionality");
        return MGUCommandType.ExecutionResult.SUCCESS;
    }

    @Override
    public List<String> getCommandOptions(MGUCommandContext context) {
        return List.of();
    }

    @Override
    public boolean testCommandArgs(MGUCommandContext context) {
        return true;
    }

    public ItemStack createCoordinateWand(){
        return CoordinateWand.createWithOrigin(getMap().getOrigin());
    }


    public List<Component> getDebug(MGUCommandContext context){
        List<Component> list = new ArrayList<>();
        list.add(Component.text("Game debug data: " + getId()));
        list.add(Component.text("isRunning: " + isRunning()));
        list.add(Component.text("players: " + getPlayers()));
        return list;
    }

    public abstract List<MGUPlayer> getPlayers();
    public abstract MGUCommandType.ExecutionResult start(MGUCommandContext context);
    public abstract MGUCommandType.ExecutionResult stop(MGUCommandContext context);
    public abstract MGUCommandType.ExecutionResult join(Player player, MGUCommandContext context);
    public abstract MGUCommandType.ExecutionResult leave(MGUPlayer player, MGUCommandContext context);
}







