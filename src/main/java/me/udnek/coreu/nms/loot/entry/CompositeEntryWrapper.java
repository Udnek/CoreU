package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.loot.condition.LootConditionWrapper;
import me.udnek.coreu.nms.loot.util.NmsFields;
import me.udnek.coreu.util.Reflex;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class CompositeEntryWrapper implements EntryWrapper{

    protected final CompositeEntryBase entry;

    public CompositeEntryWrapper(CompositeEntryBase entry) {
        this.entry = entry;
    }

    public CompositeEntryWrapper copy(){
        Constructor<AlternativesEntry> constructor = Reflex.getFirstConstructor(AlternativesEntry.class);
        AlternativesEntry newEntry = Reflex.construct(constructor, getChildrenNms(), Reflex.getFieldValue(entry, NmsFields.CONDITIONS));
        return new CompositeEntryWrapper(newEntry);
    }

    public void addChild(EntryWrapper container){
        List<LootPoolEntryContainer> newChildren = new ArrayList<>(getChildrenNms());
        newChildren.add(container.getNms());
        setChildrenNms(newChildren);
    }

    public void addChild(int n, EntryWrapper container){
        List<LootPoolEntryContainer> newChildren = new ArrayList<>(getChildrenNms());
        newChildren.add(n, container.getNms());
        setChildrenNms(newChildren);
    }

    public EntryWrapper getChild(int n){
        return EntryWrapper.fromNms(getChildrenNms().get(n));
    }

    public void removeChild(int n){
        List<LootPoolEntryContainer> children = new ArrayList<>(getChildrenNms());
        children.remove(n);
        setChildrenNms(children);
    }

    @Override
    public void extractAllSingleton(Consumer<SingletonEntryWrapper> consumer) {
        getChildrenNms().forEach(container -> EntryWrapper.fromNms(container).extractAllSingleton(consumer));
    }

    protected void setChildrenNms(List<LootPoolEntryContainer> newChildren){
        Method composeMethod = Reflex.getMethod(
                CompositeEntryBase.class,
                "compose"
        );
        Object composedChildren = Reflex.invokeMethod(
                entry,
                composeMethod,
                newChildren
        );

        Reflex.setFieldValue(entry, NmsFields.CHILDREN, newChildren);
        Reflex.setFieldValue(entry, NmsFields.COMPOSED_CHILDREN, composedChildren);
    }

    protected List<LootPoolEntryContainer> getChildrenNms(){
        return Reflex.getFieldValue(entry, NmsFields.CHILDREN);
    }

    @Override
    public List<LootConditionWrapper> getConditions() {
        List<LootItemCondition> conditions = new ArrayList<>();
        getChildrenNms().forEach(nmsEntry -> {
            List<LootItemCondition> subConditions = LootConditionWrapper.unwrap(EntryWrapper.fromNms(nmsEntry).getConditions());
            conditions.addAll(subConditions);
        });
        return LootConditionWrapper.wrap(conditions);
    }

    @Override
    public void setConditions(List<LootConditionWrapper> conditions) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LootPoolEntryContainer getNms() {
        return entry;
    }
}
