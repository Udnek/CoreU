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

import java.util.Optional;

@org.jspecify.annotations.NullMarked public class LootContextBuilder{

    protected LootParams.Builder paramsBuilder;
    protected ContextKeySet keySet;

    public LootContextBuilder(LootTable lootTableFor, World world){
        keySet = ((CraftLootTable) lootTableFor).getHandle().getParamSet();
        paramsBuilder = new LootParams.Builder(NmsUtils.toNmsWorld(world));
    }

    public LootContextBuilder thisEntity(Entity entity){
        return tryPut(LootContextParams.THIS_ENTITY, NmsUtils.toNmsEntity(entity));
    }
    public LootContextBuilder lastDamagePlayer(Player player){
        return tryPut(LootContextParams.THIS_ENTITY, NmsUtils.toNmsPlayer(player));
    }
    public LootContextBuilder damageSource(DamageSource damageSource){
        return tryPut(LootContextParams.DAMAGE_SOURCE, ((CraftDamageSource) damageSource).getHandle());
    }
    public LootContextBuilder attackingEntity(Entity entity){
        return tryPut(LootContextParams.ATTACKING_ENTITY, NmsUtils.toNmsEntity(entity));
    }
    public LootContextBuilder directAttackingEntity(Entity entity){
        return tryPut(LootContextParams.DIRECT_ATTACKING_ENTITY, NmsUtils.toNmsEntity(entity));
    }
    public LootContextBuilder origin(Location location){
        return tryPut(LootContextParams.ORIGIN, new Vec3(location.getX(), location.getY(), location.getZ()));
    }
    public LootContextBuilder blockState(BlockState blockState){
        return tryPut(LootContextParams.BLOCK_STATE, NmsUtils.toNmsBlockState(blockState));
    }
    // TODO BLOCK ENTITY
    public LootContextBuilder tool(ItemStack itemStack){
        return tryPut(LootContextParams.TOOL, NmsUtils.toNmsItemStack(itemStack));
    }
    public LootContextBuilder explosionRadius(float radius){
        return tryPut(LootContextParams.EXPLOSION_RADIUS, radius);
    }
    public LootContextBuilder enchantmentLevel(int level){
        return tryPut(LootContextParams.ENCHANTMENT_LEVEL, level);
    }
    public LootContextBuilder enchantmentActive(boolean active){
        return tryPut(LootContextParams.ENCHANTMENT_ACTIVE, active);
    }

    protected <T> LootContextBuilder tryPut(ContextKey<T> key, T value){
        paramsBuilder.withParameter(key, value);
        return this;
    }

    public LootContext getNmsContext(){
        return new LootContext.Builder(paramsBuilder.create(keySet)).create(Optional.empty());
    }
    public LootParams getNmsParams(){
        return paramsBuilder.create(keySet);
    }
}
