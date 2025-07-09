package me.udnek.coreu.custom.advancement;

import com.google.common.collect.ImmutableMap;
import me.udnek.coreu.custom.item.ItemUtils;
import me.udnek.coreu.util.Reflex;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomAdvancementUtils {

    private static final HashMap<CustomAdvancementContainer, CustomAdvancementContainer> realPoses = new HashMap<>();

    public static @NotNull ConstructableCustomAdvancement itemAdvancement(@NotNull Key key, @NotNull ItemStack itemStack){
        ConstructableCustomAdvancement advancement = new ConstructableCustomAdvancement(key);
        advancement.addCriterion(ItemUtils.getId(itemStack), AdvancementCriterion.INVENTORY_CHANGE.create(itemStack));
        CustomAdvancementDisplayBuilder display = new CustomAdvancementDisplayBuilder(itemStack);
        display.title(ItemUtils.getDisplayName(itemStack));
        advancement.display(display);
        return advancement;
    }

    public static void register(@NotNull CustomAdvancementContainer advancementContainer){
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerAdvancementManager manager = server.getAdvancements();

        AdvancementHolder advancementHolder = advancementContainer.get();

        Reflex.invokeMethod(
                manager,
                Reflex.getMethod(
                        ServerAdvancementManager.class,
                        "validate",
                        ResourceLocation.class, Advancement.class
                ),
                advancementHolder.id(),
                advancementHolder.value()
        );

        ImmutableMap.Builder<ResourceLocation, AdvancementHolder> mapBuilder = ImmutableMap.builder();
        Map<ResourceLocation, AdvancementHolder> advancements = new HashMap<>(mapBuilder.buildOrThrow());
        advancements.putAll((Map<ResourceLocation, AdvancementHolder>) Reflex.getFieldValue(manager, "advancements"));
        advancements.put(advancementHolder.id(), advancementHolder);

        AdvancementTree tree = new AdvancementTree();
        tree.addAll(advancements.values());
        for (AdvancementNode advancementnode : tree.roots()) {
            if (advancementnode.holder().value().display().isPresent()) {
                TreeNodePosition.run(advancementnode);
            }
        }

        Reflex.setFieldValue(manager, "advancements", advancements);
        Reflex.setFieldValue(manager, "tree", tree);

        for (@NotNull CustomAdvancementContainer fake : advancementContainer.getFakes()) {
            realPoses.put(fake, advancementContainer);
            register(fake);
        }

        for (Map.Entry<CustomAdvancementContainer, CustomAdvancementContainer> entry : realPoses.entrySet()) {
            CustomAdvancementContainer fake = entry.getKey();
            CustomAdvancementContainer real = entry.getValue();

            DisplayInfo fakeDisplay = (DisplayInfo) Reflex.<Optional<?>>getFieldValue(fake.get().value(), "display").orElse(null);
            if (fakeDisplay == null) continue;

            DisplayInfo realDisplay = (DisplayInfo) Reflex.<Optional<?>>getFieldValue(real.get().value(), "display").orElse(null);
            if (realDisplay == null) return;

            fakeDisplay.setLocation(realDisplay.getX(), realDisplay.getY());
        }
    }
}
