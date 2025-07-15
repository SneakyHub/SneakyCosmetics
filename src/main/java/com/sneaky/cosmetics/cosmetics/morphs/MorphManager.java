package com.sneaky.cosmetics.cosmetics.morphs;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages morph cosmetics and their special abilities
 * Handles morph-specific events and interactions
 */
public class MorphManager implements Listener {
    
    private final SneakyCosmetics plugin;
    private final Map<UUID, MorphCosmetic> activeMorphs;
    private final Map<UUID, Long> lastAbilityUse;
    
    // Cooldowns for special abilities (in milliseconds)
    private static final long TELEPORT_COOLDOWN = 5000; // 5 seconds
    private static final long SPECIAL_ABILITY_COOLDOWN = 3000; // 3 seconds
    
    public MorphManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.activeMorphs = new HashMap<>();
        this.lastAbilityUse = new HashMap<>();
        
        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Set a player's active morph
     */
    public void setActiveMorph(Player player, MorphCosmetic morph) {
        if (morph == null) {
            removeMorph(player);
            return;
        }
        
        activeMorphs.put(player.getUniqueId(), morph);
        
        // Apply the morph
        morph.activate(player);
        
        plugin.getLogger().info("Player " + player.getName() + " morphed into " + morph.getDisplayName());
    }
    
    /**
     * Remove a player's active morph
     */
    public void removeMorph(Player player) {
        MorphCosmetic currentMorph = activeMorphs.remove(player.getUniqueId());
        if (currentMorph != null) {
            currentMorph.deactivate(player);
            plugin.getLogger().info("Player " + player.getName() + " returned to human form");
        }
    }
    
    /**
     * Get a player's active morph
     */
    public MorphCosmetic getActiveMorph(Player player) {
        return activeMorphs.get(player.getUniqueId());
    }
    
    /**
     * Check if a player has an active morph
     */
    public boolean hasMorph(Player player) {
        return activeMorphs.containsKey(player.getUniqueId());
    }
    
    /**
     * Check if a player can use their special ability (cooldown check)
     */
    private boolean canUseAbility(Player player) {
        UUID uuid = player.getUniqueId();
        Long lastUse = lastAbilityUse.get(uuid);
        
        if (lastUse == null) {
            return true;
        }
        
        return System.currentTimeMillis() - lastUse >= SPECIAL_ABILITY_COOLDOWN;
    }
    
    /**
     * Set the last ability use time for a player
     */
    private void setLastAbilityUse(Player player) {
        lastAbilityUse.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    /**
     * Handle player interactions for morph abilities
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Only handle off-hand to avoid double triggering
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        MorphCosmetic morph = getActiveMorph(player);
        if (morph == null || !morph.hasSpecialAbilities()) {
            return;
        }
        
        // Check if player is sneaking and right-clicking for special abilities
        if (player.isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (!canUseAbility(player)) {
                player.sendMessage("§cAbility is on cooldown!");
                return;
            }
            
            handleSpecialAbility(player, morph);
            event.setCancelled(true);
        }
    }
    
    /**
     * Handle special abilities based on morph type
     */
    private void handleSpecialAbility(Player player, MorphCosmetic morph) {
        switch (morph.getEntityType()) {
            case ENDERMAN:
                handleEndermanTeleport(player);
                break;
            case CREEPER:
                handleCreeperExplosion(player);
                break;
            case WOLF:
                handleWolfHowl(player);
                break;
            case BAT:
                handleBatEcholocation(player);
                break;
            case SQUID:
                handleSquidInk(player);
                break;
            default:
                player.sendMessage("§7This morph doesn't have special abilities.");
                return;
        }
        
        setLastAbilityUse(player);
    }
    
    /**
     * Handle Enderman teleportation ability
     */
    private void handleEndermanTeleport(Player player) {
        org.bukkit.Location targetLocation = player.getTargetBlock(null, 32).getLocation().add(0, 1, 0);
        
        if (targetLocation.getBlock().getType().isSolid()) {
            player.sendMessage("§cCannot teleport to that location!");
            return;
        }
        
        // Teleport with effects
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.spawnParticle(org.bukkit.Particle.PORTAL, player.getLocation().add(0, 1, 0), 20, 0.5, 1, 0.5, 0.1);
        
        player.teleport(targetLocation);
        
        player.playSound(targetLocation, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.spawnParticle(org.bukkit.Particle.PORTAL, targetLocation.add(0, 1, 0), 20, 0.5, 1, 0.5, 0.1);
        
        player.sendMessage("§5✦ Teleported!");
    }
    
    /**
     * Handle Creeper fake explosion ability (visual only)
     */
    private void handleCreeperExplosion(Player player) {
        org.bukkit.Location loc = player.getLocation();
        
        // Create fake explosion effect
        player.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_LARGE, loc, 3, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        
        // Push nearby players away (non-damaging)
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby != player && nearby.getLocation().distance(loc) <= 5) {
                org.bukkit.util.Vector direction = nearby.getLocation().toVector().subtract(loc.toVector()).normalize();
                nearby.setVelocity(direction.multiply(0.5));
                nearby.sendMessage("§c💥 " + player.getName() + " used Creeper Blast!");
            }
        }
        
        player.sendMessage("§a💥 Used Creeper Blast!");
    }
    
    /**
     * Handle Wolf howl ability
     */
    private void handleWolfHowl(Player player) {
        org.bukkit.Location loc = player.getLocation();
        
        // Play howl sound
        player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_WOLF_HOWL, 2.0f, 1.0f);
        
        // Give temporary speed boost to nearby players
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby.getLocation().distance(loc) <= 10) {
                nearby.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SPEED, 200, 1, false, false)); // 10 seconds
                nearby.sendMessage("§6🐺 " + player.getName() + "'s howl gave you speed!");
            }
        }
        
        player.sendMessage("§6🐺 Used Pack Howl!");
    }
    
    /**
     * Handle Bat echolocation ability
     */
    private void handleBatEcholocation(Player player) {
        // Highlight nearby players and mobs
        for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
            if (entity instanceof org.bukkit.entity.LivingEntity) {
                entity.getLocation().getWorld().spawnParticle(
                    org.bukkit.Particle.END_ROD, 
                    entity.getLocation().add(0, entity.getHeight()/2, 0), 
                    5, 0.2, 0.2, 0.2, 0.1
                );
            }
        }
        
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_BAT_AMBIENT, 1.0f, 2.0f);
        player.sendMessage("§7🦇 Used Echolocation!");
    }
    
    /**
     * Handle Squid ink ability
     */
    private void handleSquidInk(Player player) {
        org.bukkit.Location loc = player.getLocation();
        
        // Create ink cloud effect
        player.getWorld().spawnParticle(org.bukkit.Particle.SQUID_INK, loc, 50, 2, 1, 2, 0.1);
        
        // Give temporary blindness to nearby players
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby != player && nearby.getLocation().distance(loc) <= 5) {
                nearby.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.BLINDNESS, 60, 0, false, false)); // 3 seconds
                nearby.sendMessage("§0🦑 " + player.getName() + " used Ink Cloud!");
            }
        }
        
        player.sendMessage("§0🦑 Used Ink Cloud!");
    }
    
    /**
     * Handle flight toggle for flying morphs
     */
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        MorphCosmetic morph = getActiveMorph(player);
        
        // Only allow flight if the morph supports it
        if (morph != null && !morph.canFly()) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
    
    /**
     * Clean up when player quits
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Remove morph data
        activeMorphs.remove(uuid);
        lastAbilityUse.remove(uuid);
    }
    
    /**
     * Get all active morphs (for admin purposes)
     */
    public Map<UUID, MorphCosmetic> getActiveMorphs() {
        return new HashMap<>(activeMorphs);
    }
    
    /**
     * Force remove all morphs (for plugin disable)
     */
    public void removeAllMorphs() {
        for (UUID uuid : activeMorphs.keySet()) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                removeMorph(player);
            }
        }
        activeMorphs.clear();
        lastAbilityUse.clear();
    }
}