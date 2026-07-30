package me.udnek.coreu.custom.registry;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked public abstract class AbstractRegistrable implements Registrable{
    protected @Nullable String id;

    @Override
    @MustBeInvokedByOverriders
    public void initialize(Plugin plugin) {
        Preconditions.checkArgument(id == null, "Registrable already initialized!");
        id = new NamespacedKey(plugin, getRawId()).asString();
    }

    public abstract String getRawId();

    @Override
    public final String getId() {
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
