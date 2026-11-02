package ru.orbital.firstjoinclass;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class FirstJoinClassPlugin extends JavaPlugin {

    private NamespacedKey promptedKey;
    private PluginConfig config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        promptedKey = new NamespacedKey(this, "class_prompted");
        config = PluginConfig.load(getConfig());

        getServer().getPluginManager().registerEvents(new FirstJoinListener(this), this);

        FirstJoinClassCommand command = new FirstJoinClassCommand(this);
        if (getCommand("firstjoinclass") != null) {
            getCommand("firstjoinclass").setExecutor(command);
            getCommand("firstjoinclass").setTabCompleter(command);
        }

        getLogger().info("Enabled. First-join command: /" + config.command()
                + " (" + (config.onlyNewPlayers() ? "new players only" : "once per player") + ")");
    }

    PluginConfig config() {
        return config;
    }

    void reloadPlugin() {
        reloadConfig();
        config = PluginConfig.load(getConfig());
    }

    boolean wasPrompted(OfflinePlayer player) {
        return player.getPersistentDataContainer().has(promptedKey, PersistentDataType.BYTE);
    }

    void markPrompted(Player player) {
        player.getPersistentDataContainer().set(promptedKey, PersistentDataType.BYTE, (byte) 1);
    }

    void clearPrompted(Player player) {
        player.getPersistentDataContainer().remove(promptedKey);
    }

    /**
     * Executes the configured command as the player.
     *
     * @return true if the command was handled successfully
     */
    boolean prompt(Player player) {
        String command = config.command();
        if (command.isBlank()) {
            return false;
        }

        boolean success;
        try {
            success = player.performCommand(stripSlash(command));
        } catch (RuntimeException exception) {
            getLogger().warning("Failed to run /" + command + " for " + player.getName()
                    + ": " + exception.getMessage());
            return false;
        }

        if (!success && config.warnOnFailure()) {
            getLogger().warning("Could not run /" + command + " for " + player.getName()
                    + " (unknown command or missing permission).");
        }
        return success;
    }

    void runNow(Player player) {
        boolean success = prompt(player);
        if (success) {
            markPrompted(player);
        }
    }

    void scheduleFirstJoinPrompt(Player player) {
        long delay = config.delayTicks();
        if (delay <= 0L) {
            runNow(player);
            return;
        }

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (player.isOnline()) {
                runNow(player);
            }
        }, delay);
    }

    private static String stripSlash(String command) {
        return command.startsWith("/") ? command.substring(1) : command;
    }
}
