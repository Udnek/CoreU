package me.udnek.coreu.nms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.effect.ConstructableCustomEffect;
import me.udnek.coreu.custom.effect.CustomEffect;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.util.Reflex;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PacketHandler {

    public static void initialize() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                CoreU.getInstance(), ListenerPriority.NORMAL,
                PacketType.Play.Server.ENTITY_EFFECT, PacketType.Play.Server.REMOVE_ENTITY_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Holder.Reference<MobEffect> holder = (Holder.Reference<MobEffect>) packet.getModifier().read(1);
                Identifier id = holder.key().identifier();
                if (id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) return;
                CustomEffect customEffect = CustomRegistries.EFFECT.get(id.toString());
                if (customEffect instanceof ConstructableCustomEffect disguiseable) {
                    PotionEffectType vanillaDisguise = disguiseable.getVanillaDisguise();
                    if (vanillaDisguise == null) event.setCancelled(true);
                    else packet.getModifier().write(1, NmsUtils.toNms(Registries.MOB_EFFECT, vanillaDisguise));
                }
            }
        });

        // CHUNKS

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                CoreU.getInstance(), ListenerPriority.NORMAL,
                PacketType.Play.Server.BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                ClientboundBlockUpdatePacket handle = (ClientboundBlockUpdatePacket) packet.getHandle();
                CustomBlockType blockType = CustomBlockType.get(NmsUtils.fromNmsBlockPos(event.getPlayer().getWorld(), handle.getPos()).getBlock());
                if (blockType == null) return;
                BlockState fakeState = blockType.getFakeState();
                if (fakeState == null) return;
                Reflex.setFieldValue(handle, "blockState", NmsUtils.toNmsBlockState(fakeState));
            }
        });

//        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
//                CoreU.getInstance(), ListenerPriority.NORMAL,
//                PacketType.Play.Server.BLOCK_ACTION, PacketType.Play.Server.TILE_ENTITY_DATA) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                PacketContainer packet = event.getPacket();
//                BlockPosition position = packet.getBlockPositionModifier().read(0);
//                CustomBlockType blockType = CustomBlockType.get(position.toLocation(event.getPlayer().getWorld()).getBlock());
//                if (blockType != null && blockType.getFakeState() != null) event.setCancelled(true);
//            }
//        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                CoreU.getInstance(), ListenerPriority.NORMAL,
                PacketType.Play.Server.WORLD_PARTICLES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                ClientboundLevelParticlesPacket handle = (ClientboundLevelParticlesPacket) packet.getHandle();
                if (!isFallingParticle(handle)) return;

                CustomBlockType blockType = CustomBlockType.get(NmsUtils.fromNmsBlockPos(
                        event.getPlayer().getWorld(),
                        NmsUtils.toNmsPlayer(event.getPlayer()).getOnPos()).getBlock()
                );
                if (blockType == null) return;
                blockType.onPlayerFall(event.getPlayer(), new Location(event.getPlayer().getWorld(), handle.getX(), handle.getY(), handle.getZ()), handle.getCount());
                event.setCancelled(true);
            }

            public boolean isFallingParticle(@NotNull ClientboundLevelParticlesPacket packet){
                if (packet.getParticle().getType() != ParticleTypes.BLOCK) return false;
                return packet.getXDist() == 0 && packet.getYDist() == 0 && packet.getZDist() == 0 || packet.getMaxSpeed() != 0.15;
            }
        });

    }
}









