package me.udnek.coreu.util;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.nms.NmsUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@org.jspecify.annotations.NullMarked public class FakeGlow{
    private static final HashMap<Entity, FakeGlow> fakes = new HashMap<>();

    private static final EntityDataAccessor<Byte> DATA_KEY = Reflex.getFieldValue(Entity.class, "DATA_SHARED_FLAGS_ID");

    private @Nullable BukkitRunnable task;
    private final List<Player> observers;
    private final Entity nmsEntity;
    private final long duration;

    public static void glow(org.bukkit.entity.Entity entity, List<Player> observers, long duration) {
        new FakeGlow(entity, observers, duration).run();
    }

    public static void stop(org.bukkit.entity.Entity entity){
        FakeGlow fakeGlow = fakes.get(NmsUtils.toNmsEntity(entity));
        if (fakeGlow == null) return;
        fakeGlow.stop(true);
    }

    private FakeGlow(org.bukkit.entity.Entity entity, List<Player> observers, long duration) {
        this.observers = observers;
        this.nmsEntity = NmsUtils.toNmsEntity(entity);
        this.duration = duration;
    }

    private void run(){

        task = new BukkitRunnable() {
            int step = 0;
            @Override
            public void run() {
                if (step == duration){
                    stop(true);
                    return;
                }
                sendPacketToObservers(((byte) (getMetadata() | 0x40)));
                step++;
            }
        };
        task.runTaskTimer(CoreU.getInstance(), 0, 1);

        FakeGlow oldFake = fakes.get(nmsEntity);
        if (oldFake != null) oldFake.stop(false);
        fakes.put(nmsEntity, this);
    }

    private void stop(boolean sendReal){
        if (task != null) task.cancel();
        fakes.remove(nmsEntity);
        if (!sendReal) return;
        sendPacketToObservers((byte) (getMetadata() & ~0x40));
    }

    private byte getMetadata(){
        return nmsEntity.getEntityData().get(DATA_KEY);
    }

    private void sendPacketToObservers(byte data){
        for (Player observer : observers){
            NmsUtils.sendPacket(observer, new ClientboundSetEntityDataPacket(nmsEntity.getId(),
                    Collections.singletonList(new SynchedEntityData.DataValue<>(0, EntityDataSerializers.BYTE, data)
            )));
        }
    }
}
