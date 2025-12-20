package me.udnek.coreu.custom.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.event.CustomItemGeneratedEvent;
import me.udnek.coreu.custom.inventory.CustomInventory;
import me.udnek.coreu.nms.Nms;
import me.udnek.coreu.rpgu.lore.AttributeLoreGenerator;
import me.udnek.coreu.util.LoreBuilder;
import me.udnek.coreu.util.SelfRegisteringListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static io.papermc.paper.datacomponent.DataComponentTypes.TOOLTIP_DISPLAY;

public class CustomItemListener extends SelfRegisteringListener {
    public CustomItemListener(@NotNull Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                Nms.get().iterateTroughCooldowns(player, (key, start, end) -> Nms.get().sendCooldown(player,  key, end-start));
            }
        }.runTaskLater(CoreU.getInstance(), 20);

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CustomItem customItem = CustomItem.get(event.getItem());
        if (customItem == null) return;
        Action action = event.getAction();
        if (action.isRightClick()) customItem.getComponents().getOrDefault(CustomComponentType.RIGHT_CLICKABLE_ITEM).onRightClick(customItem, event);
        if (action.isLeftClick()) customItem.getComponents().getOrDefault(CustomComponentType.LEFT_CLICKABLE_ITEM).onLeftClick(customItem, event);
    }

    @EventHandler
    public void onDispensing(BlockDispenseEvent event) {
        CustomItem customItem = CustomItem.get(event.getItem());
        if (customItem == null) return;
        switch (event.getBlock().getType()) {
            case DROPPER -> customItem.getComponents().getOrDefault(CustomComponentType.DISPENSABLE_ITEM).onDrop(customItem, event);
            case DISPENSER -> customItem.getComponents().getOrDefault(CustomComponentType.DISPENSABLE_ITEM).onDispense(customItem, event);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        CustomItem.consumeIfCustom(event.getItemInHand(), customItem ->
                customItem.getComponents().getOrDefault(CustomComponentType.BLOCK_PLACING_ITEM).onPlace(event));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerConsume(InventoryClickEvent event){
        CustomItem currentItem = CustomItem.get(event.getCurrentItem());
        CustomItem cursorItem = CustomItem.get(event.getCursor());
        if (currentItem != null) currentItem.getComponents().getOrDefault(CustomComponentType.INVENTORY_INTERACTABLE_ITEM).onBeingClicked(currentItem, event);
        if (cursorItem != null) cursorItem.getComponents().getOrDefault(CustomComponentType.INVENTORY_INTERACTABLE_ITEM).onClickWith(cursorItem, event);
    }

    @EventHandler
    public void updateItemOnTake(InventoryClickEvent event){
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if (clickedInventory.getType() == InventoryType.PLAYER) return;
        CustomInventory customInventory = CustomInventory.get(clickedInventory);
        if (customInventory != null && !customInventory.shouldAutoUpdateItems()) return;
        CustomItem.consumeIfCustom(event.getCurrentItem(), customItem -> event.setCurrentItem(customItem.update(event.getCurrentItem())));
    }

    @EventHandler
    public void updateItemOnJoin(PlayerJoinEvent event){
        PlayerInventory inventory = event.getPlayer().getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            int finalI = i;
            CustomItem.consumeIfCustom(item, customItem -> inventory.setItem(finalI, customItem.update(item)));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemGenerates(CustomItemGeneratedEvent event){
        CustomItem customItem = event.getCustomItem();
        LoreBuilder loreBuilder = event.getLoreBuilder();

        for (var component : customItem.getComponents().getAllTyped(LoreProvidingItemComponent.class)) {
            component.getLore(customItem, loreBuilder);
        }

        AttributeLoreGenerator.generateVanillaAttributes(event.getItemStack(), loreBuilder);

        if (VanillaItemManager.isReplaced(customItem)){
            TooltipDisplay oldDisplay = event.getItemStack().getDataOrDefault(TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().build());
            TooltipDisplay.Builder displayBuilder = TooltipDisplay.tooltipDisplay().hideTooltip(oldDisplay.hideTooltip());
            for (DataComponentType oldComponent : oldDisplay.hiddenComponents()) {
                displayBuilder.addHiddenComponents(oldComponent);
            }
            displayBuilder.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        }


        loreBuilder.add(
                LoreBuilder.Position.ID,
                Component.text(customItem.getId()).color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
            );
    }
}














