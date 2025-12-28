package me.udnek.coreu.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class LoreBuilder {

    Map<@NotNull Integer, @NotNull Componentable> lore = new HashMap<>();

    public LoreBuilder(){}

    public void add(int priority, @NotNull Component line){
        Componentable components = lore.getOrDefault(priority, new Componentable.Simple());
        components.add(line);
        lore.put(priority, components);
    }
    public void add(@NotNull Position position, @NotNull Component line){
        add(position.priority, line);
    }
    public void set(int priority, @Nullable Componentable componentable){
        if (componentable == null) lore.remove(priority);
        else lore.put(priority, componentable);
    }
    public void set(@NotNull Position position, @Nullable Componentable componentable){
        set(position.priority, componentable);
    }
    public @Nullable Componentable get(int priority){
        return lore.get(priority);
    }
    public @Nullable Componentable get(@NotNull Position position){
        return get(position.priority);
    }
    public boolean isEmpty(){
        return lore.values().stream().allMatch(Componentable::isEmpty);
    }
    public void clear(){lore.clear();}

    public @NotNull List<Component> build(){
        TreeMap<Integer, Componentable> sorted = new TreeMap<>(Integer::compare);
        sorted.putAll(lore);
        List<Component> finalLore = new ArrayList<>();
        sorted.values().forEach(componentable -> componentable.toComponents(finalLore::add));
        return finalLore;
    }

    public void buildAndApply(@NotNull ItemStack itemStack){
        List<Component> lore = LoreBuilder.this.build();
        if (lore.isEmpty()) return;
        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
    }

    public enum Position{
        ENCHANTMENTS(0),
        VANILLA_LORE(100),
        ATTRIBUTES(200),
        BACKSTORY(300),
        ID(400);

        public final int priority;
        Position(int priority){
            this.priority = priority;
        }
    }

    public interface Componentable{
        void toComponents(@NotNull Consumer<Component> consumer);
        void add(@NotNull Component component);
        void addFirst(@NotNull Component component);
        boolean isEmpty();

        class Simple implements Componentable{

            List<Component> components = new ArrayList<>();

            @Override
            public void toComponents(@NotNull Consumer<Component> consumer) {
                components.forEach(consumer);
            }

            @Override
            public void add(@NotNull Component component) {
                components.add(component);
            }

            @Override
            public void addFirst(@NotNull Component component) {
                components.addFirst(component);
            }

            @Override
            public boolean isEmpty() {
                return components.isEmpty();
            }
        }
    }

}
