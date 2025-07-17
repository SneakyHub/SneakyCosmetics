package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages gadget functionality and interactions
 */
public class GadgetManager implements Listener {
    private final SneakyCosmetics plugin;
    private final Map<Player, Long> cooldowns = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    // Cooldown times in milliseconds
    private static final long GRAPPLING_HOOK_COOLDOWN = 3000; // 3 seconds
    private static final long FIREWORK_LAUNCHER_COOLDOWN = 2000; // 2 seconds
    private static final long PAINT_BRUSH_COOLDOWN = 1000; // 1 second
    private static final long TELEPORT_STICK_COOLDOWN = 5000; // 5 seconds
    private static final long PARTY_POPPER_COOLDOWN = 2000; // 2 seconds
    private static final long SNOWBALL_CANNON_COOLDOWN = 500; // 0.5 seconds
    private static final long WIND_CANNON_COOLDOWN = 3000; // 3 seconds
    private static final long GRAVITY_GUN_COOLDOWN = 4000; // 4 seconds
    private static final long LIGHTNING_WAND_COOLDOWN = 6000; // 6 seconds
    private static final long FREEZE_RAY_COOLDOWN = 2500; // 2.5 seconds
    private static final long JUMP_BOOTS_COOLDOWN = 1500; // 1.5 seconds
    private static final long PORTAL_GUN_COOLDOWN = 8000; // 8 seconds
    
    public GadgetManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        // Register as event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        // Check if this is a gadget and handle accordingly
        if (displayName.contains("Grappling Hook")) {
            handleGrapplingHook(player, event);
        } else if (displayName.contains("Firework Launcher")) {
            handleFireworkLauncher(player, event);
        } else if (displayName.contains("Paint Brush")) {
            handlePaintBrush(player, event);
        } else if (displayName.contains("Teleport Stick")) {
            handleTeleportStick(player, event);
        } else if (displayName.contains("Party Popper")) {
            handlePartyPopper(player, event);
        } else if (displayName.contains("Snowball Cannon")) {
            handleSnowballCannon(player, event);
        } else if (displayName.contains("Wind Cannon")) {
            handleWindCannon(player, event);
        } else if (displayName.contains("Gravity Gun")) {
            handleGravityGun(player, event);
        } else if (displayName.contains("Lightning Wand")) {
            handleLightningWand(player, event);
        } else if (displayName.contains("Freeze Ray")) {
            handleFreezeRay(player, event);
        } else if (displayName.contains("Jump Boots")) {
            handleJumpBoots(player, event);
        } else if (displayName.contains("Portal Gun")) {
            handlePortalGun(player, event);
        }
    }
    
    private void handleGrapplingHook(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, GRAPPLING_HOOK_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Launch a fishing hook-like projectile
        FishHook hook = player.launchProjectile(FishHook.class);
        hook.setMetadata("grappling_hook", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
        
        player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 1.0f);
        player.sendMessage("§2✓ Grappling hook launched! Right-click again to retract.");
        
        // Auto-retract after 10 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!hook.isDead()) {
                    hook.remove();
                }
            }
        }.runTaskLater(plugin, 200L);
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof FishHook)) {
            return;
        }
        
        FishHook hook = (FishHook) event.getEntity();
        if (!hook.hasMetadata("grappling_hook")) {
            return;
        }
        
        String playerUUID = hook.getMetadata("grappling_hook").get(0).asString();
        Player player = plugin.getServer().getPlayer(java.util.UUID.fromString(playerUUID));
        
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // Pull player towards hook location
        Location hookLoc = hook.getLocation();
        Location playerLoc = player.getLocation();
        
        Vector direction = hookLoc.toVector().subtract(playerLoc.toVector()).normalize();
        direction.multiply(2.0); // Adjust pull strength
        direction.setY(Math.max(direction.getY(), 0.5)); // Ensure some upward movement
        
        player.setVelocity(direction);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
        player.sendMessage("§2✓ Grappling successful!");
        
        hook.remove();
    }
    
    private void handleFireworkLauncher(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, FIREWORK_LAUNCHER_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Create and launch firework
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
        Firework firework = (Firework) player.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        
        // Random colors and effects
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.WHITE};
        builder.withColor(colors[random.nextInt(colors.length)]);
        builder.withFade(colors[random.nextInt(colors.length)]);
        
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        builder.with(types[random.nextInt(types.length)]);
        
        if (random.nextBoolean()) builder.withFlicker();
        if (random.nextBoolean()) builder.withTrail();
        
        meta.addEffect(builder.build());
        meta.setPower(random.nextInt(3) + 1);
        firework.setFireworkMeta(meta);
        
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        player.sendMessage("§c✓ Firework launched! §e" + (3 - (System.currentTimeMillis() - cooldowns.getOrDefault(player, 0L)) / 1000) + "s cooldown");
    }
    
    private void handlePaintBrush(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, PAINT_BRUSH_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection();
        
        // Create colorful particle effects
        Particle[] particles = {Particle.DUST, Particle.HAPPY_VILLAGER, Particle.VILLAGER_ANGRY, 
                               Particle.HEART, Particle.NOTE, Particle.TOTEM_OF_UNDYING};
        
        for (int i = 0; i < 10; i++) {
            Location particleLoc = loc.clone().add(direction.clone().multiply(i * 0.5));
            Particle particle = particles[random.nextInt(particles.length)];
            
            if (particle == Particle.DUST) {
                // Random colored dust
                Particle.DustOptions dust = new Particle.DustOptions(
                    Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)), 
                    1.0f
                );
                player.getWorld().spawnParticle(particle, particleLoc, 5, 0.2, 0.2, 0.2, 0, dust);
            } else {
                player.getWorld().spawnParticle(particle, particleLoc, 3, 0.2, 0.2, 0.2, 0);
            }
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1.0f, 1.5f);
        player.sendMessage("§9✓ Paint splashed!");
    }
    
    private void handleTeleportStick(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, TELEPORT_STICK_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Teleport player to where they're looking (max 20 blocks)
        Location targetLoc = player.getTargetBlock(null, 20).getLocation().add(0, 1, 0);
        
        // Safety check - make sure it's a safe location
        if (targetLoc.getBlock().getType().isSolid() || targetLoc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
            player.sendMessage("§c✗ Cannot teleport to solid blocks!");
            return;
        }
        
        // Teleport effects
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        player.teleport(targetLoc);
        
        player.getWorld().spawnParticle(Particle.PORTAL, targetLoc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        player.playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
        player.sendMessage("§5✓ Teleported successfully!");
    }
    
    private void handlePartyPopper(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, PARTY_POPPER_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        Location loc = player.getEyeLocation();
        
        // Create celebration effects
        player.getWorld().spawnParticle(Particle.FIREWORK, loc, 50, 2.0, 2.0, 2.0, 0.1);
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 30, 3.0, 3.0, 3.0, 0);
        player.getWorld().spawnParticle(Particle.NOTE, loc, 20, 2.0, 2.0, 2.0, 0);
        
        // Play celebration sounds
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
        
        // Send message to nearby players
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby.getLocation().distance(player.getLocation()) <= 20) {
                nearby.sendMessage("§6🎉 " + player.getName() + " is celebrating! 🎉");
            }
        }
        
        player.sendMessage("§6✓ Party time! 🎉");
    }
    
    private void handleSnowballCannon(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, SNOWBALL_CANNON_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Launch snowball with extra velocity
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(snowball.getVelocity().multiply(2.0));
        snowball.setMetadata("super_snowball", new FixedMetadataValue(plugin, true));
        
        player.playSound(player.getLocation(), Sound.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 1.0f);
        player.sendMessage("§b✓ Super snowball fired!");
    }
    
    private void handleWindCannon(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, WIND_CANNON_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection();
        
        // Create wind particle effects
        for (int i = 1; i <= 15; i++) {
            Location particleLoc = loc.clone().add(direction.clone().multiply(i * 0.8));
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, particleLoc, 3, 0.3, 0.3, 0.3, 0.1);
            player.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 5, 0.4, 0.4, 0.4, 0.05);
        }
        
        // Push nearby entities away
        for (Entity entity : player.getNearbyEntities(10, 5, 10)) {
            if (entity instanceof Player || entity instanceof org.bukkit.entity.LivingEntity) {
                Vector pushDirection = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                pushDirection.multiply(1.5);
                pushDirection.setY(0.3);
                entity.setVelocity(pushDirection);
            }
        }
        
        player.playSound(player.getLocation(), Sound.ITEM_ELYTRA_FLYING, 1.0f, 0.8f);
        player.sendMessage("§a✓ Wind blast unleashed!");
    }
    
    private void handleGravityGun(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, GRAVITY_GUN_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Launch nearby entities into the air
        for (Entity entity : player.getNearbyEntities(8, 4, 8)) {
            if (entity instanceof org.bukkit.entity.LivingEntity && !(entity instanceof Player)) {
                Vector upward = new Vector(0, 2.0, 0);
                entity.setVelocity(upward);
                
                // Add gravity particle effects around the entity
                Location entityLoc = entity.getLocation();
                player.getWorld().spawnParticle(Particle.PORTAL, entityLoc, 20, 1.0, 1.0, 1.0, 0.5);
            }
        }
        
        // Create gravity field visual effect
        Location loc = player.getEyeLocation();
        for (int i = 0; i < 360; i += 20) {
            double angle = Math.toRadians(i);
            double x = Math.cos(angle) * 3;
            double z = Math.sin(angle) * 3;
            Location particleLoc = loc.clone().add(x, 0, z);
            player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
        }
        
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.5f);
        player.sendMessage("§d✓ Gravity manipulation activated!");
    }
    
    private void handleLightningWand(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, LIGHTNING_WAND_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Get target location
        Location targetLoc = player.getTargetBlock(null, 30).getLocation();
        
        // Create fake lightning effect (visual only)
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, targetLoc.add(0, 1, 0), 50, 1.0, 3.0, 1.0, 0.3);
        player.getWorld().spawnParticle(Particle.FLASH, targetLoc, 1, 0, 0, 0, 0);
        
        // Create lightning path from player to target
        Location start = player.getEyeLocation();
        Vector direction = targetLoc.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(targetLoc);
        
        for (double i = 0; i < distance; i += 0.5) {
            Location pathLoc = start.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, pathLoc, 2, 0.1, 0.1, 0.1, 0.1);
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.5f);
        player.playSound(targetLoc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
        player.sendMessage("§e✓ Lightning strike called down!");
    }
    
    private void handleFreezeRay(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, FREEZE_RAY_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection();
        
        // Create freeze ray beam effect
        for (int i = 1; i <= 20; i++) {
            Location beamLoc = loc.clone().add(direction.clone().multiply(i * 0.5));
            player.getWorld().spawnParticle(Particle.SNOWFLAKE, beamLoc, 5, 0.2, 0.2, 0.2, 0.02);
            player.getWorld().spawnParticle(Particle.CLOUD, beamLoc, 2, 0.1, 0.1, 0.1, 0.01);
        }
        
        // Freeze nearby entities (slow them down)
        for (Entity entity : player.getNearbyEntities(15, 5, 15)) {
            if (entity instanceof org.bukkit.entity.LivingEntity && !(entity instanceof Player)) {
                org.bukkit.entity.LivingEntity living = (org.bukkit.entity.LivingEntity) entity;
                living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SLOWNESS, 100, 2
                ));
                
                // Ice particle effects around frozen entities
                Location entityLoc = entity.getLocation();
                player.getWorld().spawnParticle(Particle.SNOWFLAKE, entityLoc.add(0, 1, 0), 15, 1.0, 1.0, 1.0, 0.1);
            }
        }
        
        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);
        player.sendMessage("§b✓ Freeze ray activated!");
    }
    
    private void handleJumpBoots(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, JUMP_BOOTS_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Super jump effect
        Vector jumpVelocity = new Vector(0, 1.5, 0);
        player.setVelocity(jumpVelocity);
        
        // Landing particle effects
        Location playerLoc = player.getLocation();
        player.getWorld().spawnParticle(Particle.CLOUD, playerLoc, 20, 1.0, 0.1, 1.0, 0.1);
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, playerLoc, 10, 0.5, 0.5, 0.5, 0);
        
        // Sound effects
        player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 1.0f, 0.8f);
        player.sendMessage("§2✓ Super jump activated!");
        
        // Schedule landing effect
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnGround()) {
                    Location landLoc = player.getLocation();
                    player.getWorld().spawnParticle(Particle.EXPLOSION, landLoc, 5, 1.0, 0.1, 1.0, 0);
                    player.playSound(landLoc, Sound.ENTITY_GENERIC_EXPLODE, 0.3f, 1.5f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 10L, 2L);
    }
    
    private void handlePortalGun(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, PORTAL_GUN_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Get target location for portal
        Location targetLoc = player.getTargetBlock(null, 50).getLocation().add(0, 1, 0);
        
        // Safety check
        if (targetLoc.getBlock().getType().isSolid()) {
            player.sendMessage("§c✗ Cannot create portal in solid blocks!");
            return;
        }
        
        // Create portal visual effects at target
        player.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 100, 2.0, 2.0, 2.0, 1.0);
        player.getWorld().spawnParticle(Particle.END_ROD, targetLoc, 20, 1.0, 1.0, 1.0, 0.1);
        
        // Portal sound at target
        player.playSound(targetLoc, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.0f);
        
        // Teleport player after brief delay
        new BukkitRunnable() {
            @Override
            public void run() {
                // Create departure effects
                Location departLoc = player.getLocation();
                player.getWorld().spawnParticle(Particle.PORTAL, departLoc.add(0, 1, 0), 50, 1.0, 1.0, 1.0, 0.5);
                
                // Teleport
                player.teleport(targetLoc);
                
                // Arrival effects
                player.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 50, 1.0, 1.0, 1.0, 0.5);
                player.getWorld().spawnParticle(Particle.FLASH, targetLoc, 1, 0, 0, 0, 0);
                player.playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
                
                player.sendMessage("§5✓ Portal travel successful!");
            }
        }.runTaskLater(plugin, 20L); // 1 second delay
        
        player.sendMessage("§5✓ Portal created! Teleporting...");
    }
    
    private boolean checkCooldown(Player player, long cooldownTime) {
        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(player, 0L);
        
        if (currentTime - lastUsed < cooldownTime) {
            long remaining = (cooldownTime - (currentTime - lastUsed)) / 1000;
            player.sendMessage("§c✗ Gadget on cooldown! " + remaining + "s remaining.");
            return false;
        }
        
        cooldowns.put(player, currentTime);
        return true;
    }
    
    public void stopAllTasks() {
        // Clear cooldowns when plugin disables
        cooldowns.clear();
    }
    
    public void stopPlayerEffects(Player player) {
        // Remove player from cooldowns
        cooldowns.remove(player);
    }
    
    // Legacy methods for compatibility
    public void startParticleTask() {}
    public void startTrailTask() {}
    public void startPetTask() {}
    public void startWingTask() {}
    public void startAuraTask() {}
    public void stopPlayerTrails(Player player) {}
    public void removePet(Player player) {}
    public void stopPlayerWings(Player player) {}
    public void stopPlayerAuras(Player player) {}
}