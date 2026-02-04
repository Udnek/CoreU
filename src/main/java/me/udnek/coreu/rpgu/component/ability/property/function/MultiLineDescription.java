package me.udnek.coreu.rpgu.component.ability.property.function;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public class MultiLineDescription{

    public static MultiLineDescription of(Component component){
        return new MultiLineDescription().add(component);
    }

    public static MultiLineDescription of(){
        return new MultiLineDescription();
    }

    protected List<Component> lines = new ArrayList<>();

    protected MultiLineDescription(){}

    public MultiLineDescription add(MultiLineDescription other){
        if (other.lines.isEmpty()) return this;
        add(other.lines.getFirst());
        for (Component component : other.lines.subList(1, other.lines.size())) {
            addLine(component);
        }
        return this;
    }

    public MultiLineDescription addToBeginning(Component component){
        if (lines.isEmpty()) lines.add(Component.empty());
        lines.set(0, component.append(lines.getFirst()));
        return this;
    }
    public MultiLineDescription add(Component component){
        if (lines.isEmpty()) lines.add(Component.empty());
        lines.set(lines.size()-1, lines.getLast().append(component));
        return this;
    }

    public MultiLineDescription addLineToBeginning(Component component){
        lines.addFirst(component);
        return this;
    }
    public MultiLineDescription addLine(Component component){
        lines.add(component);
        return this;
    }

    public Component join(){
        TextComponent join = Component.empty();
        for (Component component : lines) join = join.append(component);
        return join;
    }

    public List<Component> getLines() {return lines;}
}
