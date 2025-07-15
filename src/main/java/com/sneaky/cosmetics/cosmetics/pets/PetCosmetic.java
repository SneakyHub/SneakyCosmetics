package com.sneaky.cosmetics.cosmetics.pets;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.Cosmetic;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced pet cosmetic that spawns and manages pet entities with riding, feeding, leveling, and naming
 */
public class PetCosmetic extends Cosmetic implements Listener {
    
    private final EntityType entityType;
    private final boolean isBaby;
    private final String defaultPetName;
    private final SneakyCosmetics plugin;
    
    private static final Map<Player, Entity> activePets = new ConcurrentHashMap<>();
    private static final Map<Player, BukkitRunnable> petTasks = new ConcurrentHashMap<>();
    private static final Map<Player, PetData> petDataCache = new ConcurrentHashMap<>();
    private static final Map<Player, Long> lastInteractionTime = new ConcurrentHashMap<>();
    private static final Map<UUID, Player> ridingPets = new ConcurrentHashMap<>();
    
    public PetCosmetic(String id, String displayName, int price, Material iconMaterial,
                      List<String> description, String permission, boolean requiresVIP,
                      boolean requiresPremium, EntityType entityType, boolean isBaby,
                      String defaultPetName, SneakyCosmetics plugin) {
        super(id, displayName, CosmeticType.PET, price, iconMaterial, description, 
              permission, requiresVIP, requiresPremium);
        this.entityType = entityType;
        this.isBaby = isBaby;
        this.defaultPetName = defaultPetName;
        this.plugin = plugin;
        
        // Register event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public void activate(Player player) {
        // Remove any existing pet
        deactivate(player);
        
        // Load or create pet data
        PetData petData = loadPetData(player);
        petDataCache.put(player, petData);
        
        // Spawn the pet near the player
        Location spawnLocation = player.getLocation().add(1, 0, 1);
        Entity pet = player.getWorld().spawnEntity(spawnLocation, entityType);
        
        if (pet instanceof LivingEntity) {
            LivingEntity livingPet = (LivingEntity) pet;
            
            // Set pet properties
            livingPet.setAI(false); // Disable AI to prevent natural behavior
            livingPet.setInvulnerable(true); // Make pet invulnerable
            livingPet.setSilent(false); // Allow sounds for interaction feedback
            livingPet.setRemoveWhenFarAway(false); // Don't despawn
            
            // Set baby if applicable
            if (isBaby && livingPet instanceof org.bukkit.entity.Ageable) {
                ((org.bukkit.entity.Ageable) livingPet).setBaby();
            }
            
            // Set custom name with level and mood
            updatePetNameAndDisplay(livingPet, petData);
            
            // Apply level-based improvements
            applyLevelBonuses(livingPet, petData);
            
            activePets.put(player, pet);
            
            // Start follow task
            startEnhancedFollowTask(player, pet, petData);
            
            // Show activation message
            plugin.getMessageManager().sendSuccess(player, "§a✓ Your pet " + petData.getCustomName() + " (Level " + petData.getLevel() + ") is now active!");
            
            // Show mood if not happy
            if (petData.getMood() != PetData.PetMood.HAPPY) {
                plugin.getMessageManager().sendMessage(player, "§7" + petData.getMood().getDescription());
            }
        }
    }
    
    @Override
    public void deactivate(Player player) {
        // Save pet data if exists
        PetData petData = petDataCache.get(player);
        if (petData != null) {
            savePetData(petData);
            petDataCache.remove(player);
        }
        
        // Handle riding
        if (ridingPets.containsKey(player.getUniqueId())) {
            stopRiding(player);
        }
        
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
        
        // Clear interaction tracking
        lastInteractionTime.remove(player);
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
    
    /**
     * Start enhanced follow task with experience tracking
     */
    private void startEnhancedFollowTask(Player player, Entity pet, PetData petData) {
        BukkitRunnable followTask = new BukkitRunnable() {
            private long lastUpdateTime = System.currentTimeMillis();
            
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
                
                // Track pet time for experience
                long currentTime = System.currentTimeMillis();
                long timeDiff = currentTime - lastUpdateTime;
                petData.addPetTime(timeDiff);
                lastUpdateTime = currentTime;
                
                // Update pet display every few seconds
                if (currentTime % 3000 < 500) { // Roughly every 3 seconds
                    updatePetNameAndDisplay((LivingEntity) pet, petData);
                }
                
                Location playerLocation = player.getLocation();
                Location petLocation = pet.getLocation();
                
                // Check distance
                double distance = petLocation.distance(playerLocation);
                
                // Get speed multiplier based on level
                double speedMultiplier = 1.0 + (petData.getAbilityLevel("speed") * 0.1);
                
                // Use improved pathfinding system
                PetPathfinder.MovementResult movement = PetPathfinder.calculateMovement(pet, player, speedMultiplier);
                
                switch (movement.getType()) {
                    case TELEPORT:
                        Location teleportLoc = movement.getLocation();
                        if (teleportLoc != null) {
                            // Play teleport effect at old location
                            pet.getWorld().spawnParticle(Particle.PORTAL, petLocation, 15, 0.5, 0.5, 0.5, 0.1);
                            pet.getWorld().playSound(petLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.2f);
                            
                            // Teleport pet
                            pet.teleport(teleportLoc);
                            
                            // Play teleport effect at new location
                            pet.getWorld().spawnParticle(Particle.PORTAL, teleportLoc, 15, 0.5, 0.5, 0.5, 0.1);
                            pet.getWorld().playSound(teleportLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.2f);
                        }
                        break;
                        
                    case WALK:
                        Location walkLoc = movement.getLocation();
                        if (walkLoc != null) {
                            // Check if pet should jump
                            if (PetPathfinder.shouldJump(petLocation, walkLoc) && pet instanceof LivingEntity) {
                                PetPathfinder.applyJump((LivingEntity) pet);
                            }
                            
                            // Smooth movement with velocity
                            if (pet instanceof LivingEntity) {
                                LivingEntity livingPet = (LivingEntity) pet;
                                Vector velocity = walkLoc.toVector().subtract(petLocation.toVector());
                                
                                // Limit velocity to prevent pets from moving too fast
                                double maxVelocity = 0.5 * speedMultiplier;
                                if (velocity.length() > maxVelocity) {
                                    velocity.normalize().multiply(maxVelocity);
                                }
                                
                                // Preserve Y velocity for jumping/falling
                                velocity.setY(Math.max(velocity.getY(), livingPet.getVelocity().getY()));
                                
                                livingPet.setVelocity(velocity);
                            } else {
                                // Fallback to teleportation for non-living entities
                                pet.teleport(walkLoc);
                            }
                        }
                        break;
                        
                    case JUMP:
                        if (pet instanceof LivingEntity) {
                            PetPathfinder.applyJump((LivingEntity) pet);
                        }
                        break;
                        
                    case IDLE:
                        // Pet is close enough, no movement needed
                        break;
                }
                
                // Spawn mood particles
                spawnMoodParticles(pet, petData);
            }
        };
        
        followTask.runTaskTimer(plugin, 0L, 6L); // Every 0.3 seconds for smoother movement
        petTasks.put(player, followTask);
    }
    
    // ===============================
    // PET INTERACTION EVENT HANDLERS
    // ===============================
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        
        // Check if this is the player's active pet
        Entity playerPet = activePets.get(player);
        if (playerPet == null || !playerPet.equals(entity)) return;
        
        // Prevent default interactions
        event.setCancelled(true);
        
        // Only handle main hand interactions
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        PetData petData = petDataCache.get(player);
        if (petData == null) return;
        
        // Check interaction cooldown
        long currentTime = System.currentTimeMillis();
        Long lastInteraction = lastInteractionTime.get(player);
        if (lastInteraction != null && currentTime - lastInteraction < 1000) return; // 1 second cooldown
        
        lastInteractionTime.put(player, currentTime);
        
        // Handle different interactions based on what player is holding
        Material heldItem = player.getInventory().getItemInMainHand().getType();
        
        if (player.isSneaking()) {
            // Sneaking + Right click = Pet menu
            openPetInteractionMenu(player, petData);
        } else if (isRideable(entity) && petData.hasUnlockedFeature("riding")) {
            // Normal right click on rideable pet = Start riding
            startRiding(player, entity, petData);
        } else if (isFoodItem(heldItem)) {
            // Right click with food = Feed pet
            feedPet(player, petData, heldItem);
        } else {
            // Normal interaction = Pet the pet
            petThePet(player, petData);
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking() && ridingPets.containsKey(player.getUniqueId())) {
            // Player stopped sneaking while riding - dismount
            stopRiding(player);
        }
    }
    
    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player player = (Player) event.getExited();
            if (ridingPets.containsKey(player.getUniqueId())) {
                ridingPets.remove(player.getUniqueId());
                
                PetData petData = petDataCache.get(player);
                if (petData != null) {
                    petData.setRiding(false);
                    petData.addExperience(PetData.EXP_PER_RIDE);
                    
                    plugin.getMessageManager().sendMessage(player, "§7You dismounted your pet!");
                }
            }
        }
    }
    
    // ===============================
    // PET INTERACTION METHODS
    // ===============================
    
    /**
     * Start riding the pet
     */
    private void startRiding(Player player, Entity pet, PetData petData) {
        if (ridingPets.containsKey(player.getUniqueId())) {
            plugin.getMessageManager().sendError(player, "You are already riding a pet!");
            return;
        }
        
        if (petData.isHungry()) {
            plugin.getMessageManager().sendError(player, "Your pet is too hungry to be ridden! Feed it first.");
            return;
        }
        
        // Add player as passenger
        pet.addPassenger(player);
        ridingPets.put(player.getUniqueId(), player);
        petData.setRiding(true);
        
        // Apply riding effects based on pet level
        int speedLevel = petData.getAbilityLevel("speed");
        int jumpLevel = petData.getAbilityLevel("jump");
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speedLevel - 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, jumpLevel - 1, false, false));
        
        // Play mounting sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1.0f, 1.2f);
        
        plugin.getMessageManager().sendSuccess(player, "§a✓ You are now riding " + petData.getCustomName() + "!");
        plugin.getMessageManager().sendMessage(player, "§7Sneak to dismount. Speed: " + speedLevel + " | Jump: " + jumpLevel);
    }
    
    /**
     * Stop riding the pet
     */
    private void stopRiding(Player player) {
        ridingPets.remove(player.getUniqueId());
        
        // Remove riding effects
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        
        // Eject from vehicle
        if (player.getVehicle() != null) {
            player.getVehicle().removePassenger(player);
        }
        
        PetData petData = petDataCache.get(player);
        if (petData != null) {
            petData.setRiding(false);
            petData.addExperience(PetData.EXP_PER_RIDE);
        }
        
        // Play dismount sound
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1.0f, 0.8f);
    }
    
    /**
     * Feed the pet
     */
    private void feedPet(Player player, PetData petData, Material food) {
        if (!petData.feed()) {
            plugin.getMessageManager().sendError(player, "Your pet is not hungry right now!");
            return;
        }
        
        // Remove food item
        player.getInventory().getItemInMainHand().setAmount(
            player.getInventory().getItemInMainHand().getAmount() - 1
        );
        
        // Play feeding effects
        Entity pet = activePets.get(player);
        if (pet != null) {
            pet.getWorld().spawnParticle(Particle.HEART, pet.getLocation().add(0, 1, 0), 5);
            pet.getWorld().playSound(pet.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.2f);
        }
        
        plugin.getMessageManager().sendSuccess(player, "§a✓ You fed " + petData.getCustomName() + "!");
        plugin.getMessageManager().sendMessage(player, "§7Happiness: " + petData.getHappiness() + "/100 " + petData.getMood().getIcon());
        
        // Check for level up
        checkLevelUp(player, petData);
    }
    
    /**
     * Pet the pet (simple interaction)
     */
    private void petThePet(Player player, PetData petData) {
        petData.addExperience(PetData.EXP_PER_INTERACTION);
        
        Entity pet = activePets.get(player);
        if (pet != null) {
            // Play petting effects
            pet.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, pet.getLocation().add(0, 1, 0), 3);
            pet.getWorld().playSound(pet.getLocation(), Sound.ENTITY_CAT_PURR, 0.7f, 1.5f);
        }
        
        plugin.getMessageManager().sendMessage(player, "§7You petted " + petData.getCustomName() + " " + petData.getMood().getIcon());
        
        // Check for level up
        checkLevelUp(player, petData);
    }
    
    /**
     * Open pet interaction menu
     */
    private void openPetInteractionMenu(Player player, PetData petData) {
        // This will be implemented when we update the GUI
        plugin.getMessageManager().sendMessage(player, "§ePet Menu: " + petData.getCustomName() + " (Level " + petData.getLevel() + ")");
        plugin.getMessageManager().sendMessage(player, "§7Happiness: " + petData.getHappiness() + "/100 " + petData.getMood().getIcon());
        plugin.getMessageManager().sendMessage(player, "§7Experience: " + petData.getExperience() + "/" + petData.getExperienceRequiredForNextLevel() + " (" + String.format("%.1f", petData.getExperienceProgress()) + "%)");
        plugin.getMessageManager().sendMessage(player, "§7Age: " + petData.getAgeInDays() + " days | Time: " + petData.getFormattedPetTime());
    }
    
    // ===============================
    // HELPER METHODS
    // ===============================
    
    /**
     * Load pet data from database
     */
    private PetData loadPetData(Player player) {
        // For now, create new pet data - this will be enhanced when database is updated
        String customName = plugin.getDatabaseManager().getPetCustomName(player.getUniqueId(), getId());
        
        PetData petData = new PetData(player.getUniqueId(), getId());
        if (customName != null && !customName.isEmpty()) {
            petData.setCustomName(customName);
        } else {
            petData.setCustomName(player.getName() + "'s " + defaultPetName);
        }
        
        return petData;
    }
    
    /**
     * Save pet data to database
     */
    private void savePetData(PetData petData) {
        // Save custom name
        plugin.getDatabaseManager().setPetCustomName(
            petData.getPlayerUUID(), 
            petData.getPetId(), 
            petData.getCustomName()
        );
        
        // TODO: Save other pet data when database schema is updated
    }
    
    /**
     * Update pet name and display
     */
    private void updatePetNameAndDisplay(LivingEntity pet, PetData petData) {
        String displayName = "§e" + petData.getCustomName() + " §7(Lv." + petData.getLevel() + ") " + petData.getMood().getIcon();
        pet.setCustomName(displayName);
        pet.setCustomNameVisible(true);
    }
    
    /**
     * Apply level-based bonuses to pet
     */
    private void applyLevelBonuses(LivingEntity pet, PetData petData) {
        // Apply health bonus
        double healthBonus = 1.0 + (petData.getLevel() * 0.1);
        pet.setMaxHealth(pet.getMaxHealth() * healthBonus);
        pet.setHealth(pet.getMaxHealth());
        
        // Apply size scaling for some pets
        if (pet instanceof Slime) {
            Slime slime = (Slime) pet;
            int size = Math.min(1 + (petData.getLevel() / 10), 4);
            slime.setSize(size);
        }
    }
    
    /**
     * Spawn mood particles around pet
     */
    private void spawnMoodParticles(Entity pet, PetData petData) {
        Location loc = pet.getLocation().add(0, 1, 0);
        PetData.PetMood mood = petData.getMood();
        
        switch (mood) {
            case HAPPY:
                if (Math.random() < 0.3) {
                    pet.getWorld().spawnParticle(Particle.HEART, loc, 1);
                }
                break;
            case CONTENT:
                if (Math.random() < 0.1) {
                    pet.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 1);
                }
                break;
            case SAD:
                if (Math.random() < 0.2) {
                    pet.getWorld().spawnParticle(Particle.SMOKE, loc, 1);
                }
                break;
            case VERY_SAD:
                if (Math.random() < 0.4) {
                    pet.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, loc, 1);
                }
                break;
        }
    }
    
    /**
     * Check if entity is rideable
     */
    private boolean isRideable(Entity entity) {
        return entity instanceof Horse || entity instanceof Pig || entity instanceof Llama ||
               entity instanceof Wolf || entity instanceof Ocelot || entity.getType() == EntityType.PANDA;
    }
    
    /**
     * Check if material is pet food
     */
    private boolean isFoodItem(Material material) {
        switch (material) {
            case BONE:
            case COOKED_BEEF:
            case COOKED_PORKCHOP:
            case COOKED_CHICKEN:
            case COOKED_MUTTON:
            case BREAD:
            case APPLE:
            case CARROT:
            case POTATO:
            case WHEAT:
            case WHEAT_SEEDS:
            case MELON_SEEDS:
            case PUMPKIN_SEEDS:
            case BEETROOT_SEEDS:
            case TROPICAL_FISH:
            case COD:
            case SALMON:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Check for level up and notify player
     */
    private void checkLevelUp(Player player, PetData petData) {
        // The addExperience method already handles level ups, just check if it happened
        // and the level up notifications are handled in PetData.onLevelUp()
        // Here we can add additional level up effects
        
        Entity pet = activePets.get(player);
        if (pet != null) {
            // Update display after potential level up
            updatePetNameAndDisplay((LivingEntity) pet, petData);
        }
    }
    
    // ===============================
    // STATIC UTILITY METHODS
    // ===============================
    
    /**
     * Get the current pet entity for a player
     */
    public static Entity getPlayerPet(Player player) {
        return activePets.get(player);
    }
    
    /**
     * Get pet data for a player
     */
    public static PetData getPlayerPetData(Player player) {
        return petDataCache.get(player);
    }
    
    /**
     * Update the pet's custom name
     */
    public void updatePetName(Player player, String newName) {
        PetData petData = petDataCache.get(player);
        if (petData != null) {
            petData.setCustomName(newName);
            
            Entity pet = activePets.get(player);
            if (pet instanceof LivingEntity) {
                updatePetNameAndDisplay((LivingEntity) pet, petData);
            }
            
            // Save to database
            plugin.getDatabaseManager().setPetCustomName(player.getUniqueId(), getId(), newName);
        }
    }
    
    /**
     * Clean up all pets (for plugin disable)
     */
    public static void cleanupAllPets() {
        // Save all pet data
        for (Map.Entry<Player, PetData> entry : petDataCache.entrySet()) {
            // Auto-save would be handled by the plugin's shutdown process
        }
        
        // Remove all pets
        for (Entity pet : activePets.values()) {
            if (pet != null && !pet.isDead()) {
                pet.remove();
            }
        }
        activePets.clear();
        
        // Cancel all tasks
        for (BukkitRunnable task : petTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        petTasks.clear();
        
        // Clear caches
        petDataCache.clear();
        lastInteractionTime.clear();
        ridingPets.clear();
    }
    
    // ===============================
    // GETTERS
    // ===============================
    
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