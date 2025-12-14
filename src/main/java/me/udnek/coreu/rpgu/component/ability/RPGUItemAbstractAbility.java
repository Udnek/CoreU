package me.udnek.coreu.rpgu.component.ability;

import com.google.common.collect.Iterables;
import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.instance.TranslatableThing;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.rpgu.component.RPGUComponents;
import me.udnek.coreu.rpgu.lore.ability.AbilityLorePart;
import me.udnek.coreu.util.Either;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RPGUItemAbstractAbility<ActivationContext> extends AbstractComponentHolder<RPGUItemAbility<?>> implements RPGUItemAbility<ActivationContext> {

    private static final int INFINITE_COOLDOWN = 100*60*60*20;
    private static final int INFINITE_COOLDOWN_THRESHOLD = INFINITE_COOLDOWN/2;

    public RPGUItemAbstractAbility(){
        TranslatableThing translations = getTranslations();
        if (translations == null) return;
        getComponents().set(translations);
    }

    public void activate(@NotNull CustomItem customItem,
                         @NotNull LivingEntity livingEntity,
                         boolean canselActivationContextIfCooldown,
                         @NotNull Either<UniversalInventorySlot, CustomEquipmentSlot.Single> slot,
                         @NotNull ActivationContext activationContext)
    {
        if (!(livingEntity instanceof Player player)) {
            action(customItem, livingEntity, slot, activationContext);
            return;
        }
        if (customItem.hasCooldown(player)) {
            if (canselActivationContextIfCooldown && activationContext instanceof Cancellable cancellable){
                cancellable.setCancelled(true);
            }
            return;
        }
        ActionResult result = action(customItem, player, slot, activationContext);
        if (result == ActionResult.INFINITE_COOLDOWN) infiniteCooldown(customItem, player);
        else if (result == ActionResult.FULL_COOLDOWN || result == ActionResult.PENALTY_COOLDOWN){
            double cooldown = getComponents().getOrDefault(RPGUComponents.ABILITY_COOLDOWN_TIME).get(player);
            if (result == ActionResult.PENALTY_COOLDOWN) cooldown = cooldown * getComponents().getOrDefault(RPGUComponents.ABILITY_MISS_USAGE_COOLDOWN_MULTIPLIER).get(player);
            if (cooldown > 0) cooldown(customItem, player, (int) Math.ceil(cooldown));
        }
    }

    @Override
    public void activate(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity, @NotNull Either<UniversalInventorySlot, CustomEquipmentSlot.Single> slot, @NotNull ActivationContext activationContext){
        activate(customItem, livingEntity, false, slot, activationContext);
    }

    protected abstract @NotNull ActionResult action(@NotNull CustomItem customItem, @NotNull LivingEntity livingEntity,
                                                    @NotNull Either<UniversalInventorySlot, CustomEquipmentSlot.Single> slot, @NotNull ActivationContext activationContext);


    public abstract @Nullable Pair<List<String>, List<String>> getEngAndRuDescription();

    public @Nullable TranslatableThing getTranslations(){
        Pair<List<String>, List<String>> desc = getEngAndRuDescription();
        if (desc == null) return null;
        int maxLen = Math.max(desc.getLeft().size(), desc.getRight().size());
        TranslatableThing translations = new TranslatableThing(null);
        for (int i = 0; i < maxLen; i++) {
            translations.addAdditional("description."+i, TranslatableThing.Translations.ofEngAndRu(
                    Iterables.get(desc.getLeft(), i, ""),
                    Iterables.get(desc.getRight(), i, "")
            ));
        }
        return translations;
    }

    @Override
    public abstract void getLore(@NotNull LoreBuilder loreBuilder);

    protected void addDescriptionLines(@NotNull AbilityLorePart lorePart){
        Pair<List<String>, List<String>> desc = getEngAndRuDescription();
        if (desc == null) return;
        int maxLen = Math.max(desc.getLeft().size(), desc.getRight().size());
        for (int i = 0; i < maxLen; i++) {
            lorePart.addAbilityDescription(Component.translatable(translationKey() + ".description."+i));
        }
    }

    protected void addPropertyLines(@NotNull AbilityLorePart lorePart){
        getProperties().forEach(c -> c.describe(lorePart));
    }

    public enum ActionResult {
        INFINITE_COOLDOWN,
        FULL_COOLDOWN,
        PENALTY_COOLDOWN,
        NO_COOLDOWN
    }
}
