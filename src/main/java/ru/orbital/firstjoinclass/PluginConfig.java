package ru.orbital.firstjoinclass;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Immutable snapshot of config.yml.
 */
public record PluginConfig(
        boolean enabled,
        String command,
        long delayTicks,
        boolean onlyNewPlayers,
        boolean warnOnFailure
) {

    public static PluginConfig load(FileConfiguration config) {
        return new PluginConfig(
                config.getBoolean("enabled", true),
                config.getString("command", "class").trim(),
                Math.max(0L, config.getLong("delay-ticks", 20L)),
                config.getBoolean("only-new-players", true),
                config.getBoolean("warn-on-failure", true)
        );
    }

}
