package me.udnek.coreu.util;

import com.google.common.base.Preconditions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
public final class Either<Left, Right>{

    @Nullable
    private final Left left;
    @Nullable
    private final Right right;

    public static <L, R> Either<L, R> ofLeft(L left){
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> ofRight(R right){
        return new Either<>(null, right);
    }

    private Either(@Nullable Left left, @Nullable Right right){
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
}
