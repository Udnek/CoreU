package me.udnek.coreu.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class LoreBuilder{

    Map<Integer, Componentable> lore = new HashMap<>();

    public LoreBuilder(){}

    public void add(int priority, Component line){
        Componentable components = lore.getOrDefault(priority, new Componentable.Simple());
        components.add(line);
        lore.put(priority, components);
    }
    public void add(Position position, Component line){
        add(position.priority, line);
    }
    public void set(int priority, @Nullable LoreBuilder.Componentable componentable){
        if (componentable == null) lore.remove(priority);
        else lore.put(priority, componentable);
    }
    public void set(Position position, @Nullable LoreBuilder.Componentable componentable){
        set(position.priority, componentable);
    }
    public @Nullable LoreBuilder.Componentable get(int priority){
        return lore.get(priority);
    }
    public @Nullable LoreBuilder.Componentable get(Position position){
        return get(position.priority);
    }
    public boolean isEmpty(){
        return lore.values().stream().allMatch(Componentable::isEmpty);
    }
    public void clear(){lore.clear();}

    public List<Component> build(){
        TreeMap<Integer, Componentable> sorted = new TreeMap<>(Integer::compare);
        sorted.putAll(lore);
        List<Component> finalLore = new ArrayList<>();
        sorted.values().forEach(componentable -> componentable.toComponents(finalLore::add));
        return finalLore;
    }

    public void buildAndApply(ItemStack itemStack){
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
        void toComponents(Consumer<Component> consumer);
        void add(Component component);
        void addFirst(Component component);
        boolean isEmpty();

        class Simple implements Componentable{

            List<Component> components = new ArrayList<>();

            @Override
            public void toComponents(Consumer<Component> consumer) {
                components.forEach(consumer);
            }

            @Override
            public void add(Component component) {
                components.add(component);
            }

            @Override
            public void addFirst(Component component) {
                components.addFirst(component);
            }

            @Override
            public boolean isEmpty() {
                return components.isEmpty();
            }
        }
    }

}
