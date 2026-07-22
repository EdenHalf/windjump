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
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@Mod(WindJumpMod.MODID)
public class WindJumpMod {
    public static final String MODID = "windjump";

    // Reboundable Key Mapping registered to the standard Movement category
    public static final KeyMapping WIND_JUMP_KEY = new KeyMapping(
            "key.windjump.jump",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KeyMapping.CATEGORY_MOVEMENT
    );

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(WIND_JUMP_KEY);
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player != null && WIND_JUMP_KEY.consumeClick()) {
                // Thrust the player upwards
                Vec3 currentVel = player.getDeltaMovement();
                player.setDeltaMovement(currentVel.x, 1.25D, currentVel.z);

                // Reset fall distance counter to protect from landing/kinetic damage
                player.resetFallDistance();

                if (mc.level != null) {
                    // Spawn particle effect
                    mc.level.addParticle(
                            ParticleTypes.GUST,
                            player.getX(), player.getY(), player.getZ(),
                            0.0D, 0.0D, 0.0D
                    );

                    // Play wind charge sound using holder unwrapping .value()
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
