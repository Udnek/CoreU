package me.udnek.coreu.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Consumer;

@org.jspecify.annotations.NullMarked public class Either<Left, Right>{

    protected Left left;
    protected Right right;

    public Either(@UnknownNullability @NotNull Left left, @UnknownNullability @NotNull Right right){
        Preconditions.checkArgument(!(left == null && right == null), "Either can no be both null");
        Preconditions.checkArgument(left == null || right == null, "Either can no be both not null");
        this.left = left;
        this.right = right;
    }

    public boolean isRight(){return right != null;}
    public boolean isLeft(){return left != null;}

    public void consumeEither(@Nullable Consumer<Right> rightConsumer, @Nullable Consumer<Left> leftConsumer){
        if (isRight() && rightConsumer != null) rightConsumer.accept(right);
        if (isLeft() && leftConsumer != null) leftConsumer.accept(left);
    }

    public void consumeIfRight(Consumer<Right> consumer){
        if (isRight()) consumer.accept(right);
    }

    public void consumeIfLeft(Consumer<Left> consumer){
        if (isLeft()) consumer.accept(left);
    }

    public Right getRight() {
        return right;
    }
    public Left getLeft() {
        return left;
    }
}
