package me.udnek.coreu.custom.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@NullMarked public  interface CustomItemProperties{
    Material getMaterial();
    // OPTIONAL
    @Nullable default List<ItemFlag> getTooltipHides(){return null;}

    /////////////////////////
    default CustomItemProperties.@Nullable DataSupplier<ItemAttributeModifiers> getAttributeModifiers(){return null;}  // 2.1 attribute_modifiers

// 2.2 banner_patterns
    // 2.3 base_color
    // 2.4 bees
    // 2.5 block_entity_data
    default CustomItemProperties.@Nullable DataSupplier<BlockItemDataProperties> getBlockData(){return null;}  // 2.6 block_state

// 2.7 bucket_entity_data
    // 2.8 bundle_contents
    // 2.9 can_break
    // 2.10 can_place_on
    // 2.11 charged_projectiles
    default CustomItemProperties.@Nullable DataSupplier<Consumable> getConsumable(){return null;}  // 2.12 consumable

// 2.13 container
    // 2.14 container_loot
    // 2.15 custom_data
    default CustomItemProperties.@Nullable DataSupplier<CustomModelData> getCustomModelData(){return null;}  // 2.16 custom_model_data

    default CustomItemProperties.@Nullable DataSupplier<Component> getDisplayName(){return null;}  // 2.17 custom_name

// 2.18 damage
    default CustomItemProperties.@Nullable DataSupplier<DamageResistant> getDamageResistant(){return null;}  // 2.19 damage_resistant

// 2.20 debug_stick_state
    // todo 2.21 death_protection
    default CustomItemProperties.@Nullable DataSupplier<DyedItemColor> getDyedColor(){return null;}  //2.22 dyed_color

// 2.23 enchantable
    default CustomItemProperties.@Nullable DataSupplier<Boolean> getEnchantmentGlintOverride(){return null;}  // 2.24 enchantment_glint_override

// 2.25 enchantments
    // 2.26 entity_data
    default CustomItemProperties.@Nullable DataSupplier<Equippable> getEquippable(){return null;}  // 2.27 equippable

    default CustomItemProperties.@Nullable DataSupplier<FireworkEffect> getFireworkExplosion(){return null;}  // 2.29 firework_explosion

// 2.30 fireworks
    default CustomItemProperties.@Nullable DataSupplier<FoodProperties> getFood(){return null;} // 2.31 food
@Nullable default Boolean getGlider(){return null;}  // 2.32 glider

    default CustomItemProperties.@Nullable DataSupplier<TooltipDisplay> getTooltipDisplay(){return null;}

    //@Nullable default Boolean getHideAdditionalTooltip(){return null;} // 2.33 hide_additional_tooltip
    //@Nullable default Boolean getHideTooltip(){return null;} // 2.34 hide_tooltip
    default CustomItemProperties.@Nullable DataSupplier<MusicInstrument> getMusicInstrument(){return null;}  // 2.35 instrument

// 2.36 intangible_projectile
    default CustomItemProperties.@Nullable DataSupplier<Key> getItemModel(){return null;}  // 2.37 item_model

    default CustomItemProperties.@Nullable DataSupplier<Component> getItemName(){return null;}// 2.38 item_name
// 2.39 jukebox_playable
     // 2.40 lock

    // 2.41 lodestone_tracker
    default CustomItemProperties.@Nullable DataSupplier<ItemLore> getLore(){return null;}  // 2.42 lore

// 2.43 map_color
    // 2.44 map_decorations
    // 2.45 map_id
    default CustomItemProperties.@Nullable DataSupplier<Integer> getMaxDamage(){return null;}  // 2.46 max_damage

    default CustomItemProperties.@Nullable DataSupplier<Integer> getMaxStackSize(){return null;}  // 2.47 max_stack_size

// 2.48 note_block_sound
    // 2.49 ominous_bottle_amplifier
    // 2.50 pot_decorations
    default CustomItemProperties.@Nullable DataSupplier<PotionContents> getPotionContents(){return null;}  // 2.51 potion_contents

// 2.52 profile
    default CustomItemProperties.@Nullable DataSupplier<ItemRarity> getRarity(){return null;}  // 2.53 rarity

// 2.54 recipes
    default CustomItemProperties.@Nullable DataSupplier<Repairable> getRepairable(){return null;}  // 2.55 repairable

// 2.56 repair_cost
    // 2.57 stored_enchantments
    // 2.58 suspicious_stew_effects
    default CustomItemProperties.@Nullable DataSupplier<Tool> getTool(){return null;}  // 2.59 tool

// 2.60 tooltip_style
    default CustomItemProperties.@Nullable DataSupplier<ItemArmorTrim> getTrim(){return null;} // 2.61 trim
@Nullable default Boolean getUnbreakable(){return null;}  // 2.62 unbreakable

    default CustomItemProperties.@Nullable DataSupplier<UseCooldown> getUseCooldown(){return null;}  // 2.63 use_cooldown

    default CustomItemProperties.@Nullable DataSupplier<UseRemainder> getUseRemainder(){return null;}  // 2.64 use_remainder

    default CustomItemProperties.@Nullable DataSupplier<Weapon> getWeapon(){return null;}

    default CustomItemProperties.@Nullable DataSupplier<AttackRange> getAttackRange(){return null;}

    default CustomItemProperties.@Nullable DataSupplier<BlocksAttacks> getBlocksAttacks(){return null;}

    default CustomItemProperties.@Nullable DataSupplier<UseEffects> getUseEffects(){return null;}

    default CustomItemProperties.@Nullable DataSupplier<KineticWeapon> getKineticWeapon(){return null;}

    default CustomItemProperties.@Nullable DataSupplier<PiercingWeapon> getPiercingWeapon(){return null;}

    default CustomItemProperties.@Nullable DataSupplier<SwingAnimation> getSwingAnimation(){return null;}


    static <Value, Type extends DataComponentType.Valued<Value>> Value getInDataOrInStack(ItemStack itemStack, Type type, CustomItemProperties.@Nullable DataSupplier<Value> dataSupplier, @NonNull Value fallback){
        if (dataSupplier == null) return itemStack.getDataOrDefault(type, fallback);
        Value value = dataSupplier.get();
        return value == null ? fallback : value;
    }

    class DataSupplier<T> implements Supplier<T> {

        protected final @Nullable T data;

        private DataSupplier(@Nullable T object){
            this.data = object;
        }

        @Override
        public @Nullable T get() {
            return data;
        }

        public static <T> DataSupplier<T> of(@Nullable T data){
            return new DataSupplier<>(data);
        }
    }
}
