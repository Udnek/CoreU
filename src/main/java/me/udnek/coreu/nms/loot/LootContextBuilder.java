package me.udnek.coreu.nms.loot;

import me.udnek.coreu.nms.NmsUtils;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LootContextBuilder {

    protected LootParams.Builder paramsBuilder;
    protected ContextKeySet keySet;

    public LootContextBuilder(@NotNull LootTable lootTableFor, @NotNull World world){
        keySet = ((CraftLootTable) lootTableFor).getHandle().getParamSet();
        paramsBuilder = new LootParams.Builder(NmsUtils.toNmsWorld(world));
    }

    public @NotNull LootContextBuilder thisEntity(@NotNull Entity entity){
        return tryPut(LootContextParams.THIS_ENTITY, NmsUtils.toNmsEntity(entity));
    }
    public @NotNull LootContextBuilder lastDamagePlayer(@NotNull Player player){
        return tryPut(LootContextParams.THIS_ENTITY, NmsUtils.toNmsPlayer(player));
    }
    public @NotNull LootContextBuilder damageSource(@NotNull DamageSource damageSource){
        return tryPut(LootContextParams.DAMAGE_SOURCE, ((CraftDamageSource) damageSource).getHandle());
    }
    public @NotNull LootContextBuilder attackingEntity(@NotNull Entity entity){
        return tryPut(LootContextParams.ATTACKING_ENTITY, NmsUtils.toNmsEntity(entity));
    }
    public @NotNull LootContextBuilder directAttackingEntity(@NotNull Entity entity){
        return tryPut(LootContextParams.DIRECT_ATTACKING_ENTITY, NmsUtils.toNmsEntity(entity));
    }
    public @NotNull LootContextBuilder origin(@NotNull Location location){
        return tryPut(LootContextParams.ORIGIN, new Vec3(location.getX(), location.getY(), location.getZ()));
    }
    public @NotNull LootContextBuilder blockState(@NotNull BlockState blockState){
        return tryPut(LootContextParams.BLOCK_STATE, NmsUtils.toNmsBlockState(blockState));
    }
    // TODO BLOCK ENTITY
    public @NotNull LootContextBuilder tool(@NotNull ItemStack itemStack){
        return tryPut(LootContextParams.TOOL, NmsUtils.toNmsItemStack(itemStack));
    }
    public @NotNull LootContextBuilder explosionRadius(float radius){
        return tryPut(LootContextParams.EXPLOSION_RADIUS, radius);
    }
    public @NotNull LootContextBuilder enchantmentLevel(int level){
        return tryPut(LootContextParams.ENCHANTMENT_LEVEL, level);
    }
    public @NotNull LootContextBuilder enchantmentActive(boolean active){
        return tryPut(LootContextParams.ENCHANTMENT_ACTIVE, active);
    }

    protected <T> @NotNull LootContextBuilder tryPut(@NotNull ContextKey<@NotNull T> key, T value){
        paramsBuilder.withParameter(key, value);
        return this;
    }

    public @NotNull LootContext getNmsContext(){
        return new LootContext.Builder(paramsBuilder.create(keySet)).create(Optional.empty());
    }
    public @NotNull LootParams getNmsParams(){
        return paramsBuilder.create(keySet);
    }
}
