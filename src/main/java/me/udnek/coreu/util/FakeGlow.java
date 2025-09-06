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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FakeGlow {
    private static final HashMap<Entity, FakeGlow> fakes = new HashMap<>();

    private @Nullable BukkitRunnable task;
    private final @NotNull List<Player> observers;
    private final @NotNull net.minecraft.world.entity.Entity nmsEntity;

    public FakeGlow(@NotNull List<Player> observers, @NotNull org.bukkit.entity.Entity entity) {
        this.observers = observers;
        this.nmsEntity = NmsUtils.toNmsEntity(entity);
    }

    public void glow(int duration) {
        EntityDataAccessor<Byte> key = Reflex.getFieldValue(Entity.class, "DATA_SHARED_FLAGS_ID");
        task = new BukkitRunnable() {
            int step = 0;
            @Override
            public void run() {
                byte metadata = nmsEntity.getEntityData().get(key);
                if (step == duration){
                    sendPacketToObservers((byte) (metadata & ~0x40));
                    cancel();
                    return;
                }
                sendPacketToObservers(((byte) (metadata | 0x40)));
                step++;
            }
        };
        task.runTaskTimer(CoreU.getInstance(), 0, 1);

        FakeGlow oldFake = fakes.get(nmsEntity);
        if (oldFake != null && oldFake.task != null) {
            oldFake.task.cancel();
        }
        fakes.put(nmsEntity, this);
    }

    private void sendPacketToObservers(byte data){
        for (Player observer : observers){
            NmsUtils.sendPacket(observer, new ClientboundSetEntityDataPacket(nmsEntity.getId(),
                    Collections.singletonList(new SynchedEntityData.DataValue<>(0, EntityDataSerializers.BYTE, data)
            )));
        }
    }
}
