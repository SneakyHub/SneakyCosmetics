package com.sneaky.cosmetics.cosmetics.pets;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pet cosmetic that spawns and manages pet entities that follow the player
 */
public class PetCosmetic extends Cosmetic {
    
    private final EntityType entityType;
    private final boolean isBaby;
    private final String defaultPetName;
    private final Plugin plugin;
    
    private static final Map<Player, Entity> activePets = new ConcurrentHashMap<>();
    private static final Map<Player, BukkitRunnable> petTasks = new ConcurrentHashMap<>();
    
    public PetCosmetic(String id, String displayName, int price, Material iconMaterial,
                      List<String> description, String permission, boolean requiresVIP,
                      boolean requiresPremium, EntityType entityType, boolean isBaby,
                      String defaultPetName, Plugin plugin) {
        super(id, displayName, CosmeticType.PET, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.entityType = entityType;
        this.isBaby = isBaby;
        this.defaultPetName = defaultPetName;
        this.plugin = plugin;
    }
    
    @Override
    public void activate(Player player) {
        // Remove any existing pet
        deactivate(player);
        
        // Spawn the pet near the player
        Location spawnLocation = player.getLocation().add(1, 0, 1);
        Entity pet = player.getWorld().spawnEntity(spawnLocation, entityType);
        
        if (pet instanceof LivingEntity) {
            LivingEntity livingPet = (LivingEntity) pet;
            
            // Set pet properties
            livingPet.setAI(false); // Disable AI to prevent natural behavior
            livingPet.setInvulnerable(true); // Make pet invulnerable
            livingPet.setSilent(true); // Make pet silent
            livingPet.setRemoveWhenFarAway(false); // Don't despawn
            
            // Set baby if applicable
            if (isBaby && livingPet instanceof org.bukkit.entity.Ageable) {
                ((org.bukkit.entity.Ageable) livingPet).setBaby();
            }
            
            // Set custom name
            // Get custom name from database
            String customName = null;
            if (plugin instanceof com.sneaky.cosmetics.SneakyCosmetics) {
                customName = ((com.sneaky.cosmetics.SneakyCosmetics) plugin).getDatabaseManager().getPetCustomName(player.getUniqueId(), getId());
            }
            if (customName != null && !customName.isEmpty()) {
                livingPet.setCustomName("§e" + customName);
            } else {
                livingPet.setCustomName("§e" + player.getName() + "'s " + defaultPetName);
            }
            livingPet.setCustomNameVisible(true);
            
            activePets.put(player, pet);
            
            // Start follow task
            startFollowTask(player, pet);
        }
    }
    
    @Override
    public void deactivate(Player player) {
        // Remove existing pet
        Entity existingPet = activePets.remove(player);
        if (existingPet != null && !existingPet.isDead()) {
            existingPet.remove();
        }
        
        // Stop follow task
        BukkitRunnable task = petTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }
    
    @Override
    public boolean isActive(Player player) {
        Entity pet = activePets.get(player);
        return pet != null && !pet.isDead();
    }
    
    @Override
    public void cleanup(Player player) {
        deactivate(player);
    }
    
    private void startFollowTask(Player player, Entity pet) {
        BukkitRunnable followTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || pet.isDead()) {
                    this.cancel();
                    activePets.remove(player);
                    petTasks.remove(player);
                    if (!pet.isDead()) {
                        pet.remove();
                    }
                    return;
                }
                
                Location playerLocation = player.getLocation();
                Location petLocation = pet.getLocation();
                
                // Check distance
                double distance = petLocation.distance(playerLocation);
                
                // Teleport if too far
                if (distance > 12) {
                    Location teleportLocation = playerLocation.add(
                        Math.random() * 4 - 2, 0, Math.random() * 4 - 2
                    );
                    pet.teleport(teleportLocation);
                } else if (distance > 3) {
                    // Move towards player
                    Location targetLocation = playerLocation.clone().add(
                        Math.random() * 2 - 1, 0, Math.random() * 2 - 1
                    );
                    
                    // Simple movement towards target
                    double deltaX = targetLocation.getX() - petLocation.getX();
                    double deltaZ = targetLocation.getZ() - petLocation.getZ();
                    double deltaY = targetLocation.getY() - petLocation.getY();
                    
                    // Normalize and apply speed
                    double speed = 0.3;
                    double length = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                    if (length > 0) {
                        deltaX = (deltaX / length) * speed;
                        deltaZ = (deltaZ / length) * speed;
                    }
                    
                    Location newLocation = petLocation.add(deltaX, deltaY, deltaZ);
                    pet.teleport(newLocation);
                }
            }
        };
        
        followTask.runTaskTimer(plugin, 0L, 10L); // Every 0.5 seconds
        petTasks.put(player, followTask);
    }
    
    /**
     * Update the pet's custom name
     */
    public void updatePetName(Player player, String newName) {
        Entity pet = activePets.get(player);
        if (pet instanceof LivingEntity) {
            ((LivingEntity) pet).setCustomName("§e" + newName);
        }
    }
    
    /**
     * Get the current pet entity for a player
     */
    public static Entity getPlayerPet(Player player) {
        return activePets.get(player);
    }
    
    /**
     * Clean up all pets (for plugin disable)
     */
    public static void cleanupAllPets() {
        for (Entity pet : activePets.values()) {
            if (pet != null && !pet.isDead()) {
                pet.remove();
            }
        }
        activePets.clear();
        
        for (BukkitRunnable task : petTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        petTasks.clear();
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    public boolean isBaby() {
        return isBaby;
    }
    
    public String getDefaultPetName() {
        return defaultPetName;
    }
}