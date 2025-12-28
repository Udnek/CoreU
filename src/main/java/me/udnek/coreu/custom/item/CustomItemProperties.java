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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public interface CustomItemProperties {
    @NotNull Material getMaterial();
    // OPTIONAL
    @Nullable default List<ItemFlag> getTooltipHides(){return null;}
    /////////////////////////
    // COMPONENTS
    /////////////////////////
    @Nullable default DataSupplier<ItemAttributeModifiers> getAttributeModifiers(){return null;} // 2.1 attribute_modifiers
    // 2.2 banner_patterns
    // 2.3 base_color
    // 2.4 bees
    // 2.5 block_entity_data
    @Nullable default DataSupplier<BlockItemDataProperties> getBlockData(){return null;} // 2.6 block_state
    // 2.7 bucket_entity_data
    // 2.8 bundle_contents
    // 2.9 can_break
    // 2.10 can_place_on
    // 2.11 charged_projectiles
    @Nullable default DataSupplier<Consumable> getConsumable(){return null;} // 2.12 consumable
    // 2.13 container
    // 2.14 container_loot
    // 2.15 custom_data
    @Nullable default DataSupplier<CustomModelData> getCustomModelData(){return null;} // 2.16 custom_model_data
    @Nullable default DataSupplier<Component> getDisplayName(){return null;} // 2.17 custom_name
    // 2.18 damage
    @Nullable default DataSupplier<DamageResistant> getDamageResistant(){return null;} // 2.19 damage_resistant
    // 2.20 debug_stick_state
    // todo 2.21 death_protection
    @Nullable default DataSupplier<DyedItemColor> getDyedColor(){return null;} //2.22 dyed_color
    // 2.23 enchantable
    @Nullable default DataSupplier<Boolean> getEnchantmentGlintOverride(){return null;} // 2.24 enchantment_glint_override
    // 2.25 enchantments
    // 2.26 entity_data
    @Nullable default DataSupplier<Equippable> getEquippable(){return null;} // 2.27 equippable
    @Nullable default DataSupplier<FireworkEffect> getFireworkExplosion(){return null;} // 2.29 firework_explosion
    // 2.30 fireworks
    @Nullable default DataSupplier<FoodProperties> getFood(){return null;} // 2.31 food
    @Nullable default Boolean getGlider(){return null;} // 2.32 glider
    @Nullable default DataSupplier<TooltipDisplay> getTooltipDisplay(){return null;}
    //@Nullable default Boolean getHideAdditionalTooltip(){return null;} // 2.33 hide_additional_tooltip
    //@Nullable default Boolean getHideTooltip(){return null;} // 2.34 hide_tooltip
    @Nullable default DataSupplier<MusicInstrument> getMusicInstrument(){return null;} // 2.35 instrument
    // 2.36 intangible_projectile
    @Nullable default DataSupplier<Key> getItemModel(){return null;} // 2.37 item_model
    @Nullable default DataSupplier<Component> getItemName(){return null;}// 2.38 item_name
    // 2.39 jukebox_playable
    // 2.40 lock
    // 2.41 lodestone_tracker
    @Nullable default DataSupplier<ItemLore> getLore(){return null;} // 2.42 lore
    // 2.43 map_color
    // 2.44 map_decorations
    // 2.45 map_id
    @Nullable default DataSupplier<Integer> getMaxDamage(){return null;} // 2.46 max_damage
    @Nullable default DataSupplier<Integer> getMaxStackSize(){return null;} // 2.47 max_stack_size
    // 2.48 note_block_sound
    // 2.49 ominous_bottle_amplifier
    // 2.50 pot_decorations
    @Nullable default DataSupplier<PotionContents> getPotionContents(){return null;} // 2.51 potion_contents
    // 2.52 profile
    @Nullable default DataSupplier<ItemRarity> getRarity(){return null;} // 2.53 rarity
    // 2.54 recipes
    @Nullable default DataSupplier<Repairable> getRepairable(){return null;} // 2.55 repairable
    // 2.56 repair_cost
    // 2.57 stored_enchantments
    // 2.58 suspicious_stew_effects
    @Nullable default DataSupplier<Tool> getTool(){return null;} // 2.59 tool
    // 2.60 tooltip_style
    @Nullable default DataSupplier<ItemArmorTrim> getTrim(){return null;} // 2.61 trim
    @Nullable default Boolean getUnbreakable(){return null;} // 2.62 unbreakable
    @Nullable default DataSupplier<UseCooldown> getUseCooldown(){return null;} // 2.63 use_cooldown
    @Nullable default DataSupplier<UseRemainder> getUseRemainder(){return null;} // 2.64 use_remainder
    // 2.65 writable_book_content
    // 2.66 written_book_content

    @Nullable default DataSupplier<Weapon> getWeapon(){return null;}
    @Nullable default DataSupplier<AttackRange> getAttackRange(){return null;}
    @Nullable default DataSupplier<BlocksAttacks> getBlocksAttacks(){return null;}
    @Nullable default DataSupplier<UseEffects> getUseEffects(){return null;}
    @Nullable default DataSupplier<KineticWeapon> getKineticWeapon(){return null;}
    @Nullable default DataSupplier<PiercingWeapon> getPiercingWeapon(){return null;}
    @Nullable default DataSupplier<SwingAnimation> getSwingAnimation(){return null;}


    static <Value, Type extends DataComponentType.Valued<@NotNull Value>> @NotNull Value getInDataOrInStack(@NotNull ItemStack itemStack, @NotNull Type type, @Nullable DataSupplier<Value> dataSupplier, @NotNull Value fallback){
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

        public static <T> @NotNull DataSupplier<T> of(@Nullable T data){
            return new DataSupplier<>(data);
        }
    }

    interface AttributeConsumer{
        void consume(@NotNull Attribute attribute, @NotNull AttributeModifier modifier);
    }
}
