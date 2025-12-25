package me.udnek.coreu.rpgu.component.ability.property.function;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiLineDescription {

    public static @NotNull MultiLineDescription of(@NotNull Component component){
        return new MultiLineDescription().add(component);
    }

    public static @NotNull MultiLineDescription of(){
        return new MultiLineDescription();
    }

    protected List<Component> lines = new ArrayList<>();

    protected MultiLineDescription(){}

    public @NotNull MultiLineDescription add(@NotNull MultiLineDescription other){
        if (other.lines.isEmpty()) return this;
        add(other.lines.getFirst());
        for (Component component : other.lines.subList(1, other.lines.size())) {
            addLine(component);
        }
        return this;
    }

    public @NotNull MultiLineDescription addToBeginning(@NotNull Component component){
        if (lines.isEmpty()) lines.add(Component.empty());
        lines.set(0, component.append(lines.getFirst()));
        return this;
    }
    public @NotNull MultiLineDescription add(@NotNull Component component){
        if (lines.isEmpty()) lines.add(Component.empty());
        lines.set(lines.size()-1, lines.getLast().append(component));
        return this;
    }

    public @NotNull MultiLineDescription addLineToBeginning(@NotNull Component component){
        lines.addFirst(component);
        return this;
    }
    public @NotNull MultiLineDescription addLine(@NotNull Component component){
        lines.add(component);
        return this;
    }

    public @NotNull Component join(){
        TextComponent join = Component.empty();
        for (Component component : lines) join = join.append(component);
        return join;
    }
    
    public @NotNull List<Component> getLines() {return lines;}
}
