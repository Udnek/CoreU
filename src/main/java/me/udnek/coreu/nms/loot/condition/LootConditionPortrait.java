package me.udnek.coreu.nms.loot.condition;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class LootConditionPortrait {

    public @NotNull Set<Structure> structures = new HashSet<>();
    public @NotNull Set<Biome> biomes = new HashSet<>();
    public @NotNull Set<EntityType> vehicles = new HashSet<>();
    public @Nullable Float randomChance;
    public @Nullable Float unenchantedRandomChance;
}