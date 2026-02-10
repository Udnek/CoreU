package me.udnek.coreu.nms.loot.condition;

import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.NmsWrapper;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.Range;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.generator.structure.CraftStructure;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@org.jspecify.annotations.NullMarked public class LootConditionWrapper implements NmsWrapper<LootItemCondition> {

    public static List<LootConditionWrapper> wrap(List<LootItemCondition> conditions) {
        return conditions.stream().map(LootConditionWrapper::new).toList();
    }

    public static List<LootItemCondition> unwrap(List<LootConditionWrapper> functions) {
        return functions.stream().map(LootConditionWrapper::getNms).toList();
    }

    public static LootConditionWrapper randomChange(@org.jetbrains.annotations.Range(from = 0, to = 1) float chance){
        return new LootConditionWrapper(new LootItemRandomChanceCondition(new ConstantValue(chance)));
    }

    public static LootConditionWrapper time(Range<Integer> range, @Nullable Long period){
        return new LootConditionWrapper(new TimeCheck(
                Optional.ofNullable(period), IntRange.range(range.getMinimum(), range.getMaximum()))
        );
    }

    public static LootConditionWrapper structure(Set<Key> structures){
        LocationPredicate.Builder location = LocationPredicate.Builder.location();
        location.setStructures(NmsUtils.toNms(Registries.STRUCTURE, structures));
        return new LootConditionWrapper(new LocationCheck(
                Optional.of(location.build()),
                BlockPos.ZERO
        ));
    }
    public static LootConditionWrapper biome(Set<Key> biomes){
        LocationPredicate.Builder location = LocationPredicate.Builder.location();
        location.setBiomes(NmsUtils.toNms(Registries.BIOME, biomes));
        return new LootConditionWrapper(new LocationCheck(
                Optional.of(location.build()),
                BlockPos.ZERO
        ));
    }

    protected final LootItemCondition condition;

    public LootConditionWrapper(LootItemCondition condition) {
        this.condition = condition;
    }

    public LootConditionPortrait getPortrait(){
        LootConditionPortrait portrait = new LootConditionPortrait();
        switch (condition) {
            case TimeCheck time -> {}// todo

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

            case MatchTool matchTool -> {
                // tool enchantments
//                matchTool.predicate()
//                        .flatMap(predicate -> predicate.)
//                        .ifPresent(
//                                typePredicate -> typePredicate.types()
//                                        .forEach(type -> portrait.attackers.add(CraftEntityType.minecraftToBukkit(type.value()))
//                                        ));
            }

            case LootItemEntityPropertyCondition entityCondition -> {
                // vehicle
                if (entityCondition.entityTarget() == LootContext.EntityTarget.THIS){
                    entityCondition.predicate()
                            .flatMap(EntityPredicate::vehicle)
                            .flatMap(EntityPredicate::entityType)
                            .ifPresent(
                                    typePredicate -> typePredicate.types()
                                            .forEach(type -> portrait.vehicles.add(CraftEntityType.minecraftToBukkit(type.value()))
                                            ));
                }

                // killer
                if (entityCondition.entityTarget() == LootContext.EntityTarget.ATTACKER){
                    entityCondition.predicate()
                            .flatMap(EntityPredicate::entityType)
                            .ifPresent(
                                    typePredicate -> typePredicate.types()
                                            .forEach(type -> portrait.attackers.add(CraftEntityType.minecraftToBukkit(type.value()))
                                            ));
                }
            }
            default -> {
            }
        }
        return portrait;
    }

    @Override
    public LootItemCondition getNms() {
        return condition;
    }

    @Override
    public String toString() {
        return condition.toString();
    }
}
