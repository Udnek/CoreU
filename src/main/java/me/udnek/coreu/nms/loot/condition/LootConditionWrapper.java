package me.udnek.coreu.nms.loot.condition;

import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.NmsWrapper;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.predicates.*;
import org.apache.commons.lang3.Range;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LootConditionWrapper implements NmsWrapper<@NotNull LootItemCondition> {

    public static @NotNull List<LootConditionWrapper> wrap(@NotNull List<LootItemCondition> conditions) {
        return conditions.stream().map(LootConditionWrapper::new).toList();
    }

    public static @NotNull List<LootItemCondition> unwrap(@NotNull List<LootConditionWrapper> functions) {
        return functions.stream().map(LootConditionWrapper::getNms).toList();
    }

    public static @NotNull LootConditionWrapper time(@NotNull Range<Integer> range, @Nullable Long period){
        return new LootConditionWrapper(new TimeCheck(
                Optional.ofNullable(period), IntRange.range(range.getMinimum(), range.getMaximum()))
        );
    }

    public static @NotNull LootConditionWrapper structure(@NotNull Key structure){
        return new LootConditionWrapper(new LocationCheck(
                Optional.of(LocationPredicate.Builder.inStructure(NmsUtils.toNms(Registries.STRUCTURE, structure)).build()),
                BlockPos.ZERO
        ));
    }
    public static @NotNull LootConditionWrapper biome(@NotNull Biome biome){
        return new LootConditionWrapper(new LocationCheck(
                Optional.of(LocationPredicate.Builder.inBiome(NmsUtils.toNms(Registries.BIOME, biome)).build()),
                BlockPos.ZERO
        ));
    }

    protected final LootItemCondition condition;

    public LootConditionWrapper(@NotNull LootItemCondition condition) {
        this.condition = condition;
    }

    public @NotNull LootConditionPortrait getPortrait(){
        LootConditionPortrait portrait = new LootConditionPortrait();
        switch (condition) {
            case TimeCheck time -> portrait.period = time.period().orElse(null);

            // todo
//            IntRange value = time.value();
//            portrait.range = Range.of(value);
            case LocationCheck locationCheck -> {
                LocationPredicate predicate = locationCheck.predicate().orElse(null);
                if (predicate == null) return portrait;
                predicate.biomes().ifPresent(holders -> {
                    holders.forEach(biomeHolder -> {
                        portrait.biomes.add(CraftBiome.minecraftHolderToBukkit(biomeHolder));
                    });
                });
                predicate.structures().ifPresent(holders -> {
                    holders.forEach(structureHolder -> {
                        portrait.structures.add(CraftStructure.minecraftHolderToBukkit(structureHolder));
                    });
                });
            }
            case LootItemRandomChanceCondition randomChanceCondition ->
                    portrait.randomChance = NmsUtils.averageFromNumberProvider(randomChanceCondition.chance(), 1);
            case LootItemRandomChanceWithEnchantedBonusCondition randomChanceCondition ->
                    portrait.unenchantedRandomChance = randomChanceCondition.unenchantedChance();
            default -> {
            }
        }
        return portrait;
    }

    @Override
    public @NotNull LootItemCondition getNms() {
        return condition;
    }

    @Override
    public String toString() {
        return condition.toString();
    }
}
