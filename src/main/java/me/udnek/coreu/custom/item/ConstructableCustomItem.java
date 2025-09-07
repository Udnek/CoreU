package me.udnek.coreu.custom.item;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.ForOverride;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.*;
import me.udnek.coreu.custom.attribute.AttributeUtils;
import me.udnek.coreu.custom.component.AbstractComponentHolder;
import me.udnek.coreu.custom.component.instance.TranslatableThing;
import me.udnek.coreu.custom.event.CustomItemGeneratedEvent;
import me.udnek.coreu.custom.recipe.RecipeManager;
import me.udnek.coreu.custom.registry.AbstractRegistrableComponentable;
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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.papermc.paper.datacomponent.DataComponentTypes.*;


public abstract class ConstructableCustomItem extends AbstractRegistrableComponentable<CustomItem> implements CustomItemProperties, UpdatingCustomItem {
    private boolean afterInitialized = false;
    protected ItemStack itemStack = null;
    protected List<Recipe> recipes = null;
    protected RepairData repairData = null;

    public static final Material DEFAULT_MATERIAL = Material.GUNPOWDER;
    public static final Material DEFAULT_MATERIAL_FOR_BLOCK_PLACEABLE = Material.BARRIER;

    ///////////////////////////////////////////////////////////////////////////
    // INITIAL
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public @NotNull Material getMaterial() {return DEFAULT_MATERIAL;}

    @ForOverride
    @OverridingMethodsMustInvokeSuper
    public void initializeComponents(){
        TranslatableThing translations = getTranslations();
        if (translations != null) getComponents().set(translations);
    }

    @MustBeInvokedByOverriders
    @Override
    public void afterInitialization() {
        UpdatingCustomItem.super.afterInitialization();
        afterInitialized = true;
        initializeComponents();
        repairData = initializeRepairData();
        generateRecipes(this::registerRecipe);
    }

    @Override
    public void setCooldown(@NotNull Player player, int ticks) {
        player.setCooldown(getItemNoClone(), ticks);
    }
    @Override
    public int getCooldown(@NotNull Player player) {
        return player.getCooldown(getItemNoClone());
    }

    @Override
    public boolean isTagged(@NotNull Tag<Material> tag) {
        return tag.isTagged(getMaterial());
    }

    ///////////////////////////////////////////////////////////////////////////
    // COMPONENTS
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public @NotNull String translationKey() {
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
    public void getLore(@NotNull Consumer<Component> consumer){}

    @Override
    public DataSupplier<ItemLore> getLore() {
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

    @ForOverride
    public void initializeAdditionalAttributes(@NotNull ItemStack itemStack){}
    public boolean addDefaultAttributes(){return false;}

    @Override
    public @Nullable List<ItemFlag> getTooltipHides() {
        return List.of(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public @Nullable DataSupplier<ItemRarity> getRarity() {return DataSupplier.of(ItemRarity.COMMON);}
    ///////////////////////////////////////////////////////////////////////////
    // CREATING
    ///////////////////////////////////////////////////////////////////////////
    protected void setPersistentData(@NotNull ItemStack itemStack){
        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(PERSISTENT_DATA_CONTAINER_NAMESPACE, PersistentDataType.STRING, getId()));
    }
    protected <T> void setData(@NotNull DataComponentType.Valued<@NotNull T> type, @Nullable DataSupplier<T> supplier){
        if (supplier == null) return;
        T value = supplier.get();
        if (value == null) itemStack.unsetData(type);
        else itemStack.setData(type, value);
    }
    protected void setData(@NotNull DataComponentType.NonValued type, @Nullable Boolean value){
        if (value == null) return;
        if (value) itemStack.setData(type);
        else itemStack.resetData(type);
    }
    protected void hideSpecificComponent(@NotNull DataComponentType type){
        TooltipDisplay oldDisplay = itemStack.getDataOrDefault(TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().build());
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay().hideTooltip(oldDisplay.hideTooltip());
        for (DataComponentType oldComponent : oldDisplay.hiddenComponents()) {
            builder.addHiddenComponents(oldComponent);
        }
        builder.addHiddenComponents(type);
        itemStack.setData(TOOLTIP_DISPLAY, builder.build());
    }

    protected void initializeItemStack(){
        itemStack = new ItemStack(getMaterial());
        setPersistentData(itemStack);

        int maxStackSize = CustomItemProperties.getInDataOrInStack(itemStack, MAX_STACK_SIZE, getMaxStackSize(), 1);
        int maxDamage = CustomItemProperties.getInDataOrInStack(itemStack, MAX_DAMAGE, getMaxDamage(), 0);
        if (maxStackSize > 1 && maxDamage > 0){
            throw new RuntimeException("Item can not be stackable and damageable: " + getId());
        }

        setData(MAX_STACK_SIZE, getMaxStackSize());
        setData(MAX_DAMAGE, getMaxDamage());

        setData(ITEM_NAME, getItemName());
        setData(LORE, getLore());
        setData(RARITY, getRarity());
        setData(TOOLTIP_DISPLAY, getTooltipDisplay());
        setData(FOOD, getFood());
        setData(TOOL, getTool());
        setData(CUSTOM_NAME, getDisplayName());
        setData(UNBREAKABLE, getUnbreakable());
        setData(ENCHANTMENT_GLINT_OVERRIDE, getEnchantmentGlintOverride());
        setData(CUSTOM_MODEL_DATA, getCustomModelData());
        setData(DAMAGE_RESISTANT, getDamageResistant());
        setData(TRIM, getTrim());
        setData(INSTRUMENT, getMusicInstrument());
        setData(ATTRIBUTE_MODIFIERS, getAttributeModifiers());
        setData(BLOCK_DATA, getBlockData());
        setData(GLIDER, getGlider());
        setData(ITEM_MODEL, getItemModel());
        setData(USE_REMAINDER, getUseRemainder());
        setData(EQUIPPABLE, getEquippable());
        setData(USE_COOLDOWN, getUseCooldown());
        setData(REPAIRABLE, getRepairable());
        setData(CONSUMABLE, getConsumable());
        setData(POTION_CONTENTS, getPotionContents());
        setData(DYED_COLOR, getDyedColor());
        setData(FIREWORK_EXPLOSION, getFireworkExplosion());
        
        if (getUseRemainderCustom() != null) itemStack.setData(USE_REMAINDER, UseRemainder.useRemainder(getUseRemainderCustom().getItem()));
        if (addDefaultAttributes()) AttributeUtils.addDefaultAttributes(itemStack);

        initializeAdditionalAttributes(itemStack);

        List<ItemFlag> tooltipHides = getTooltipHides();
        if (tooltipHides != null){
            for (ItemFlag flag : tooltipHides) {
                switch (flag){
                    case HIDE_ENCHANTS -> hideSpecificComponent(ENCHANTMENTS);
                    case HIDE_ATTRIBUTES -> hideSpecificComponent(ATTRIBUTE_MODIFIERS);
                    case HIDE_UNBREAKABLE -> hideSpecificComponent(UNBREAKABLE);
                    case HIDE_DESTROYS -> hideSpecificComponent(CAN_BREAK);
                    case HIDE_PLACED_ON -> hideSpecificComponent(CAN_PLACE_ON);
                    case HIDE_DYE -> hideSpecificComponent(DYED_COLOR);
                    case HIDE_ARMOR_TRIM -> hideSpecificComponent(TRIM);
                    case HIDE_STORED_ENCHANTS -> hideSpecificComponent(STORED_ENCHANTMENTS);
                };
            }
        }

        modifyFinalItemStack(itemStack);
    }
    protected void modifyFinalItemStack(@NotNull ItemStack itemStack){}

    protected @NotNull ItemStack getItemNoClone(){
        if (itemStack == null){
            initializeItemStack();
            CustomItemGeneratedEvent event = new CustomItemGeneratedEvent(this, itemStack, getLoreBuilder(), getRepairData());
            event.callEvent();
            event.getLoreBuilder().buildAndApply(event.getItemStack());
            repairData = event.getRepairData();
            setData(REPAIRABLE, getRepairable());
            itemStack = event.getItemStack();
        }
        return itemStack;
    }

    @Override
    public @NotNull ItemStack getItem(){
        return getItemNoClone().clone();
    }
    ///////////////////////////////////////////////////////////////////////////
    // RECIPES
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public @NotNull NamespacedKey getNewRecipeKey() {
        return NamespacedKey.fromString(getId() + "_" + (recipes != null ? Integer.toString(recipes.size()) : "0"));
    }
    @Override
    public void getRecipes(@NotNull Consumer<@NotNull Recipe> consumer) {
        if (recipes == null) return;
        recipes.forEach(consumer);
    }
    @Override
    public void registerRecipe(@NotNull Recipe recipe) {
        if (recipes == null) recipes = new ArrayList<>();
        recipes.add(recipe);
        RecipeManager.getInstance().register(recipe);
    }
    @ForOverride
    protected void generateRecipes(@NotNull Consumer<@NotNull Recipe> consumer){}
}
