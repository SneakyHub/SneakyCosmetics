# SneakyCosmetics Placeholders

This document lists all available PlaceholderAPI placeholders provided by SneakyCosmetics.

## Basic Placeholders

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_credits%` | Player's current credit balance | `1500` |
| `%sneakycosmetics_total_owned%` | Total cosmetics owned by player | `23` |
| `%sneakycosmetics_total_active%` | Total active cosmetics | `4` |
| `%sneakycosmetics_has_free_access%` | Whether player has free access | `true/false` |

## Achievement Placeholders

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_achievement_progress%` | Achievement completion percentage | `67.5` |
| `%sneakycosmetics_achievement_credits%` | Credits earned from achievements | `2500` |

## Player Statistics

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_stats_cosmetics_activated%` | Total cosmetics activated | `156` |
| `%sneakycosmetics_stats_achievements_unlocked%` | Total achievements unlocked | `8` |
| `%sneakycosmetics_stats_credits_earned%` | Total credits earned | `5420` |
| `%sneakycosmetics_stats_credits_spent%` | Total credits spent | `3200` |
| `%sneakycosmetics_stats_total_usage_time%` | Total cosmetic usage time | `2h 45m 30s` |
| `%sneakycosmetics_stats_favorite_cosmetic%` | Most used cosmetic | `rainbow_particles` |
| `%sneakycosmetics_stats_favorite_type%` | Most used cosmetic type | `Particles` |
| `%sneakycosmetics_stats_last_cosmetic%` | Last cosmetic activated | `heart_particles` |
| `%sneakycosmetics_stats_last_achievement%` | Last achievement unlocked | `collector_5` |
| `%sneakycosmetics_stats_last_purchase%` | Last item purchased | `diamond_helm` |

## Cosmetic Type Statistics

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_owned_<type>%` | Owned cosmetics of specific type | `%sneakycosmetics_owned_particle%` → `12` |
| `%sneakycosmetics_active_<type>%` | Active cosmetic of specific type | `%sneakycosmetics_active_particle%` → `rainbow_particles` |
| `%sneakycosmetics_type_usage_<type>%` | Times type was activated | `%sneakycosmetics_type_usage_PARTICLE%` → `45` |
| `%sneakycosmetics_type_time_<type>%` | Time using specific type | `%sneakycosmetics_type_time_PARTICLE%` → `1h 23m` |

Available types: `PARTICLE`, `HAT`, `PET`, `TRAIL`, `GADGET`, `WINGS`, `AURA`

## Individual Cosmetic Statistics

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_has_<cosmetic_id>%` | Whether player owns cosmetic | `%sneakycosmetics_has_rainbow%` → `true` |
| `%sneakycosmetics_using_<cosmetic_id>%` | Whether cosmetic is active | `%sneakycosmetics_using_rainbow%` → `false` |
| `%sneakycosmetics_usage_count_<cosmetic_id>%` | Times cosmetic was activated | `%sneakycosmetics_usage_count_rainbow%` → `23` |
| `%sneakycosmetics_usage_time_<cosmetic_id>%` | Time using cosmetic | `%sneakycosmetics_usage_time_rainbow%` → `45m 12s` |

## Credit Source Statistics

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_credit_source_<source>%` | Credits from specific source | `%sneakycosmetics_credit_source_daily%` → `350` |

Common sources: `daily`, `playtime`, `achievement`, `purchase`, `welcome`, `admin`

## Server-Wide Statistics

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_server_total_credits_earned%` | Total credits earned server-wide | `1250000` |
| `%sneakycosmetics_server_total_credits_spent%` | Total credits spent server-wide | `980000` |
| `%sneakycosmetics_server_total_cosmetics_activated%` | Total activations server-wide | `45620` |
| `%sneakycosmetics_server_total_achievements_unlocked%` | Total achievements unlocked | `892` |
| `%sneakycosmetics_server_most_popular_cosmetic%` | Most popular cosmetic | `rainbow_particles` |
| `%sneakycosmetics_server_most_used_type%` | Most used cosmetic type | `PARTICLE` |

## Leaderboard Placeholders

### Top Players by Statistic
| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_top_player_<stat>_<position>%` | Player name at position | `%sneakycosmetics_top_player_credits_earned_1%` → `PlayerName` |
| `%sneakycosmetics_top_value_<stat>_<position>%` | Value at position | `%sneakycosmetics_top_value_credits_earned_1%` → `15420` |

Available stats:
- `cosmetics_activated` - Most cosmetics activated
- `achievements_unlocked` - Most achievements unlocked  
- `credits_earned` - Most credits earned
- `credits_spent` - Most credits spent
- `total_usage_time` - Most time using cosmetics

Position can be 1-10 for top 10 players.

### Examples:
- `%sneakycosmetics_top_player_cosmetics_activated_1%` - Top cosmetic user's name
- `%sneakycosmetics_top_value_cosmetics_activated_1%` - Top cosmetic user's count
- `%sneakycosmetics_top_player_credits_earned_3%` - 3rd top credit earner's name
- `%sneakycosmetics_top_value_credits_earned_3%` - 3rd top credit earner's amount

## Pet Placeholders

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_pet_name_<pet_id>%` | Custom pet name or default | `%sneakycosmetics_pet_name_dog%` → `Fluffy` |

## CMI Integration Placeholders

*Available only when CMI is installed and enabled*

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%sneakycosmetics_cmi_balance%` | CMI economy balance | `15420.50` |
| `%sneakycosmetics_cmi_nickname%` | CMI nickname or username | `&c[VIP] PlayerName` |
| `%sneakycosmetics_cmi_playtime%` | CMI playtime formatted | `5d 12h 30m` |
| `%sneakycosmetics_cmi_vanished%` | Whether player is vanished | `true/false` |
| `%sneakycosmetics_cmi_afk%` | Whether player is AFK | `true/false` |
| `%sneakycosmetics_cmi_god%` | Whether player is in god mode | `true/false` |

## Usage Examples

### In Chat Plugins
```
[%sneakycosmetics_cmi_nickname%] &7has &e%sneakycosmetics_credits% &7credits and &a%sneakycosmetics_total_owned% &7cosmetics!
```

### In Scoreboard/TAB
```
&6Credits: &e%sneakycosmetics_credits%
&bCosmetics: &f%sneakycosmetics_total_owned%&7/&f%sneakycosmetics_total_available%
&aAchievements: &f%sneakycosmetics_achievement_progress%&7%
```

### In Signs/Holograms
```
&6&lTOP CREDIT EARNERS
&e1. &f%sneakycosmetics_top_player_credits_earned_1% &7- &e%sneakycosmetics_top_value_credits_earned_1%
&e2. &f%sneakycosmetics_top_player_credits_earned_2% &7- &e%sneakycosmetics_top_value_credits_earned_2%
&e3. &f%sneakycosmetics_top_player_credits_earned_3% &7- &e%sneakycosmetics_top_value_credits_earned_3%
```

### In GUIs/Menus
```
&7Your Stats:
&7• Credits Earned: &e%sneakycosmetics_stats_credits_earned%
&7• Favorite Cosmetic: &a%sneakycosmetics_stats_favorite_cosmetic%
&7• Usage Time: &b%sneakycosmetics_stats_total_usage_time%
```

## Notes

- All placeholders return empty string if plugin is not loaded
- Statistics are updated in real-time as players use cosmetics
- Leaderboards are cached and updated every few minutes for performance
- CMI placeholders only work when CMI plugin is installed and active
- Time values are automatically formatted (e.g., "2h 45m 30s")
- Boolean values always return "true" or "false" as strings