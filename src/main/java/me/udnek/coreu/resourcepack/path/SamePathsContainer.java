package me.udnek.coreu.resourcepack.path;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@org.jspecify.annotations.NullMarked public class SamePathsContainer{
    private final List<RpPath> paths = new ArrayList<>();
    public SamePathsContainer(RpPath rpPathA, RpPath rpPathB){
        Preconditions.checkArgument(rpPathA.isSame(rpPathB), "Paths are not same!");
        paths.add(rpPathA);
        paths.add(rpPathB);
    }

    public boolean canAdd(RpPath rpPath){
        return paths.getFirst().isSame(rpPath);
    }

    public void add(RpPath rpPath){
        Preconditions.checkArgument(canAdd(rpPath), "Can not add " + rpPath + " to container with sames: " + this);
        paths.add(rpPath);
    }

    public List<RpPath> getMostPrioritized(){
        Preconditions.checkArgument(!paths.isEmpty(), "paths is empty!");
        ArrayList<RpPath> list = new ArrayList<>(paths);
        list.sort(Comparator.comparingInt(RpPath::getPriorityValue));
        int priority = list.getLast().getPriorityValue();
        return list.stream().filter((Predicate<RpPath>) input -> input.getPriorityValue() == priority).toList();
    }

    public List<RpPath> getAll(){
        return new ArrayList<>(paths);
    }

    public RpPath getExample(){
        return paths.getFirst();
    }

    @Override
    public String toString() {
        return "SamePathsContainer{" +
                "paths=" + paths +
                '}';
    }
}
