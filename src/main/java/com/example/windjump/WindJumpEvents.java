package com.example.windjump; // Make sure this matches your package!

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// This tells NeoForge to listen to the events in this class
@EventBusSubscriber(modid = "windjump")
public class WindJumpEvents {
    
    // This is our "safe list" to track players who just used the super jump
    public static final Set<UUID> SAFE_FALLERS = new HashSet<>();

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        // 1. Check if the entity taking fall damage is a Player
        if (event.getEntity() instanceof Player player) {
            
            // 2. Check if they are on our safe list
            if (SAFE_FALLERS.contains(player.getUUID())) {
                
                // 3. Cancel the fall damage entirely!
                event.setCanceled(true);
                
                // 4. Remove them from the list so normal falls still hurt them later
                SAFE_FALLERS.remove(player.getUUID());
            }
        }
    }
}
