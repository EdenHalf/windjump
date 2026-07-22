package com.example.windjump;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@Mod(WindJumpMod.MODID)
public class WindJumpMod {
    public static final String MODID = "windjump";

    // KeyMapping definition using standard vanilla constructor for maximum compatibility
    public static final KeyMapping WIND_JUMP_KEY = new KeyMapping(
            "key.windjump.jump",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.movement"
    );

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientEvents {

        // Register key mapping (NeoForge automatically routes IModBusEvents here)
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(WIND_JUMP_KEY);
        }

        // Tick loop (NeoForge automatically routes game tick events here)
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player != null && WIND_JUMP_KEY.consumeClick()) {
                // Apply upward jump velocity
                Vec3 currentVel = player.getDeltaMovement();
                player.setDeltaMovement(currentVel.x, 1.25D, currentVel.z);

                // Reset fall distance on client side
                player.resetFallDistance();

                // Spawn wind particles and play sound locally
                if (mc.level != null) {
                    mc.level.addParticle(
                            ParticleTypes.GUST,
                            player.getX(), player.getY(), player.getZ(),
                            0.0D, 0.0D, 0.0D
                    );

                    mc.level.playLocalSound(
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.WIND_CHARGE_BURST.value(),
                            SoundSource.PLAYERS,
                            1.0F, 1.0F, false
                    );
                }
            }
        }
    }
}
