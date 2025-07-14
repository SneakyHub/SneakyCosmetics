package com.sneaky.cosmetics.cosmetics.pets;

import java.util.*;

/**
 * Data class to store all pet-related information including levels, experience, and interactions
 */
public class PetData {
    
    private final UUID playerUUID;
    private final String petId;
    private String customName;
    private int level;
    private int experience;
    private int happiness;
    private long lastFeedTime;
    private long totalPetTime;
    private long createdTime;
    private boolean isRiding;
    private final Map<String, Integer> abilities;
    private final List<String> unlockedFeatures;
    
    // Constants for pet progression
    public static final int MAX_LEVEL = 100;
    public static final int MAX_HAPPINESS = 100;
    public static final int BASE_EXPERIENCE_PER_LEVEL = 100;
    public static final long HAPPINESS_DECAY_INTERVAL = 3600000; // 1 hour in milliseconds
    public static final int HUNGER_THRESHOLD = 50; // Pet gets hungry when happiness drops below this
    
    // Experience rewards
    public static final int EXP_PER_FEED = 10;
    public static final int EXP_PER_MINUTE_ACTIVE = 1;
    public static final int EXP_PER_RIDE = 5;
    public static final int EXP_PER_INTERACTION = 3;
    
    public PetData(UUID playerUUID, String petId) {
        this.playerUUID = playerUUID;
        this.petId = petId;
        this.customName = null; // Will be set from default or custom
        this.level = 1;
        this.experience = 0;
        this.happiness = MAX_HAPPINESS;
        this.lastFeedTime = System.currentTimeMillis();
        this.totalPetTime = 0;
        this.createdTime = System.currentTimeMillis();
        this.isRiding = false;
        this.abilities = new HashMap<>();
        this.unlockedFeatures = new ArrayList<>();
        
        // Initialize default abilities
        initializeDefaultAbilities();
    }
    
    public PetData(UUID playerUUID, String petId, String customName, int level, int experience, 
                   int happiness, long lastFeedTime, long totalPetTime, long createdTime, 
                   boolean isRiding, Map<String, Integer> abilities, List<String> unlockedFeatures) {
        this.playerUUID = playerUUID;
        this.petId = petId;
        this.customName = customName;
        this.level = Math.min(level, MAX_LEVEL);
        this.experience = Math.max(experience, 0);
        this.happiness = Math.max(0, Math.min(happiness, MAX_HAPPINESS));
        this.lastFeedTime = lastFeedTime;
        this.totalPetTime = totalPetTime;
        this.createdTime = createdTime;
        this.isRiding = isRiding;
        this.abilities = abilities != null ? new HashMap<>(abilities) : new HashMap<>();
        this.unlockedFeatures = unlockedFeatures != null ? new ArrayList<>(unlockedFeatures) : new ArrayList<>();
        
        if (this.abilities.isEmpty()) {
            initializeDefaultAbilities();
        }
    }
    
    private void initializeDefaultAbilities() {
        abilities.put("speed", 1);
        abilities.put("jump", 1);
        abilities.put("loyalty", 1);
        abilities.put("intelligence", 1);
    }
    
    /**
     * Add experience to the pet and handle level ups
     */
    public boolean addExperience(int amount) {
        if (level >= MAX_LEVEL) return false;
        
        experience += amount;
        boolean leveledUp = false;
        
        // Check for level up
        while (experience >= getExperienceRequiredForNextLevel() && level < MAX_LEVEL) {
            experience -= getExperienceRequiredForNextLevel();
            level++;
            leveledUp = true;
            onLevelUp();
        }
        
        return leveledUp;
    }
    
    /**
     * Calculate experience required for next level
     */
    public int getExperienceRequiredForNextLevel() {
        if (level >= MAX_LEVEL) return 0;
        return BASE_EXPERIENCE_PER_LEVEL + (level * 25); // Increases by 25 per level
    }
    
    /**
     * Get experience progress as percentage for current level
     */
    public double getExperienceProgress() {
        if (level >= MAX_LEVEL) return 100.0;
        int required = getExperienceRequiredForNextLevel();
        return required > 0 ? (double) experience / required * 100.0 : 0.0;
    }
    
    /**
     * Handle pet level up - unlock new features and improve abilities
     */
    private void onLevelUp() {
        // Increase abilities every few levels
        if (level % 5 == 0) {
            abilities.put("speed", Math.min(abilities.get("speed") + 1, 10));
        }
        if (level % 7 == 0) {
            abilities.put("jump", Math.min(abilities.get("jump") + 1, 10));
        }
        if (level % 3 == 0) {
            abilities.put("loyalty", Math.min(abilities.get("loyalty") + 1, 10));
        }
        if (level % 4 == 0) {
            abilities.put("intelligence", Math.min(abilities.get("intelligence") + 1, 10));
        }
        
        // Unlock features at certain levels
        if (level >= 5 && !unlockedFeatures.contains("riding")) {
            unlockedFeatures.add("riding");
        }
        if (level >= 10 && !unlockedFeatures.contains("tricks")) {
            unlockedFeatures.add("tricks");
        }
        if (level >= 15 && !unlockedFeatures.contains("combat_assist")) {
            unlockedFeatures.add("combat_assist");
        }
        if (level >= 20 && !unlockedFeatures.contains("inventory")) {
            unlockedFeatures.add("inventory");
        }
        if (level >= 25 && !unlockedFeatures.contains("teleport")) {
            unlockedFeatures.add("teleport");
        }
        if (level >= 50 && !unlockedFeatures.contains("flying")) {
            unlockedFeatures.add("flying");
        }
    }
    
    /**
     * Feed the pet to increase happiness and experience
     */
    public boolean feed() {
        if (happiness >= MAX_HAPPINESS) return false;
        
        happiness = Math.min(happiness + 20, MAX_HAPPINESS);
        lastFeedTime = System.currentTimeMillis();
        addExperience(EXP_PER_FEED);
        return true;
    }
    
    /**
     * Update happiness based on time since last feed
     */
    public void updateHappiness() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastFeed = currentTime - lastFeedTime;
        
        // Decrease happiness over time (1 point per hour)
        int happinessDecrease = (int) (timeSinceLastFeed / HAPPINESS_DECAY_INTERVAL);
        if (happinessDecrease > 0) {
            happiness = Math.max(0, happiness - happinessDecrease);
            // Update lastFeedTime to prevent continuous decay calculation
            lastFeedTime = currentTime - (timeSinceLastFeed % HAPPINESS_DECAY_INTERVAL);
        }
    }
    
    /**
     * Check if pet is hungry
     */
    public boolean isHungry() {
        updateHappiness();
        return happiness < HUNGER_THRESHOLD;
    }
    
    /**
     * Get pet mood based on happiness level
     */
    public PetMood getMood() {
        updateHappiness();
        if (happiness >= 80) return PetMood.HAPPY;
        if (happiness >= 60) return PetMood.CONTENT;
        if (happiness >= 40) return PetMood.NEUTRAL;
        if (happiness >= 20) return PetMood.SAD;
        return PetMood.VERY_SAD;
    }
    
    /**
     * Add pet time (for experience tracking)
     */
    public void addPetTime(long milliseconds) {
        totalPetTime += milliseconds;
        // Add experience for active time (1 exp per minute)
        int minutesAdded = (int) (milliseconds / 60000);
        if (minutesAdded > 0) {
            addExperience(minutesAdded * EXP_PER_MINUTE_ACTIVE);
        }
    }
    
    /**
     * Check if feature is unlocked
     */
    public boolean hasUnlockedFeature(String feature) {
        return unlockedFeatures.contains(feature);
    }
    
    /**
     * Get ability level
     */
    public int getAbilityLevel(String ability) {
        return abilities.getOrDefault(ability, 1);
    }
    
    /**
     * Get pet age in days
     */
    public int getAgeInDays() {
        long ageMillis = System.currentTimeMillis() - createdTime;
        return (int) (ageMillis / (24 * 60 * 60 * 1000));
    }
    
    /**
     * Get formatted total pet time
     */
    public String getFormattedPetTime() {
        long hours = totalPetTime / 3600000;
        long minutes = (totalPetTime % 3600000) / 60000;
        return hours + "h " + minutes + "m";
    }
    
    // Getters and Setters
    public UUID getPlayerUUID() { return playerUUID; }
    public String getPetId() { return petId; }
    public String getCustomName() { return customName; }
    public void setCustomName(String customName) { this.customName = customName; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getHappiness() { return happiness; }
    public long getLastFeedTime() { return lastFeedTime; }
    public long getTotalPetTime() { return totalPetTime; }
    public long getCreatedTime() { return createdTime; }
    public boolean isRiding() { return isRiding; }
    public void setRiding(boolean riding) { this.isRiding = riding; }
    public Map<String, Integer> getAbilities() { return new HashMap<>(abilities); }
    public List<String> getUnlockedFeatures() { return new ArrayList<>(unlockedFeatures); }
    
    /**
     * Pet mood enumeration
     */
    public enum PetMood {
        VERY_SAD("§c😢", "Very Sad", "Your pet is very unhappy and needs attention!"),
        SAD("§6😞", "Sad", "Your pet is feeling sad and could use some care."),
        NEUTRAL("§e😐", "Neutral", "Your pet is feeling okay."),
        CONTENT("§a😊", "Content", "Your pet is feeling good!"),
        HAPPY("§2😄", "Happy", "Your pet is very happy and full of energy!");
        
        private final String icon;
        private final String name;
        private final String description;
        
        PetMood(String icon, String name, String description) {
            this.icon = icon;
            this.name = name;
            this.description = description;
        }
        
        public String getIcon() { return icon; }
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
    
    @Override
    public String toString() {
        return "PetData{" +
                "playerUUID=" + playerUUID +
                ", petId='" + petId + '\'' +
                ", customName='" + customName + '\'' +
                ", level=" + level +
                ", experience=" + experience +
                ", happiness=" + happiness +
                ", mood=" + getMood() +
                '}';
    }
}