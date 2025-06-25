package me.udnek.coreu.mgu;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.instance.LeftClickableItem;
import me.udnek.coreu.custom.component.instance.RightClickableItem;
import me.udnek.coreu.custom.item.ConstructableCustomItem;
import me.udnek.coreu.util.Utils;
import me.udnek.coreu.custom.component.instance.AutoGeneratingFilesItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CoordinateWand extends ConstructableCustomItem {

    public static final NamespacedKey ORIGIN_KEY = new NamespacedKey(CoreU.getInstance(), "origin");

    public static @NotNull ItemStack createWithOrigin(@NotNull Location location) {
        ItemStack item = MGUItems.COORDINATE_WAND.getItem();
        List<Double> localZero = List.of(location.getX(), location.getY(), location.getZ());
        item.editPersistentDataContainer(persistentDataContainer ->
                persistentDataContainer.set(CoordinateWand.ORIGIN_KEY, PersistentDataType.LIST.doubles(), localZero));
        return item;
    }

    @Override
    public @NotNull String getRawId() {
        return "coordinate_wand";
    }

    @Override
    public @NotNull ItemStack update(@NotNull ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public void initializeComponents() {
        super.initializeComponents();

        getComponents().set(AutoGeneratingFilesItem.HANDHELD);
        getComponents().set((RightClickableItem) (item, event) -> {
            event.setCancelled(true);
            Player player = event.getPlayer();
            Location location = player.getLocation();

            List<Double> origin = Objects.requireNonNull(event.getItem())
                    .getPersistentDataContainer().get(ORIGIN_KEY, PersistentDataType.LIST.doubles());
            if (origin == null || origin.isEmpty()) {origin = List.of(0d, 0d, 0d);}

            String x = round(location.getX() - origin.getFirst());
            String y = round(location.getY() - origin.get(1));
            String z = round(location.getZ() - origin.get(2));

            Component component = Component.text("Location: ").color(NamedTextColor.GOLD);
            TextComponent copy = Component.text(
                    "[X: " + x +
                            ", Y: " + y +
                            ", Z: " + z +
                            ", Yaw: " + round(location.getYaw()) +
                            ", Pitch: " + round(location.getPitch()) +
                            ", PlayerFacing: " + player.getFacing() + "]")
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, x + ", " + y + ", " + z + ", " + round(location.getYaw()) + ", "
                            + round(location.getPitch())));

            event.getPlayer().sendMessage(component.append(copy
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to copy")))
                    .color(NamedTextColor.GREEN)));
        });
        getComponents().set((LeftClickableItem) (item, event) -> {
            event.setCancelled(true);
            Block block = event.getClickedBlock();
            if (block == null) return;
            Location location = block.getLocation();

            List<Double> origin =  Objects.requireNonNull(event.getItem())
                    .getPersistentDataContainer().get(ORIGIN_KEY, PersistentDataType.LIST.doubles());
            if (origin == null || origin.isEmpty()) {origin = List.of(0d, 0d, 0d);}

            String x = round(location.getX() - origin.getFirst());
            String y = round(location.getY() - origin.get(1));
            String z = round(location.getZ() - origin.get(2));

            Component component = Component.text("Block Location: ").color(NamedTextColor.GOLD);
            TextComponent copy = Component.text("[X: " + x + ", Y: " + y + ", Z: " + z + "]")
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, x + ", " + y + ", " + z));

            event.getPlayer().sendMessage(component.append(copy
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to copy")))
                    .color(NamedTextColor.GREEN)));
        });
    }

    public @NotNull String round(double number) {
        return  Utils.roundToTwoDigits(number).replace(",", ".");
    }
}
