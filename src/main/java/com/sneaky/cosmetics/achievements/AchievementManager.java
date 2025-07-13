package com.sneaky.cosmetics.achievements;

import com.sneaky.cosmetics.SneakyCosmetics;
import com.sneaky.cosmetics.cosmetics.CosmeticType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages achievements and player progress
 */
public class AchievementManager {
    
    private final SneakyCosmetics plugin;
    private final Map<String, Achievement> achievements = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> playerAchievements = new ConcurrentHashMap<>();
    
    public AchievementManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
        registerAchievements();
    }
    
    private void registerAchievements() {
        // First cosmetic achievement
        Achievement firstCosmetic = new Achievement(
            "first_cosmetic",
            "First Steps",
            "Purchase your very first cosmetic!",
            Material.EMERALD,
            Arrays.asList("Purchase any cosmetic from the shop"),
            100,
            Achievement.AchievementType.FIRST_COSMETIC,
            null
        );
        registerAchievement(firstCosmetic);
        
        // Cosmetic collector achievements
        Achievement collector5 = new Achievement(
            "collector_5",
            "Collector",
            "Own 5 different cosmetics",
            Material.CHEST,
            Arrays.asList("Purchase 5 different cosmetics"),
            250,
            Achievement.AchievementType.COSMETIC_COUNT,
            5
        );
        registerAchievement(collector5);
        
        Achievement collector10 = new Achievement(
            "collector_10",
            "Enthusiast",
            "Own 10 different cosmetics",
            Material.ENDER_CHEST,
            Arrays.asList("Purchase 10 different cosmetics"),
            500,
            Achievement.AchievementType.COSMETIC_COUNT,
            10
        );
        registerAchievement(collector10);
        
        Achievement collector25 = new Achievement(
            "collector_25",
            "Cosmetic Master",
            "Own 25 different cosmetics",
            Material.SHULKER_BOX,
            Arrays.asList("Purchase 25 different cosmetics"),
            1000,
            Achievement.AchievementType.COSMETIC_COUNT,
            25
        );
        registerAchievement(collector25);
        
        // Type-specific achievements
        Achievement particleMaster = new Achievement(
            "particle_master",
            "Particle Master",
            "Own all particle cosmetics",
            Material.BLAZE_POWDER,
            Arrays.asList("Collect every particle effect"),
            300,
            Achievement.AchievementType.TYPE_COLLECTOR,
            CosmeticType.PARTICLE
        );
        registerAchievement(particleMaster);
        
        Achievement hatCollector = new Achievement(
            "hat_collector",
            "Hat Collector",
            "Own all hat cosmetics",
            Material.DIAMOND_HELMET,
            Arrays.asList("Collect every hat cosmetic"),
            400,
            Achievement.AchievementType.TYPE_COLLECTOR,
            CosmeticType.HAT
        );
        registerAchievement(hatCollector);
        
        Achievement petLover = new Achievement(
            "pet_lover",
            "Pet Lover",
            "Own all pet cosmetics",
            Material.BONE,
            Arrays.asList("Collect every pet companion"),
            500,
            Achievement.AchievementType.TYPE_COLLECTOR,
            CosmeticType.PET
        );
        registerAchievement(petLover);
        
        Achievement trailMaster = new Achievement(
            "trail_master",
            "Trail Master",
            "Own all trail cosmetics",
            Material.FEATHER,
            Arrays.asList("Collect every trail effect"),
            350,
            Achievement.AchievementType.TYPE_COLLECTOR,
            CosmeticType.TRAIL
        );
        registerAchievement(trailMaster);
        
        Achievement gadgeteer = new Achievement(
            "gadgeteer",
            "Gadgeteer",
            "Own all gadget cosmetics",
            Material.STICK,
            Arrays.asList("Collect every gadget"),
            300,
            Achievement.AchievementType.TYPE_COLLECTOR,
            CosmeticType.GADGET
        );
        registerAchievement(gadgeteer);
        
        Achievement angelicBeing = new Achievement(
            "angelic_being",
            "Angelic Being",
            "Own all wing cosmetics",
            Material.ELYTRA,
            Arrays.asList("Collect every wing type"),
            600,
            Achievement.AchievementType.TYPE_COLLECTOR,
            CosmeticType.WINGS
        );
        registerAchievement(angelicBeing);
        
        Achievement auramaster = new Achievement(
            "aura_master",
            "Aura Master",
            "Own all aura cosmetics",
            Material.NETHER_STAR,
            Arrays.asList("Collect every mystical aura"),
            700,
            Achievement.AchievementType.TYPE_COLLECTOR,
            CosmeticType.AURA
        );
        registerAchievement(auramaster);
        
        // Status achievements
        Achievement vipMember = new Achievement(
            "vip_member",
            "VIP Member",
            "Become a VIP player",
            Material.GOLD_INGOT,
            Arrays.asList("Obtain VIP status"),
            500,
            Achievement.AchievementType.VIP_USER,
            null
        );
        registerAchievement(vipMember);
        
        Achievement premiumMember = new Achievement(
            "premium_member",
            "Premium Member",
            "Become a Premium player",
            Material.DIAMOND,
            Arrays.asList("Obtain Premium status"),
            1000,
            Achievement.AchievementType.PREMIUM_USER,
            null
        );
        registerAchievement(premiumMember);
        
        plugin.getLogger().info("Registered " + achievements.size() + " achievements");
    }
    
    private void registerAchievement(Achievement achievement) {
        achievements.put(achievement.getId(), achievement);
    }
    
    /**
     * Check if a player has completed an achievement
     */
    public boolean hasAchievement(Player player, String achievementId) {
        Set<String> playerAchs = playerAchievements.get(player.getUniqueId());
        return playerAchs != null && playerAchs.contains(achievementId);
    }
    
    /**
     * Award an achievement to a player
     */
    public void awardAchievement(Player player, String achievementId) {
        if (hasAchievement(player, achievementId)) {
            return; // Already has this achievement
        }
        
        Achievement achievement = achievements.get(achievementId);
        if (achievement == null) {
            return;
        }
        
        // Add to player's achievements
        playerAchievements.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(achievementId);
        
        // Give credit reward
        if (achievement.getCreditReward() > 0) {
            plugin.getCreditManager().addCredits(player.getUniqueId(), achievement.getCreditReward());
        }
        
        // Record statistics
        if (plugin.getStatisticsManager() != null) {
            plugin.getStatisticsManager().recordAchievementUnlock(player, achievementId);
        }
        
        // Notify player
        plugin.getMessageManager().sendSuccess(player, "§6§l🏆 Achievement Unlocked!");
        plugin.getMessageManager().sendSuccess(player, "§e" + achievement.getName() + " §7- §f" + achievement.getDescription());
        if (achievement.getCreditReward() > 0) {
            plugin.getMessageManager().sendSuccess(player, "§7Reward: §e+" + achievement.getCreditReward() + " credits");
        }
        
        // Play sound effect
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
    }
    
    /**
     * Check and award any newly completed achievements for a player
     */
    public void checkAchievements(Player player) {
        for (Achievement achievement : achievements.values()) {
            if (!hasAchievement(player, achievement.getId()) && achievement.isCompleted(player, plugin)) {
                awardAchievement(player, achievement.getId());
            }
        }
    }
    
    /**
     * Get all achievements
     */
    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }
    
    /**
     * Get player's completed achievements
     */
    public Set<String> getPlayerAchievements(Player player) {
        return new HashSet<>(playerAchievements.getOrDefault(player.getUniqueId(), new HashSet<>()));
    }
    
    /**
     * Get completion percentage for a player
     */
    public double getCompletionPercentage(Player player) {
        Set<String> completed = getPlayerAchievements(player);
        return (double) completed.size() / achievements.size() * 100.0;
    }
    
    /**
     * Get total credit rewards earned from achievements
     */
    public int getTotalCreditsEarned(Player player) {
        Set<String> completed = getPlayerAchievements(player);
        int total = 0;
        for (String achievementId : completed) {
            Achievement achievement = achievements.get(achievementId);
            if (achievement != null) {
                total += achievement.getCreditReward();
            }
        }
        return total;
    }
}