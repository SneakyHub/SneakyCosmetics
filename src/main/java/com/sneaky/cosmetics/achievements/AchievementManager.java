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
        
        // Credit achievements
        Achievement creditEarner = new Achievement(
            "credit_earner",
            "Credit Earner",
            "Earn your first 1,000 credits",
            Material.GOLD_NUGGET,
            Arrays.asList("Accumulate 1,000 credits total"),
            150,
            Achievement.AchievementType.CREDITS_EARNED,
            1000
        );
        registerAchievement(creditEarner);
        
        Achievement bigSpender = new Achievement(
            "big_spender",
            "Big Spender",
            "Spend 2,500 credits on cosmetics",
            Material.EMERALD,
            Arrays.asList("Spend 2,500 credits total"),
            300,
            Achievement.AchievementType.CREDITS_SPENT,
            2500
        );
        registerAchievement(bigSpender);
        
        Achievement creditMillionaire = new Achievement(
            "credit_millionaire",
            "Credit Millionaire",
            "Earn 10,000 credits total",
            Material.DIAMOND,
            Arrays.asList("Accumulate 10,000 credits total"),
            750,
            Achievement.AchievementType.CREDITS_EARNED,
            10000
        );
        registerAchievement(creditMillionaire);
        
        // Daily login achievements
        Achievement dailyStreak7 = new Achievement(
            "daily_streak_7",
            "Weekly Warrior",
            "Log in for 7 consecutive days",
            Material.CLOCK,
            Arrays.asList("Claim daily rewards for 7 days straight"),
            200,
            Achievement.AchievementType.DAILY_STREAK,
            7
        );
        registerAchievement(dailyStreak7);
        
        Achievement dailyStreak30 = new Achievement(
            "daily_streak_30",
            "Monthly Master",
            "Log in for 30 consecutive days",
            Material.GOLDEN_APPLE,
            Arrays.asList("Claim daily rewards for 30 days straight"),
            1000,
            Achievement.AchievementType.DAILY_STREAK,
            30
        );
        registerAchievement(dailyStreak30);
        
        Achievement dailyClaimer = new Achievement(
            "daily_claimer",
            "Daily Claimer",
            "Claim 50 daily rewards total",
            Material.CHEST,
            Arrays.asList("Claim your daily reward 50 times"),
            400,
            Achievement.AchievementType.DAILY_CLAIMS,
            50
        );
        registerAchievement(dailyClaimer);
        
        // Usage achievements
        Achievement cosmeticActivator = new Achievement(
            "cosmetic_activator",
            "Cosmetic Activator",
            "Activate cosmetics 100 times",
            Material.REDSTONE,
            Arrays.asList("Toggle cosmetics on/off 100 times"),
            250,
            Achievement.AchievementType.COSMETICS_ACTIVATED,
            100
        );
        registerAchievement(cosmeticActivator);
        
        Achievement styleChanger = new Achievement(
            "style_changer",
            "Style Changer",
            "Activate cosmetics 500 times",
            Material.COMPARATOR,
            Arrays.asList("Toggle cosmetics on/off 500 times"),
            500,
            Achievement.AchievementType.COSMETICS_ACTIVATED,
            500
        );
        registerAchievement(styleChanger);
        
        // Pet specific achievements
        Achievement petCollector = new Achievement(
            "pet_collector_baby",
            "Baby Pet Collector",
            "Own 10 baby pet variants",
            Material.EGG,
            Arrays.asList("Collect 10 different baby pets"),
            300,
            Achievement.AchievementType.BABY_PETS,
            10
        );
        registerAchievement(petCollector);
        
        Achievement legendaryPetOwner = new Achievement(
            "legendary_pet_owner",
            "Legendary Pet Owner",
            "Own a legendary pet (Dragon, Wither, or Warden)",
            Material.DRAGON_EGG,
            Arrays.asList("Purchase any legendary tier pet"),
            600,
            Achievement.AchievementType.LEGENDARY_PET,
            null
        );
        registerAchievement(legendaryPetOwner);
        
        // Particle achievements
        Achievement particleShowoff = new Achievement(
            "particle_showoff",
            "Particle Showoff",
            "Own 15 different particle effects",
            Material.BLAZE_POWDER,
            Arrays.asList("Collect 15 particle cosmetics"),
            400,
            Achievement.AchievementType.PARTICLE_COUNT,
            15
        );
        registerAchievement(particleShowoff);
        
        // Social achievements
        Achievement trendsetter = new Achievement(
            "trendsetter",
            "Trendsetter",
            "Have 3 cosmetics active simultaneously",
            Material.BEACON,
            Arrays.asList("Activate 3 different cosmetics at once"),
            200,
            Achievement.AchievementType.SIMULTANEOUS_COSMETICS,
            3
        );
        registerAchievement(trendsetter);
        
        Achievement cosmicBeing = new Achievement(
            "cosmic_being",
            "Cosmic Being",
            "Have 5 cosmetics active simultaneously",
            Material.END_CRYSTAL,
            Arrays.asList("Activate 5 different cosmetics at once"),
            500,
            Achievement.AchievementType.SIMULTANEOUS_COSMETICS,
            5
        );
        registerAchievement(cosmicBeing);
        
        // Special achievements
        Achievement earlyAdopter = new Achievement(
            "early_adopter",
            "Early Adopter",
            "Be among the first 100 players to use cosmetics",
            Material.EXPERIENCE_BOTTLE,
            Arrays.asList("Join the cosmetics system early"),
            100,
            Achievement.AchievementType.EARLY_ADOPTER,
            null
        );
        registerAchievement(earlyAdopter);
        
        Achievement completionist = new Achievement(
            "completionist",
            "Completionist",
            "Own every single cosmetic in the plugin",
            Material.NETHERITE_INGOT,
            Arrays.asList("Collect ALL cosmetics available"),
            2500,
            Achievement.AchievementType.COMPLETIONIST,
            null
        );
        registerAchievement(completionist);
        
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
     * Check if an achievement is claimable (completed but not yet claimed)
     */
    public boolean isClaimable(Player player, String achievementId) {
        Achievement achievement = achievements.get(achievementId);
        return achievement != null && 
               !hasAchievement(player, achievementId) && 
               achievement.isCompleted(player, plugin);
    }
    
    /**
     * Get all claimable achievements for a player
     */
    public List<Achievement> getClaimableAchievements(Player player) {
        List<Achievement> claimable = new ArrayList<>();
        for (Achievement achievement : achievements.values()) {
            if (isClaimable(player, achievement.getId())) {
                claimable.add(achievement);
            }
        }
        return claimable;
    }
    
    /**
     * Manually claim an achievement (for GUI interaction)
     */
    public boolean claimAchievement(Player player, String achievementId) {
        if (isClaimable(player, achievementId)) {
            awardAchievement(player, achievementId);
            return true;
        }
        return false;
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