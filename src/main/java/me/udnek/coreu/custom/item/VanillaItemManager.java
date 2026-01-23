package me.udnek.coreu.custom.item;

import com.google.common.base.Preconditions;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.recipe.CustomRecipe;
import me.udnek.coreu.custom.recipe.RecipeManager;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.nms.Nms;
import me.udnek.coreu.nms.loot.entry.NmsCustomEntry;
import me.udnek.coreu.nms.loot.util.ItemStackCreator;
import me.udnek.coreu.util.LogUtils;
import me.udnek.coreu.util.SelfRegisteringListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.*;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class VanillaItemManager extends SelfRegisteringListener {

    private static final EquipmentSlot[] ENTITY_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND};
    private static VanillaItemManager instance;

    public static VanillaItemManager getInstance() {
        if (instance == null) instance = new VanillaItemManager();
        return instance;
    }

    private final Set<Material> disabled = new HashSet<>();
    private final EnumMap<Material, VanillaBasedCustomItem> replacedByMaterial = new EnumMap<>(Material.class);
    private final List<VanillaBasedCustomItem> replacedItems = new ArrayList<>();

    private VanillaItemManager(){
        super(CoreU.getInstance());
    }

    public void disableVanillaMaterial(@NotNull Material material){
        if (isReplaced(material)) throw new RuntimeException("Disabling material that was replaced: " + material);
        disabled.add(material);
    }
    public void replaceVanillaMaterial(@NotNull Material material){
        if (isDisabled(material)) throw new RuntimeException("Replacing material that was disabled: " + material);
        if (isReplaced(material)) return;
        VanillaBasedCustomItem customItem = new VanillaBasedCustomItem(material);
        replacedByMaterial.put(material, customItem);
        replacedItems.add(customItem);
        CustomRegistries.ITEM.register(CoreU.getInstance(), customItem);
    }

    public void start(){
        startDisabler();
        startReplacer();
    }

    private void startDisabler(){
        RecipeManager recipeManager = RecipeManager.getInstance();
        for (Material material : disabled) {

            ItemStack toRemoveItem = new ItemStack(material);

            // recipe removal
            ArrayList<Recipe> recipes = new ArrayList<>();
            recipeManager.getRecipesAsIngredient(toRemoveItem, recipes::add);
            recipeManager.getRecipesAsResult(toRemoveItem, recipes::add);

            for (Recipe recipe : recipes) {
                recipeManager.unregister(recipe);
                Recipe newRecipe = copyRecipeWithRemovedItem(recipe, material);
                if (newRecipe != null) recipeManager.register(newRecipe);
            }

            // loot table removal
            for (LootTable lootTable : ItemUtils.getWhereItemOccurs(toRemoveItem)) {
                Nms.get().removeAllEntriesContains(lootTable, VanillaItemManager::isDisabled);
            }

            LogUtils.pluginLog("Disabled: " + material);
        }
    }

    private void startReplacer(){
        RecipeManager recipeManager = RecipeManager.getInstance();
        for (Map.Entry<Material, VanillaBasedCustomItem> entry : replacedByMaterial.entrySet()) {
            VanillaBasedCustomItem newItem = entry.getValue();
            Material oldMaterial = entry.getKey();

            ItemStack oldItem = new ItemStack(oldMaterial);

            // recipe replace
            List<Recipe> recipes = new ArrayList<>();
            recipeManager.getRecipesAsIngredient(oldItem, recipes::add);
            recipeManager.getRecipesAsResult(oldItem, recipes::add);

            for (Recipe oldRecipe : recipes) {
                if (oldRecipe instanceof CustomRecipe customRecipe){
                    customRecipe.replaceItem(oldItem, newItem.getItem());
                } else {
                    recipeManager.unregister(oldRecipe);
                    Recipe newRecipe = copyRecipeWithReplacedItem(oldRecipe, oldMaterial, newItem);
                    recipeManager.register(newRecipe);
                }
            }

            // loot table replace
            for (LootTable lootTable : ItemUtils.getWhereItemOccurs(oldItem)) {
                Predicate<ItemStack> predicate = itemStack -> CustomItem.get(itemStack) == newItem;
                Nms.get().replaceAllEntriesContains(
                        lootTable,
                        predicate,
                        new NmsCustomEntry.Builder(new ItemStackCreator.Custom(newItem)).fromVanilla(lootTable, predicate)
                );
            }

            LogUtils.pluginLog("Replaced: " + oldMaterial);
        }
    }

    public static boolean isDisabled(@NotNull Material material){
        return getInstance().disabled.contains(material);
    }
    public static boolean isDisabled(@NotNull ItemStack item){
        if (CustomItem.isCustom(item)) return false;
        return isDisabled(item.getType());
    }
    public static boolean isReplaced(@NotNull ItemStack itemStack){
        CustomItem customItem = CustomItem.get(itemStack);
        if (customItem == null) return false;
        return isReplaced(customItem);
    }
    public static boolean isReplaced(@NotNull CustomItem customItem){
        return getInstance().replacedItems.contains(customItem);
    }
    public static boolean isReplaced(@NotNull Material material){
        return getInstance().replacedByMaterial.containsKey(material);
    }
    public static @NotNull ItemStack replace(@NotNull ItemStack itemStack){
        if (!isReplaced(itemStack)) return itemStack;
        return getInstance().replacedByMaterial.get(itemStack.getType()).update(itemStack);
    }
    public static @Nullable VanillaBasedCustomItem getReplaced(@NotNull ItemStack itemStack){
        if (!isReplaced(itemStack)) return null;
        return getInstance().replacedByMaterial.get(itemStack.getType());
    }
    public static @Nullable VanillaBasedCustomItem getReplaced(@NotNull Material material){
        return getInstance().replacedByMaterial.get(material);
    }

    // EVENTS

    @EventHandler
    public void onSpawn(EntitySpawnEvent event){
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        EntityEquipment equipment = livingEntity.getEquipment();
        if (equipment == null) return;
        for (EquipmentSlot slot : ENTITY_SLOTS) {
            ItemStack item = equipment.getItem(slot);
            if (isDisabled(item)) equipment.setItem(slot, null);
            else if (isReplaced(item)) equipment.setItem(slot, replace(item));
        }
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent event){
        ItemStack item = event.getCursor();
        if (isDisabled(item)){
            event.setCancelled(true);
            event.getViewers().getFirst().sendMessage(Component.text("Item is disabled!").color(NamedTextColor.RED));
        } else if (isReplaced(item)){
            event.getViewers().getFirst().sendMessage(Component.text("Item is replaced!").color(NamedTextColor.GREEN));
            event.setCursor(replace(item));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNewTrade(VillagerAcquireTradeEvent event){
        MerchantRecipe recipe = event.getRecipe();

        ItemStack result = recipe.getResult();
        if (isDisabled(result)) {event.setCancelled(true); return;}
        if (isReplaced(result)){
            recipe = new MerchantRecipe(
                    replace(result),
                    recipe.getUses(),
                    recipe.getMaxUses(),
                    recipe.hasExperienceReward(),
                    recipe.getVillagerExperience(),
                    recipe.getPriceMultiplier(),
                    recipe.getDemand(),
                    recipe.getSpecialPrice(),
                    recipe.shouldIgnoreDiscounts()
            );
            recipe.setIngredients(event.getRecipe().getIngredients());
        }

        List<ItemStack> newIngredients = new ArrayList<>();
        for (ItemStack ingredient : recipe.getIngredients()) {
            if (isReplaced(ingredient)) newIngredients.add(replace(ingredient));
            else if (!isDisabled(ingredient)) newIngredients.add(ingredient);
        }

        if (newIngredients.isEmpty()){event.setCancelled(true);return;}

        recipe.setIngredients(newIngredients);
        event.setRecipe(recipe);
    }

    public @Nullable Recipe copyRecipeWithReplacedItem(@NotNull Recipe abstractRecipe, @NotNull NotNullToNullFunction<ItemStack> resultFunction, @NotNull NotNullToNullFunction<RecipeChoice> choiceFunction){
        Preconditions.checkArgument(!(abstractRecipe instanceof CustomRecipe), "Custom recipes are not allowed!");
        ItemStack result = resultFunction.apply(abstractRecipe.getResult());
        if (result == null) return null;
        switch (abstractRecipe) {
            case CookingRecipe<?> cookingRecipe -> {
                RecipeChoice choice = choiceFunction.apply(cookingRecipe.getInputChoice());
                if (choice == null) return null;
                switch (cookingRecipe) {
                    case BlastingRecipe recipe -> {
                        return new BlastingRecipe(cookingRecipe.getKey(), result, choice, cookingRecipe.getExperience(), cookingRecipe.getCookingTime());
                    }
                    case FurnaceRecipe recipe -> {
                        return new FurnaceRecipe(cookingRecipe.getKey(), result, choice, cookingRecipe.getExperience(), cookingRecipe.getCookingTime());
                    }
                    case SmokingRecipe recipe -> {
                        return new SmokingRecipe(cookingRecipe.getKey(), result, choice, cookingRecipe.getExperience(), cookingRecipe.getCookingTime());
                    }
                    case CampfireRecipe recipe -> {
                        return new CampfireRecipe(cookingRecipe.getKey(), result, choice, cookingRecipe.getExperience(), cookingRecipe.getCookingTime());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + cookingRecipe);
                }

            }
            case ShapedRecipe recipe -> {
                ShapedRecipe newRecipe = new ShapedRecipe(recipe.getKey(), result);
                newRecipe.shape(recipe.getShape());

                for (Map.Entry<Character, RecipeChoice> entry : recipe.getChoiceMap().entrySet()) {
                    if (entry.getValue() == null) continue; // BUG WITH LEATHER BOOTS
                    RecipeChoice choice = choiceFunction.apply(entry.getValue());
                    if (choice == null) return null;
                    newRecipe.setIngredient(entry.getKey(), choice);
                }
                return newRecipe;
            }
            case ShapelessRecipe recipe -> {
                ShapelessRecipe newRecipe = new ShapelessRecipe(recipe.getKey(), result);
                for (RecipeChoice choice : recipe.getChoiceList()) {
                    RecipeChoice newChoice = choiceFunction.apply(choice);
                    if (newChoice == null) return null;
                    newRecipe.addIngredient(newChoice);
                }
                return newRecipe;
            }
            case SmithingRecipe smithingRecipe -> {
                RecipeChoice base = choiceFunction.apply(smithingRecipe.getBase());
                RecipeChoice addition = choiceFunction.apply(smithingRecipe.getAddition());
                if (base == null || addition == null) return null;
                switch (smithingRecipe) {
                    case SmithingTransformRecipe recipe -> {
                        RecipeChoice template = choiceFunction.apply(recipe.getTemplate());
                        if (template == null) return null;
                        return new SmithingTransformRecipe(recipe.getKey(), result, template, base, addition, recipe.willCopyDataComponents());
                    }
                    case SmithingTrimRecipe recipe -> {
                        RecipeChoice template = choiceFunction.apply(recipe.getTemplate());
                        if (template == null) return null;
                        return new SmithingTrimRecipe(recipe.getKey(), template, base, addition, recipe.getTrimPattern(),  recipe.willCopyDataComponents());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + smithingRecipe);
                }
            }
            case StonecuttingRecipe recipe -> {
                RecipeChoice choice = choiceFunction.apply(recipe.getInputChoice());
                if (choice == null) return null;
                return new StonecuttingRecipe(recipe.getKey(), result, choice);
            }
            default -> {}
        }
        throw new IllegalArgumentException("Replacer does not support recipe: " + abstractRecipe);
    }


    public @NotNull Recipe copyRecipeWithReplacedItem(@NotNull Recipe recipe, @NotNull Material oldMaterial, @NotNull VanillaBasedCustomItem newItem){
        return Objects.requireNonNull(copyRecipeWithReplacedItem(recipe, new NotNullToNullFunction<ItemStack>() {
            @Override
            public @NotNull ItemStack apply(@NotNull ItemStack itemStack) {
                return VanillaItemManager.isReplaced(itemStack) && itemStack.getType() == oldMaterial ? newItem.update(itemStack) : itemStack;
            }
        }, new NotNullToNullFunction<RecipeChoice>() {
            @Override
            public @Nullable RecipeChoice apply(@NotNull RecipeChoice recipeChoice) {
                if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    return materialChoice;
                } else if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
                    List<ItemStack> newStacks = new ArrayList<>();
                    for (ItemStack choice : exactChoice.getChoices()) {
                        if (ItemUtils.isVanillaMaterial(choice, oldMaterial)) newStacks.add(newItem.getItem());
                        else newStacks.add(choice);
                    }
                    if (newStacks.isEmpty()) return null;
                    return new RecipeChoice.ExactChoice(newStacks);
                }
                LogUtils.pluginWarning("Recipe choice is not (Material or Exact): " + recipeChoice);
                return recipeChoice;
            }
        }));
    }

    public @Nullable Recipe copyRecipeWithRemovedItem(@NotNull Recipe recipe, @NotNull Material toRemove){
        return copyRecipeWithReplacedItem(recipe, new NotNullToNullFunction<ItemStack>() {
            @Override
            public @Nullable ItemStack apply(@NotNull ItemStack itemStack) {
                return ItemUtils.isVanillaMaterial(itemStack, toRemove) ? null : itemStack;
            }
        }, new NotNullToNullFunction<RecipeChoice>() {
            @Override
            public @Nullable RecipeChoice apply(@NotNull RecipeChoice recipeChoice) {
                if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice){
                    List<Material> newMaterials = new ArrayList<>();
                    for (Material choice : materialChoice.getChoices()) {
                        if (choice != toRemove) newMaterials.add(choice);
                    }
                    if (newMaterials.isEmpty()) return null;
                    return new RecipeChoice.MaterialChoice(newMaterials);
                } else if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
                    List<ItemStack> newStacks = new ArrayList<>();
                    for (ItemStack choice : exactChoice.getChoices()) {
                        if (!ItemUtils.isSameIds(choice, new ItemStack(toRemove))) newStacks.add(choice);
                    }
                    if (newStacks.isEmpty()) return null;
                    return new RecipeChoice.ExactChoice(newStacks);
                }
                LogUtils.pluginWarning("Recipe choice is not Material or Exact or Empty: " + recipeChoice);
                return recipeChoice;
            }
        });
    }

    public interface NotNullToNullFunction<T> extends Function<T, T>{
        @Override
        @Nullable T apply(@NotNull T t);
    }
}
