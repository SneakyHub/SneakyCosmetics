package com.sneaky.cosmetics.managers;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.entity.Player;

public class PetManager {
    private final SneakyCosmetics plugin;
    
    public PetManager(SneakyCosmetics plugin) {
        this.plugin = plugin;
    }
    
    public void startParticleTask() {}
    public void startTrailTask() {}
    public void startPetTask() {}
    public void startWingTask() {}
    public void startAuraTask() {}
    public void stopAllTasks() {}
    public void stopPlayerEffects(Player player) {}
    public void stopPlayerTrails(Player player) {}
    public void removePet(Player player) {}
    public void stopPlayerWings(Player player) {}
    public void stopPlayerAuras(Player player) {}
}
