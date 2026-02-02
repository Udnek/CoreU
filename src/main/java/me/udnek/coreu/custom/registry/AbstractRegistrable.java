package me.udnek.coreu.custom.registry;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRegistrable implements Registrable{
    protected String id;
    @Override
    @MustBeInvokedByOverriders
    public void initialize(@NotNull Plugin plugin) {
        Preconditions.checkArgument(id == null, "Registrable already initialized!");
        id = new NamespacedKey(plugin, getRawId()).asString();
    }

    public abstract @NotNull String getRawId();

    @Override
    public final @NotNull String getId() {
        Preconditions.checkArgument(id != null, "Id is not present (not registered yet probably)");
        return id;
    }

    @Override
    public String toString() {
        return "AbstractRegistrable{" +
                "id='" + id + '\'' +
                " (" + super.toString() + ")" +
                '}';
    }
}
