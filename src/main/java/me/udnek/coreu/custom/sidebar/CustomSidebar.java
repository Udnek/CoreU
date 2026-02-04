package me.udnek.coreu.custom.sidebar;

import me.udnek.coreu.nms.NmsUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;


@org.jspecify.annotations.NullMarked public class CustomSidebar{
    private final String id;
    private Component title;
    private @UnknownNullability Objective objective;
    private Map<Integer, Component> lines = new HashMap<>();
    private final List<Player> players = new ArrayList<>();

    public CustomSidebar(String id, Component title) {
        this.id = id;
        this.title = title;
    }

    public void hide(Player player){
        if (!players.remove(player)) return;
        NmsUtils.sendPacket(player, new ClientboundSetObjectivePacket(objective, 1));
        NmsUtils.sendPacket(player, new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, null));
    }

    public void show(Player player){
        players.add(player);
        objective = new Scoreboard().addObjective(
                id, ObjectiveCriteria.DUMMY, NmsUtils.toNmsComponent(title) , ObjectiveCriteria.RenderType.INTEGER, false, BlankFormat.INSTANCE);
        NmsUtils.sendPacket(player, new ClientboundSetObjectivePacket(objective, 0));
        NmsUtils.sendPacket(player, new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, objective));

        for (Map.Entry<Integer, Component> entry : lines.entrySet()) {
            NmsUtils.sendPacket(player,
                    new ClientboundSetScorePacket(entry.getKey().toString(),
                            id,
                            entry.getKey(),
                            Optional.of(NmsUtils.toNmsComponent(entry.getValue())),
                            Optional.of(BlankFormat.INSTANCE)));
        }
    }

    public void updateForAll() {
        players.forEach(this::update);
    }

    public void update(Player player) {
        NmsUtils.sendPacket(player, new ClientboundSetObjectivePacket(objective, 2));
        for (Map.Entry<Integer, Component> entry : lines.entrySet()) {
            NmsUtils.sendPacket(player,
                    new ClientboundSetScorePacket(entry.getKey().toString(),
                            id,
                            entry.getKey(),
                            Optional.of(NmsUtils.toNmsComponent(entry.getValue())),
                            Optional.of(BlankFormat.INSTANCE)));
        }
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    public Map<Integer, Component> getLines() {
        return lines;
    }

    public void setLines(Map<Integer, Component> lines) {
        this.lines = new HashMap<>(lines);
    }

    public void setLines(List<Component> lines) {
        for (Component component: lines) addLine(component);
    }

    public void setLine(int position, @Nullable Component component) {
        if (component == null) {
            if (lines.isEmpty() || lines.get(position) == null) return;
            this.lines.remove(position);
        } else this.lines.put(position, component);
    }

    public void addLine(Component component){
        if (lines.isEmpty()) this.lines.put(1, component);
        else this.lines.put(Collections.min(lines.keySet()) - 1, component);
    }
}
