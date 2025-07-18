package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages gadget functionality and interactions
 */
public class GadgetManager implements Listener {
    private final SneakyCosmetics plugin;
    private final Map<Player, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<Player, Long> damageImmunity = new ConcurrentHashMap<>();
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
    private static final long ROCKET_LAUNCHER_COOLDOWN = 4000; // 4 seconds
    private static final long FORCE_FIELD_COOLDOWN = 10000; // 10 seconds
    private static final long TIME_FREEZER_COOLDOWN = 15000; // 15 seconds
    private static final long ENERGY_SWORD_COOLDOWN = 2000; // 2 seconds
    private static final long INVISIBILITY_CLOAK_COOLDOWN = 12000; // 12 seconds
    private static final long SPEED_BOOSTER_COOLDOWN = 6000; // 6 seconds
    private static final long HEALING_STAFF_COOLDOWN = 5000; // 5 seconds
    private static final long METEOR_SUMMONER_COOLDOWN = 20000; // 20 seconds
    
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
        } else if (displayName.contains("Rocket Launcher")) {
            handleRocketLauncher(player, event);
        } else if (displayName.contains("Force Field")) {
            handleForceField(player, event);
        } else if (displayName.contains("Time Freezer")) {
            handleTimeFreezer(player, event);
        } else if (displayName.contains("Energy Sword")) {
            handleEnergySword(player, event);
        } else if (displayName.contains("Invisibility Cloak")) {
            handleInvisibilityCloak(player, event);
        } else if (displayName.contains("Speed Booster")) {
            handleSpeedBooster(player, event);
        } else if (displayName.contains("Healing Staff")) {
            handleHealingStaff(player, event);
        } else if (displayName.contains("Meteor Summoner")) {
            handleMeteorSummoner(player, event);
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
        Particle[] particles = {Particle.DUST, Particle.HAPPY_VILLAGER, Particle.ANGRY_VILLAGER, 
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
        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(20);
        Location targetLoc = (targetBlock != null) ? targetBlock.getLocation().add(0, 1, 0) : player.getLocation().add(player.getLocation().getDirection().multiply(20));
        
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
        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(30);
        Location targetLoc = (targetBlock != null) ? targetBlock.getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(30));
        
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
        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(50);
        Location targetLoc = (targetBlock != null) ? targetBlock.getLocation().add(0, 1, 0) : player.getLocation().add(player.getLocation().getDirection().multiply(50));
        
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
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Check if player has damage immunity from gadgets
        if (damageImmunity.containsKey(player)) {
            long immunityEnd = damageImmunity.get(player);
            if (System.currentTimeMillis() < immunityEnd) {
                event.setCancelled(true);
                player.sendMessage("§6✦ Gadget protection active! ✦");
                return;
            } else {
                // Immunity expired, remove it
                damageImmunity.remove(player);
            }
        }
        
        // Check if player is using any active gadget
        if (com.sneaky.cosmetics.cosmetics.gadgets.GadgetCosmetic.getActiveGadget(player) != null) {
            // Provide brief damage immunity (1 second) while using gadgets
            grantDamageImmunity(player, 1000);
            event.setCancelled(true);
            player.sendMessage("§a✦ Gadget user protection! ✦");
        }
    }
    
    private void grantDamageImmunity(Player player, long durationMs) {
        long endTime = System.currentTimeMillis() + durationMs;
        damageImmunity.put(player, endTime);
        
        // Remove immunity after duration
        new BukkitRunnable() {
            @Override
            public void run() {
                damageImmunity.remove(player);
            }
        }.runTaskLater(plugin, durationMs / 50L); // Convert ms to ticks
    }
    
    private void handleRocketLauncher(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, ROCKET_LAUNCHER_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Launch explosive firework as rocket
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(1));
        Firework rocket = (Firework) player.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        
        FireworkMeta meta = rocket.getFireworkMeta();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withColor(Color.RED, Color.ORANGE, Color.YELLOW);
        builder.with(FireworkEffect.Type.BALL_LARGE);
        builder.withTrail();
        builder.withFlicker();
        
        meta.addEffect(builder.build());
        meta.setPower(2);
        rocket.setFireworkMeta(meta);
        
        // Set velocity toward target
        Vector direction = player.getLocation().getDirection().multiply(1.5);
        rocket.setVelocity(direction);
        
        // Create explosion effects where it lands
        new BukkitRunnable() {
            @Override
            public void run() {
                if (rocket.isDead() || !rocket.isValid()) {
                    Location explodeLoc = rocket.getLocation();
                    explodeLoc.getWorld().spawnParticle(Particle.EXPLOSION, explodeLoc, 20, 3.0, 3.0, 3.0, 0.1);
                    explodeLoc.getWorld().spawnParticle(Particle.LAVA, explodeLoc, 30, 2.0, 2.0, 2.0, 0.2);
                    explodeLoc.getWorld().playSound(explodeLoc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.8f);
                    
                    // Push nearby entities away
                    for (Entity entity : explodeLoc.getWorld().getNearbyEntities(explodeLoc, 8, 4, 8)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            Vector pushDirection = entity.getLocation().toVector().subtract(explodeLoc.toVector()).normalize();
                            pushDirection.multiply(2.0);
                            pushDirection.setY(Math.max(pushDirection.getY(), 0.8));
                            entity.setVelocity(pushDirection);
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.5f, 0.8f);
        player.sendMessage("§c✓ Rocket launched! Incoming explosion!");
        
        // Grant brief damage immunity
        grantDamageImmunity(player, 3000); // 3 seconds
    }
    
    private void handleForceField(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, FORCE_FIELD_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        player.sendMessage("§b✓ Force field activated!");
        grantDamageImmunity(player, 10000); // 10 seconds immunity
        
        // Visual force field effect
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 200 || !player.isOnline()) { // 10 seconds
                    cancel();
                    return;
                }
                
                Location playerLoc = player.getLocation().add(0, 1, 0);
                
                // Create rotating particle shield
                for (int i = 0; i < 360; i += 30) {
                    double angle = Math.toRadians(i + (ticks * 5));
                    double x = Math.cos(angle) * 3;
                    double z = Math.sin(angle) * 3;
                    Location particleLoc = playerLoc.clone().add(x, 0, z);
                    player.getWorld().spawnParticle(Particle.BLOCK, particleLoc, 1, 0, 0, 0, 0, Material.BARRIER.createBlockData());
                    player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
                }
                
                // Vertical rings
                for (int y = -1; y <= 2; y++) {
                    for (int i = 0; i < 360; i += 45) {
                        double angle = Math.toRadians(i + (ticks * 3));
                        double x = Math.cos(angle) * 2.5;
                        double z = Math.sin(angle) * 2.5;
                        Location particleLoc = playerLoc.clone().add(x, y, z);
                        player.getWorld().spawnParticle(Particle.ENCHANT, particleLoc, 1, 0, 0, 0, 0);
                    }
                }
                
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
        
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);
    }
    
    private void handleTimeFreezer(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, TIME_FREEZER_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Freeze all nearby entities for 5 seconds
        Collection<Entity> nearbyEntities = player.getNearbyEntities(15, 8, 15);
        
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                LivingEntity living = (LivingEntity) entity;
                living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SLOWNESS, 100, 255
                ));
                living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.MINING_FATIGUE, 100, 255
                ));
                
                // Frozen effect particles
                Location entityLoc = entity.getLocation();
                player.getWorld().spawnParticle(Particle.BLOCK, entityLoc.add(0, 1, 0), 20, 
                    1.0, 1.0, 1.0, 0, org.bukkit.Material.ICE.createBlockData());
            }
        }
        
        // Time freeze visual effects
        Location playerLoc = player.getLocation();
        for (int i = 0; i < 50; i++) {
            double angle = Math.toRadians(i * 7.2);
            double radius = 10;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location particleLoc = playerLoc.clone().add(x, 2, z);
            player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
            player.getWorld().spawnParticle(Particle.WITCH, particleLoc, 1, 0, 0, 0, 0);
        }
        
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 0.5f);
        player.sendMessage("§f✓ Time frozen! All creatures slowed!");
        grantDamageImmunity(player, 5000);
    }
    
    private void handleEnergySword(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, ENERGY_SWORD_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection();
        
        // Create energy slash effect
        for (int i = 1; i <= 8; i++) {
            Location slashLoc = loc.clone().add(direction.clone().multiply(i * 0.8));
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, slashLoc, 2, 0.3, 0.3, 0.3, 0.1);
            player.getWorld().spawnParticle(Particle.CRIT, slashLoc, 3, 0.2, 0.2, 0.2, 0.2);
            player.getWorld().spawnParticle(Particle.ENCHANT, slashLoc, 5, 0.4, 0.4, 0.4, 0.3);
        }
        
        // Damage and knockback nearby enemies
        for (Entity entity : player.getNearbyEntities(8, 4, 8)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                LivingEntity living = (LivingEntity) entity;
                
                // Push away with energy
                Vector pushDirection = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                pushDirection.multiply(2.5);
                pushDirection.setY(0.8);
                entity.setVelocity(pushDirection);
                
                // Add glowing effect
                living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.GLOWING, 60, 0
                ));
                
                // Energy impact particles
                Location impactLoc = entity.getLocation().add(0, 1, 0);
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, impactLoc, 15, 0.5, 0.5, 0.5, 0.3);
            }
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 2.0f);
        player.sendMessage("§e✓ Energy sword slash! ⚡");
        grantDamageImmunity(player, 2000);
    }
    
    private void handleMeteorSummoner(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, METEOR_SUMMONER_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Get target location
        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(50);
        Location targetLoc = (targetBlock != null) ? targetBlock.getLocation() : player.getLocation().add(player.getLocation().getDirection().multiply(50));
        
        player.sendMessage("§c✓ Meteor summoned! Incoming impact in 3 seconds!");
        
        // Warning effects at target
        for (int i = 0; i < 3; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    targetLoc.getWorld().spawnParticle(Particle.LAVA, targetLoc.clone().add(0, 10, 0), 30, 5.0, 5.0, 5.0, 0.1);
                    targetLoc.getWorld().spawnParticle(Particle.FLAME, targetLoc.clone().add(0, 8, 0), 50, 3.0, 3.0, 3.0, 0.2);
                    targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.5f);
                }
            }.runTaskLater(plugin, i * 20L);
        }
        
        // Meteor impact after 3 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                // Massive explosion effect
                targetLoc.getWorld().spawnParticle(Particle.EXPLOSION, targetLoc, 50, 5.0, 5.0, 5.0, 0.2);
                targetLoc.getWorld().spawnParticle(Particle.LAVA, targetLoc, 100, 8.0, 8.0, 8.0, 0.3);
                targetLoc.getWorld().spawnParticle(Particle.FLAME, targetLoc, 200, 10.0, 10.0, 10.0, 0.5);
                targetLoc.getWorld().spawnParticle(Particle.SMOKE, targetLoc, 150, 8.0, 8.0, 8.0, 0.4);
                
                // Sound effects
                targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
                targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5f, 0.8f);
                
                // Push entities away from impact
                for (Entity entity : targetLoc.getWorld().getNearbyEntities(targetLoc, 15, 8, 15)) {
                    if (entity instanceof LivingEntity) {
                        Vector pushDirection = entity.getLocation().toVector().subtract(targetLoc.toVector()).normalize();
                        pushDirection.multiply(3.0);
                        pushDirection.setY(Math.max(pushDirection.getY(), 1.5));
                        entity.setVelocity(pushDirection);
                        
                        if (entity instanceof Player && entity != player) {
                            ((Player) entity).sendMessage("§c☀ Meteor impact! Take cover!");
                        }
                    }
                }
                
                player.sendMessage("§c✓ Meteor impact successful! ☀");
            }
        }.runTaskLater(plugin, 60L); // 3 seconds delay
        
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
        grantDamageImmunity(player, 8000); // 8 seconds immunity
    }
    
    private void handleInvisibilityCloak(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, INVISIBILITY_CLOAK_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Make player invisible
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.INVISIBILITY, 200, 0 // 10 seconds
        ));
        
        // Cloaking effects
        Location playerLoc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.WITCH, playerLoc, 30, 1.0, 1.0, 1.0, 0.2);
        player.getWorld().spawnParticle(Particle.ENCHANT, playerLoc, 50, 2.0, 2.0, 2.0, 0.3);
        
        player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.5f);
        player.sendMessage("§7✓ Invisibility cloak activated! You are hidden for 10 seconds.");
        grantDamageImmunity(player, 10000); // 10 seconds immunity while invisible
        
        // Show uncloaking effect after duration
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    Location loc = player.getLocation().add(0, 1, 0);
                    player.getWorld().spawnParticle(Particle.FLASH, loc, 10, 1.0, 1.0, 1.0, 0.1);
                    player.sendMessage("§7✓ Invisibility cloak deactivated.");
                }
            }
        }.runTaskLater(plugin, 200L);
    }
    
    private void handleSpeedBooster(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, SPEED_BOOSTER_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Super speed effect
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.SPEED, 120, 3 // 6 seconds, level 4
        ));
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.JUMP_BOOST, 120, 2 // 6 seconds, level 3
        ));
        
        // Speed trail effect
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 120 || !player.isOnline()) {
                    cancel();
                    return;
                }
                
                Location playerLoc = player.getLocation();
                player.getWorld().spawnParticle(Particle.DUST, playerLoc, 5, 0.5, 0.1, 0.5, 0, 
                    new Particle.DustOptions(Color.fromRGB(0, 255, 255), 1.0f));
                player.getWorld().spawnParticle(Particle.CRIT, playerLoc, 3, 0.3, 0.3, 0.3, 0.1);
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        player.playSound(player.getLocation(), Sound.ENTITY_HORSE_GALLOP, 1.0f, 1.5f);
        player.sendMessage("§f✓ Speed boost activated! ⚡");
        grantDamageImmunity(player, 6000); // 6 seconds immunity
    }
    
    private void handleHealingStaff(Player player, PlayerInteractEvent event) {
        if (!checkCooldown(player, HEALING_STAFF_COOLDOWN)) {
            return;
        }
        
        event.setCancelled(true);
        
        // Heal player to full health
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        
        // Remove negative effects
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.REGENERATION, 100, 2
        ));
        
        // Healing effects
        Location playerLoc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.HEART, playerLoc, 20, 2.0, 2.0, 2.0, 0.2);
        player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, playerLoc, 30, 1.5, 1.5, 1.5, 0.1);
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, playerLoc, 15, 1.0, 1.0, 1.0, 0.3);
        
        // Heal nearby players too
        for (Entity entity : player.getNearbyEntities(8, 4, 8)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;
                nearbyPlayer.setHealth(Math.min(nearbyPlayer.getMaxHealth(), nearbyPlayer.getHealth() + 6));
                nearbyPlayer.sendMessage("§a✓ You were healed by " + player.getName() + "'s healing staff!");
                
                Location healLoc = nearbyPlayer.getLocation().add(0, 1, 0);
                nearbyPlayer.getWorld().spawnParticle(Particle.HEART, healLoc, 10, 1.0, 1.0, 1.0, 0.1);
            }
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 1.5f);
        player.sendMessage("§a✓ Healing staff activated! You and nearby players are healed! ❤");
        grantDamageImmunity(player, 3000); // 3 seconds immunity
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
        damageImmunity.clear();
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