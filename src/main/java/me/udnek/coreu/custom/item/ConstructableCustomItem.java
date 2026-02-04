package me.udnek.coreu.custom.item;

import com.google.errorprone.annotations.ForOverride;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;import io.papermc.paper.datacomponent.item.*;
import me.udnek.coreu.custom.component.instance.TranslatableThing;
import me.udnek.coreu.custom.event.CustomItemGeneratedEvent;
import me.udnek.coreu.custom.recipe.RecipeManager;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
import me.udnek.coreu.custom.registry.InitializationProcess;
import me.udnek.coreu.util.LoreBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static io.papermc.paper.datacomponent.DataComponentTypes.*;


@org.jspecify.annotations.NullMarked public abstract class ConstructableCustomItem extends AbstractRegistrableComponentable<CustomItem>implements CustomItemProperties, UpdatingCustomItem{
    public static final Material DEFAULT_MATERIAL = Material.GUNPOWDER;
    public static final Material DEFAULT_MATERIAL_FOR_BLOCK_PLACEABLE = Material.BARRIER;

    protected ItemStack itemStack = null;
    protected RepairData repairData = null;
    protected int recipeNumber = 0;
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Material getMaterial() {return DEFAULT_MATERIAL;}

    @ForOverride
    @OverridingMethodsMustInvokeSuper
    public void initializeComponents(){
        TranslatableThing translations = getTranslations();
        if (translations != null) getComponents().set(translations);
    }

    @MustBeInvokedByOverriders
    @Override
    public void globalInitialization() {
        UpdatingCustomItem.super.globalInitialization();
        initializeComponents();
        repairData = initializeRepairData();
        generateRecipes(RecipeManager.getInstance()::register);
    }

    @Override
    public void setCooldown(Player player, int ticks) {
        player.setCooldown(getItemNoClone(), ticks);
    }
    @Override
    public int getCooldown(Player player) {
        return player.getCooldown(getItemNoClone());
    }

    @Override
    public boolean isTagged(Tag<Material> tag) {
        return tag.isTagged(getMaterial());
    }
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public String translationKey() {
        return "item."+getKey().namespace()+"."+getKey().value();
    }

    @ForOverride
    public @Nullable TranslatableThing getTranslations(){
        return null;
    }

    public @Nullable LoreBuilder getLoreBuilder(){
        List<Component> lore = new ArrayList<>();
        getLore(lore::add);
        if (lore.isEmpty()) return null;
        LoreBuilder builder = new LoreBuilder();
        lore.forEach(component -> builder.add(LoreBuilder.Position.VANILLA_LORE, component));
        return builder;
    }

    @ForOverride
    public void getLore(Consumer<Component> consumer){}

    @Override
    public @Nullable DataSupplier<ItemLore> getLore() {
        LoreBuilder builder = getLoreBuilder();
        if (builder == null) return null;
        return DataSupplier.of(ItemLore.lore(builder.build()));
    }

    @ForOverride
    public @Nullable RepairData initializeRepairData(){return RepairData.EMPTY;}

    @Override
    public final @Nullable RepairData getRepairData() {return repairData;}
    @Override
    public final @Nullable DataSupplier<Repairable> getRepairable() {
        if (getRepairData() == null) return null;
        Repairable repairable = getRepairData().getSuitableVanillaRepairable();
        if (repairable.types().isEmpty()) return DataSupplier.of(null);
        return DataSupplier.of(repairable);
    }
    @Override
    public @Nullable DataSupplier<Key> getItemModel() {return DataSupplier.of(getKey());}

    @Override
    public @Nullable DataSupplier<Component> getItemName() {
        return DataSupplier.of(Component.translatable(translationKey()));
    }
    @Override
    public @Nullable DataSupplier<UseCooldown> getUseCooldown() {
        return DataSupplier.of(UseCooldown.useCooldown(0.0000001f).cooldownGroup(getKey()).build());
    }
    public @Nullable CustomItem getUseRemainderCustom(){return null;}

    @Override
    public @Nullable List<ItemFlag> getTooltipHides() {
        return List.of(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public @Nullable DataSupplier<ItemRarity> getRarity() {return DataSupplier.of(ItemRarity.COMMON);}
    ///////////////////////////////////////////////////////////////////////////
    protected void setPersistentData(ItemStack itemStack){
        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(PERSISTENT_DATA_CONTAINER_NAMESPACE, PersistentDataType.STRING, getId()));
    }
    protected <T> void setData(DataComponentType.Valued<T> type, @Nullable DataSupplier<T> supplier){
        if (supplier == null) return;
        T value = supplier.get();
        if (value == null) itemStack.unsetData(type);
        else itemStack.setData(type, value);
    }
    protected void setData(DataComponentType.NonValued type, @Nullable Boolean value){
        if (value == null) return;
        if (value) itemStack.setData(type);
        else itemStack.resetData(type);
    }
    protected void hideSpecificComponent(DataComponentType type){
        TooltipDisplay oldDisplay = itemStack.getDataOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().build());
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay().hideTooltip(oldDisplay.hideTooltip());
        for (DataComponentType oldComponent : oldDisplay.hiddenComponents()) {
            builder.addHiddenComponents(oldComponent);
        }
        builder.addHiddenComponents(type);
        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
    }

    protected void initializeItemStack(){
        itemStack = new ItemStack(getMaterial());
        setPersistentData(itemStack);

        int maxStackSize = CustomItemProperties.getInDataOrInStack(itemStack, DataComponentTypes.MAX_STACK_SIZE, getMaxStackSize(), 1);
        int maxDamage = CustomItemProperties.getInDataOrInStack(itemStack, DataComponentTypes.MAX_DAMAGE, getMaxDamage(), 0);
        if (maxStackSize > 1 && maxDamage > 0){
            throw new RuntimeException("Item can not be stackable and damageable: " + getId());
        }

        setData(DataComponentTypes.MAX_STACK_SIZE, getMaxStackSize());
        setData(DataComponentTypes.MAX_DAMAGE, getMaxDamage());

        setData(DataComponentTypes.ITEM_NAME, getItemName());
        setData(DataComponentTypes.LORE, getLore());
        setData(DataComponentTypes.RARITY, getRarity());
        setData(DataComponentTypes.TOOLTIP_DISPLAY, getTooltipDisplay());
        setData(DataComponentTypes.FOOD, getFood());
        setData(DataComponentTypes.TOOL, getTool());
        setData(DataComponentTypes.CUSTOM_NAME, getDisplayName());
        setData(DataComponentTypes.UNBREAKABLE, getUnbreakable());
        setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, getEnchantmentGlintOverride());
        setData(DataComponentTypes.CUSTOM_MODEL_DATA, getCustomModelData());
        setData(DataComponentTypes.DAMAGE_RESISTANT, getDamageResistant());
        setData(DataComponentTypes.TRIM, getTrim());
        setData(DataComponentTypes.INSTRUMENT, getMusicInstrument());
        setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, getAttributeModifiers());
        setData(DataComponentTypes.BLOCK_DATA, getBlockData());
        setData(DataComponentTypes.GLIDER, getGlider());
        setData(DataComponentTypes.ITEM_MODEL, getItemModel());
        setData(DataComponentTypes.USE_REMAINDER, getUseRemainder());
        setData(DataComponentTypes.EQUIPPABLE, getEquippable());
        setData(DataComponentTypes.USE_COOLDOWN, getUseCooldown());
        setData(DataComponentTypes.REPAIRABLE, getRepairable());
        setData(DataComponentTypes.CONSUMABLE, getConsumable());
        setData(DataComponentTypes.POTION_CONTENTS, getPotionContents());
        setData(DataComponentTypes.DYED_COLOR, getDyedColor());
        setData(DataComponentTypes.FIREWORK_EXPLOSION, getFireworkExplosion());
        setData(DataComponentTypes.WEAPON, getWeapon());
        setData(DataComponentTypes.ATTACK_RANGE, getAttackRange());
        setData(DataComponentTypes.BLOCKS_ATTACKS, getBlocksAttacks());
        setData(DataComponentTypes.USE_EFFECTS, getUseEffects());
        setData(DataComponentTypes.KINETIC_WEAPON, getKineticWeapon());
        setData(DataComponentTypes.PIERCING_WEAPON, getPiercingWeapon());
        setData(DataComponentTypes.SWING_ANIMATION, getSwingAnimation());

        if (getUseRemainderCustom() != null) itemStack.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(getUseRemainderCustom().getItem()));

        List<ItemFlag> tooltipHides = getTooltipHides();
        if (tooltipHides != null){
            for (ItemFlag flag : tooltipHides) {
                switch (flag){
                    case HIDE_ENCHANTS -> hideSpecificComponent(DataComponentTypes.ENCHANTMENTS);
                    case HIDE_ATTRIBUTES -> hideSpecificComponent(DataComponentTypes.ATTRIBUTE_MODIFIERS);
                    case HIDE_UNBREAKABLE -> hideSpecificComponent(DataComponentTypes.UNBREAKABLE);
                    case HIDE_DESTROYS -> hideSpecificComponent(DataComponentTypes.CAN_BREAK);
                    case HIDE_PLACED_ON -> hideSpecificComponent(DataComponentTypes.CAN_PLACE_ON);
                    case HIDE_DYE -> hideSpecificComponent(DataComponentTypes.DYED_COLOR);
                    case HIDE_ARMOR_TRIM -> hideSpecificComponent(DataComponentTypes.TRIM);
                    case HIDE_STORED_ENCHANTS -> hideSpecificComponent(DataComponentTypes.STORED_ENCHANTMENTS);
                }
            }
        }

        modifyFinalItemStack(itemStack);
    }

    protected void modifyFinalItemStack(ItemStack itemStack){}

    protected ItemStack getItemNoClone(){
        if (InitializationProcess.getStep() == InitializationProcess.Step.BEFORE_REGISTRIES_LOADED) {
            throw new RuntimeException(String.format("item %s hasn't afterInitialized yet", getId()));
        }
        if (itemStack == null){
            initializeItemStack();
            CustomItemGeneratedEvent event = new CustomItemGeneratedEvent(this, itemStack, getLoreBuilder(), getRepairData());
            event.callEvent();
            event.getLoreBuilder().buildAndApply(event.getItemStack());
            repairData = event.getRepairData();
            setData(DataComponentTypes.REPAIRABLE, getRepairable());
        }
        return itemStack;
    }

    @Override
    public ItemStack getItem(){
        return getItemNoClone().clone();
    }
    ///////////////////////////////////////////////////////////////////////////
    protected NamespacedKey getNewRecipeKey() {
        return Objects.requireNonNull(NamespacedKey.fromString(getId() + "_" + recipeNumber++));
    }

    @ForOverride
    protected void generateRecipes(Consumer<Recipe> consumer){}
}
