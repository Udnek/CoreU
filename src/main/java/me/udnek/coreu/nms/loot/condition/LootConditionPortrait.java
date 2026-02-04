package me.udnek.coreu.nms.loot.condition;

import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@org.jspecify.annotations.NullMarked public class LootConditionPortrait{

    public Set<Structure> structures = new HashSet<>();
    public Set<Biome> biomes = new HashSet<>();
    public Set<EntityType> vehicles = new HashSet<>();
    public @Nullable Float randomChance;
    public @Nullable Float unenchantedRandomChance;
}