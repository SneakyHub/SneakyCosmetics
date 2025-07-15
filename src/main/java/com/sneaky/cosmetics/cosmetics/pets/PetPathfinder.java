package com.sneaky.cosmetics.cosmetics.pets;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Advanced pathfinding system for pets to follow players smoothly
 * Handles ground-based movement, obstacle avoidance, and intelligent navigation
 */
public class PetPathfinder {

    // Constants for pathfinding behavior
    private static final double MAX_FOLLOW_DISTANCE = 15.0;
    private static final double MIN_FOLLOW_DISTANCE = 3.0;
    private static final double TELEPORT_DISTANCE = 25.0;
    private static final double STEP_HEIGHT = 1.0;
    private static final double JUMP_HEIGHT = 2.0;
    private static final int MAX_PATHFIND_ATTEMPTS = 10;

    /**
     * Calculate the next movement step for a pet following a player
     */
    public static MovementResult calculateMovement(Entity pet, Player player, double speedMultiplier) {
        Location petLoc = pet.getLocation();
        Location playerLoc = player.getLocation();
        double distance = petLoc.distance(playerLoc);

        // If pet is too far, teleport it
        if (distance > TELEPORT_DISTANCE) {
            Location teleportLoc = findSafeTeleportLocation(playerLoc);
            return new MovementResult(MovementType.TELEPORT, teleportLoc);
        }

        // If pet is close enough, don't move
        if (distance <= MIN_FOLLOW_DISTANCE) {
            return new MovementResult(MovementType.IDLE, null);
        }

        // Calculate pathfinding movement
        Location targetLoc = findBestFollowPosition(playerLoc, petLoc);
        Location nextStep = calculateNextStep(petLoc, targetLoc, speedMultiplier);

        if (nextStep == null) {
            // Pathfinding failed, try teleportation
            if (distance > MAX_FOLLOW_DISTANCE) {
                Location teleportLoc = findSafeTeleportLocation(playerLoc);
                return new MovementResult(MovementType.TELEPORT, teleportLoc);
            }
            return new MovementResult(MovementType.IDLE, null);
        }

        return new MovementResult(MovementType.WALK, nextStep);
    }

    /**
     * Find the best position for the pet to follow the player
     */
    private static Location findBestFollowPosition(Location playerLoc, Location petLoc) {
        // If player is flying or too high up, keep pet on ground
        Location groundPlayerLoc = playerLoc.clone();
        if (isPlayerFlying(playerLoc) || playerLoc.getY() - findGroundLevel(playerLoc).getY() > 3) {
            groundPlayerLoc = findGroundLevel(playerLoc);
            if (groundPlayerLoc == null) {
                groundPlayerLoc = playerLoc.clone();
                groundPlayerLoc.setY(playerLoc.getY() - 1);
            }
        }

        // Try to position pet behind and slightly to the side of player
        Vector playerDirection = groundPlayerLoc.getDirection().normalize();
        Vector followOffset = playerDirection.multiply(-2.5); // Behind player

        // Add some randomness to avoid pets clustering
        followOffset.add(new Vector(
                (Math.random() - 0.5) * 2,
                0,
                (Math.random() - 0.5) * 2
        ));

        Location targetLoc = groundPlayerLoc.clone().add(followOffset);

        // Ensure the target location is safe and on ground
        targetLoc = findGroundLevel(targetLoc);
        if (targetLoc != null && isSafeLocation(targetLoc)) {
            return targetLoc;
        }

        // If preferred position isn't safe, find nearest safe position
        return findNearestSafeLocation(groundPlayerLoc, 5);
    }

    /**
     * Check if the player is flying or in the air
     */
    private static boolean isPlayerFlying(Location playerLoc) {
        if (playerLoc.getWorld() == null) return false;

        // Check if player is more than 2 blocks above ground
        Location groundLoc = findGroundLevel(playerLoc);
        if (groundLoc != null) {
            return playerLoc.getY() - groundLoc.getY() > 2.0;
        }

        return false;
    }

    /**
     * Calculate the next step in pathfinding
     */
    private static Location calculateNextStep(Location current, Location target, double speedMultiplier) {
        Vector direction = target.toVector().subtract(current.toVector());
        double distance = direction.length();

        if (distance < 0.1) {
            return null; // Already at target
        }

        // Normalize direction and apply speed
        direction.normalize();
        double speed = 0.4 * speedMultiplier;
        direction.multiply(Math.min(speed, distance));

        Location nextStep = current.clone().add(direction);

        // Check if the next step is valid
        if (canMoveTo(current, nextStep)) {
            return nextStep;
        }

        // Try alternative paths if direct path is blocked
        return findAlternativePath(current, target, speedMultiplier);
    }

    /**
     * Find an alternative path when direct movement is blocked
     */
    private static Location findAlternativePath(Location current, Location target, double speedMultiplier) {
        Vector baseDirection = target.toVector().subtract(current.toVector()).normalize();
        double speed = 0.4 * speedMultiplier;

        // Try different angles to avoid obstacles
        double[] angles = {0, 45, -45, 90, -90, 135, -135, 180};

        for (double angle : angles) {
            Vector direction = rotateVector(baseDirection, Math.toRadians(angle));
            direction.multiply(speed);

            Location testLocation = current.clone().add(direction);

            if (canMoveTo(current, testLocation)) {
                return testLocation;
            }
        }

        // Try jumping if pet is stuck
        Location jumpLocation = current.clone().add(0, STEP_HEIGHT, 0);
        if (canMoveTo(current, jumpLocation)) {
            return jumpLocation;
        }

        return null; // No valid path found
    }

    /**
     * Check if the pet can move from one location to another
     */
    private static boolean canMoveTo(Location from, Location to) {
        // Check if target location is safe
        if (!isSafeLocation(to)) {
            return false;
        }

        // Check for obstacles in the path
        Vector direction = to.toVector().subtract(from.toVector());
        double distance = direction.length();
        direction.normalize();

        // Sample points along the path
        int samples = Math.max(1, (int) (distance * 2));
        for (int i = 0; i <= samples; i++) {
            Vector step = direction.clone().multiply((distance / samples) * i);
            Location checkLoc = from.clone().add(step);

            if (!isPassable(checkLoc)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if a location is safe for the pet
     */
    private static boolean isSafeLocation(Location loc) {
        if (loc.getWorld() == null) return false;

        Block block = loc.getBlock();
        Block below = loc.clone().add(0, -1, 0).getBlock();
        Block above = loc.clone().add(0, 1, 0).getBlock();

        // Check if there's solid ground below
        if (!below.getType().isSolid()) {
            // Check if there's a block within 3 blocks below
            boolean foundGround = false;
            for (int i = 2; i <= 4; i++) {
                Block checkBelow = loc.clone().add(0, -i, 0).getBlock();
                if (checkBelow.getType().isSolid()) {
                    foundGround = true;
                    break;
                }
            }
            if (!foundGround) return false;
        }

        // Check if the location is passable
        return isPassable(loc) && isPassable(above.getLocation());
    }

    /**
     * Check if a location is passable (not solid)
     */
    private static boolean isPassable(Location loc) {
        Block block = loc.getBlock();
        Material type = block.getType();

        // Check for solid blocks that pets can't pass through
        if (type.isSolid()) {
            // Allow some passable solid blocks
            switch (type) {
                case TALL_GRASS:
                case SHORT_GRASS:
                case SNOW:
                case VINE:
                case LADDER:
                    return true;
                default:
                    return false;
            }
        }

        // Check for dangerous blocks
        switch (type) {
            case LAVA:
            case FIRE:
            case CACTUS:
            case SWEET_BERRY_BUSH:
            case WITHER_ROSE:
                return false;
            default:
                return true;
        }
    }

    /**
     * Find the nearest safe location within a radius
     */
    private static Location findNearestSafeLocation(Location center, int radius) {
        List<Location> candidates = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 2; y++) {
                    Location candidate = center.clone().add(x, y, z);
                    if (isSafeLocation(candidate)) {
                        candidates.add(candidate);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return center; // Fallback to original location
        }

        // Sort by distance to center
        candidates.sort((a, b) -> Double.compare(
                a.distanceSquared(center),
                b.distanceSquared(center)
        ));

        return candidates.get(0);
    }

    /**
     * Find a safe teleport location near the player
     */
    private static Location findSafeTeleportLocation(Location playerLoc) {
        // Try positions around the player
        for (int attempts = 0; attempts < MAX_PATHFIND_ATTEMPTS; attempts++) {
            double angle = Math.random() * Math.PI * 2;
            double distance = 2 + Math.random() * 3; // 2-5 blocks away

            double x = playerLoc.getX() + Math.cos(angle) * distance;
            double z = playerLoc.getZ() + Math.sin(angle) * distance;

            Location candidate = new Location(playerLoc.getWorld(), x, playerLoc.getY(), z);

            // Find ground level
            candidate = findGroundLevel(candidate);

            if (candidate != null && isSafeLocation(candidate)) {
                return candidate;
            }
        }

        // Fallback: teleport directly to player location
        return findGroundLevel(playerLoc.clone().add(1, 0, 1));
    }

    /**
     * Find the ground level at a given X,Z coordinate
     */
    private static Location findGroundLevel(Location loc) {
        if (loc.getWorld() == null) return null;

        // Start from a high Y level and work down
        int startY = Math.max((int) loc.getY() + 10, loc.getWorld().getMaxHeight() - 1);

        for (int y = startY; y >= loc.getWorld().getMinHeight(); y--) {
            Location checkLoc = new Location(loc.getWorld(), loc.getX(), y, loc.getZ());
            Block block = checkLoc.getBlock();
            Block above = checkLoc.clone().add(0, 1, 0).getBlock();
            Block above2 = checkLoc.clone().add(0, 2, 0).getBlock();

            if (block.getType().isSolid() &&
                    isPassable(above.getLocation()) &&
                    isPassable(above2.getLocation())) {
                return above.getLocation();
            }
        }

        return loc; // Fallback
    }

    /**
     * Rotate a vector by a given angle around the Y axis
     */
    private static Vector rotateVector(Vector vector, double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);

        double x = vector.getX() * cos - vector.getZ() * sin;
        double z = vector.getX() * sin + vector.getZ() * cos;

        return new Vector(x, vector.getY(), z);
    }

    /**
     * Check if the pet should jump to reach the target
     */
    public static boolean shouldJump(Location current, Location target) {
        double yDiff = target.getY() - current.getY();
        return yDiff > 0.5 && yDiff <= JUMP_HEIGHT;
    }

    /**
     * Apply jump movement to the pet
     */
    public static void applyJump(LivingEntity pet) {
        Vector velocity = pet.getVelocity();
        velocity.setY(0.5); // Jump velocity
        pet.setVelocity(velocity);
    }

    /**
     * Result of movement calculation
     */
    public static class MovementResult {
        private final MovementType type;
        private final Location location;

        public MovementResult(MovementType type, Location location) {
            this.type = type;
            this.location = location;
        }

        public MovementType getType() {
            return type;
        }

        public Location getLocation() {
            return location;
        }
    }

    /**
     * Types of movement the pet can perform
     */
    public enum MovementType {
        WALK,       // Normal walking movement
        TELEPORT,   // Teleport to location
        JUMP,       // Jump movement
        IDLE        // Stay in place
    }
}