package me.udnek.coreu.mgu;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.instance.AutoGeneratingFilesItem;
import me.udnek.coreu.custom.component.instance.LeftClickableItem;
import me.udnek.coreu.custom.component.instance.RightClickableItem;
import me.udnek.coreu.custom.item.ConstructableCustomItem;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
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
            String yaw = round(location.getYaw());
            String pitch = round(location.getPitch());

            Component component = Component.text("Location: ").color(NamedTextColor.GOLD);
            TextComponent copy = Component.text(
                    "[X: " + x +
                            ", Y: " + y +
                            ", Z: " + z +
                            ", Yaw: " + yaw +
                            ", Pitch: " + pitch +
                            ", PlayerFacing: " + player.getFacing() + "]")
                    .clickEvent(ClickEvent.copyToClipboard(x + ", " + y + ", " + z + ", " + yaw + "f, " + pitch+"f"));

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
            if (origin == null || origin.isEmpty()) origin = List.of(0d, 0d, 0d);

            String x = round(location.getX() - origin.getFirst());
            String y = round(location.getY() - origin.get(1));
            String z = round(location.getZ() - origin.get(2));

            Component component = Component.text("Block Location: ").color(NamedTextColor.GOLD);
            TextComponent copy = Component.text("[X: " + x + ", Y: " + y + ", Z: " + z + "]")
                    .clickEvent(ClickEvent.copyToClipboard(x + ", " + y + ", " + z));

            event.getPlayer().sendMessage(component.append(copy
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to copy")))
                    .color(NamedTextColor.GREEN)));
        });
    }

    public @NotNull String round(double number) {
        return new DecimalFormat("#.0#", new DecimalFormatSymbols(Locale.US)).format(number);
    }
    public @NotNull String round(float number) {
        return new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.US)).format(number);
    }
}
