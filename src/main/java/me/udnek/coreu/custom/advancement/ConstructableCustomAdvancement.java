package me.udnek.coreu.custom.advancement;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.Identifier;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConstructableCustomAdvancement implements CustomAdvancementContainer {
    protected boolean registered = false;
    protected @Nullable CustomAdvancementContainer parent;
    protected AdvancementHolder itself = null;
    protected final @NotNull Key key;
    protected @Nullable CustomAdvancementDisplayBuilder display;
    protected Set<@NotNull CustomAdvancementContainer> fakes = new HashSet<>();

    @NotNull Map<String, Criterion<?>> criteria = new HashMap<>();
    @NotNull AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;

    public ConstructableCustomAdvancement(@NotNull Key key){
        this.key = key;
    }

    public ConstructableCustomAdvancement(@NotNull Key key, @NotNull ConstructableCustomAdvancement other){
        this(key);
        this.parent = other.parent;
        this.display= other.display == null ? null : other.display.clone();
        this.criteria = new HashMap<>(other.criteria);
        this.requirementsStrategy = other.requirementsStrategy;
    }

    @Override
    public @NotNull ConstructableCustomAdvancement copy(@NotNull Key key) {
        return new ConstructableCustomAdvancement(key, this);
    }

    public void display(@Nullable CustomAdvancementDisplayBuilder display){
        this.display = display;
    }
    @Override
    public void setParent(@Nullable CustomAdvancementContainer parent){
        this.parent = parent;
    }


    public void addFakeParent(@NotNull CustomAdvancementContainer parent){
        ConstructableCustomAdvancement fake = copy(NamespacedKey.fromString(key.asString() + "_fake"));
        fake.setParent(parent);
        fake.getDisplay().showToast(false).announceToChat(false);
        fakes.add(fake);
    }
    public void addCriterion(@NotNull AdvancementCriterion criterion){
        addCriterion(Integer.toString(criteria.size()), criterion);
    }
    public void addCriterion(@NotNull String name, @NotNull AdvancementCriterion criterion){
        criteria.put(name, criterion.get());
    }
    public void removeCriterion(@NotNull String name){criteria.remove(name);}

    public void requirementsStrategy(@NotNull RequirementsStrategy strategy){requirementsStrategy = strategy.get();}
    @Override
    public @Nullable CustomAdvancementDisplayBuilder getDisplay() {return display;}
    @Override
    public @NotNull Set<@NotNull CustomAdvancementContainer> getFakes() {return fakes;}

    @Override
    public @NotNull AdvancementHolder get(){
        if (itself == null){
            Advancement.Builder builder = new Advancement.Builder();
            builder.display(display == null ? null : display.build());
            if (parent != null) builder.parent(parent.get());
            builder.requirements(requirementsStrategy);
            if (criteria.isEmpty()) addCriterion("auto_added_by_builder", AdvancementCriterion.IMPOSSIBLE);
            criteria.forEach(builder::addCriterion);

            itself = builder.build(Identifier.parse(key.asString()));
        }

        return itself;
    }


    public void register(){
        Preconditions.checkArgument(!registered, "Advancement already registered!");
        CustomAdvancementUtils.register(this);
        registered = true;
    }
}