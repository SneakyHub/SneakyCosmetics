package com.sneaky.cosmetics.utils;

import com.sneaky.cosmetics.SneakyCosmetics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;

/**
 * Scheduler adapter that provides compatibility between Paper/Spigot and Folia
 * Automatically detects the server type and uses appropriate scheduling methods
 */
public class SchedulerAdapter {
    
    private final SneakyCosmetics plugin;
    private final boolean isFolia;
    
    public SchedulerAdapter(SneakyCosmetics plugin) {
        this.plugin = plugin;
        this.isFolia = detectFolia();
    }
    
    private boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public boolean isFolia() {
        return isFolia;
    }
    
    /**
     * Run a task on the main thread
     */
    public BukkitTask runTask(Runnable task) {
        if (isFolia) {
            // Use Folia's global region scheduler for non-location-specific tasks
            try {
                Bukkit.getGlobalRegionScheduler().execute(plugin, task);
                return null; // Folia doesn't return BukkitTask
            } catch (Exception e) {
                // Fallback to Bukkit scheduler
                return Bukkit.getScheduler().runTask(plugin, task);
            }
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }
    
    /**
     * Run a task asynchronously
     */
    public BukkitTask runTaskAsynchronously(Runnable task) {
        if (isFolia) {
            try {
                Bukkit.getAsyncScheduler().runNow(plugin, (scheduledTask) -> task.run());
                return null;
            } catch (Exception e) {
                return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            }
        } else {
            return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
    
    /**
     * Run a delayed task on the main thread
     */
    public BukkitTask runTaskLater(Runnable task, long delay) {
        if (isFolia) {
            try {
                Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delay);
                return null;
            } catch (Exception e) {
                return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
            }
        } else {
            return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }
    
    /**
     * Run a delayed task asynchronously
     */
    public BukkitTask runTaskLaterAsynchronously(Runnable task, long delay) {
        if (isFolia) {
            try {
                Bukkit.getAsyncScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delay * 50L, java.util.concurrent.TimeUnit.MILLISECONDS); // Convert ticks to milliseconds
                return null;
            } catch (Exception e) {
                return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
            }
        } else {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        }
    }
    
    /**
     * Run a repeating task on the main thread
     */
    public BukkitTask runTaskTimer(Runnable task, long delay, long period) {
        if (isFolia) {
            try {
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), delay, period);
                return null;
            } catch (Exception e) {
                return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
            }
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }
    
    /**
     * Run a repeating task asynchronously
     */
    public BukkitTask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        if (isFolia) {
            try {
                long delayMs = delay * 50L; // Convert ticks to milliseconds
                long periodMs = period * 50L;
                Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(), delayMs, periodMs, java.util.concurrent.TimeUnit.MILLISECONDS);
                return null;
            } catch (Exception e) {
                return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
            }
        } else {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        }
    }
    
    /**
     * Run a task at a specific location (Folia region-specific)
     */
    public BukkitTask runTaskAt(Location location, Runnable task) {
        if (isFolia) {
            try {
                Bukkit.getRegionScheduler().execute(plugin, location, task);
                return null;
            } catch (Exception e) {
                return Bukkit.getScheduler().runTask(plugin, task);
            }
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }
    
    /**
     * Run a task for a specific entity (Folia entity-specific)
     */
    public BukkitTask runTaskForEntity(Entity entity, Runnable task) {
        if (isFolia) {
            try {
                entity.getScheduler().execute(plugin, task, null, 1L);
                return null;
            } catch (Exception e) {
                return Bukkit.getScheduler().runTask(plugin, task);
            }
        } else {
            return Bukkit.getScheduler().runTask(plugin, task);
        }
    }
    
    /**
     * Execute a task and return a CompletableFuture
     */
    public CompletableFuture<Void> executeAsync(Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        runTaskAsynchronously(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    /**
     * Check if the current thread is the main server thread
     */
    public boolean isMainThread() {
        if (isFolia) {
            try {
                // In Folia, we need to check if we're in the right region
                return Bukkit.isPrimaryThread();
            } catch (Exception e) {
                return Bukkit.isPrimaryThread();
            }
        } else {
            return Bukkit.isPrimaryThread();
        }
    }
    
    /**
     * Ensure a task runs on the main thread
     */
    public void ensureMainThread(Runnable task) {
        if (isMainThread()) {
            task.run();
        } else {
            runTask(task);
        }
    }
    
    /**
     * Ensure a task runs on an async thread
     */
    public void ensureAsyncThread(Runnable task) {
        if (isMainThread()) {
            runTaskAsynchronously(task);
        } else {
            task.run();
        }
    }
}