package ru.orbital.firstjoinclass;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class FirstJoinListener implements Listener {

    private final FirstJoinClassPlugin plugin;

    public FirstJoinListener(FirstJoinClassPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PluginConfig config = plugin.config();
        if (!config.enabled() || config.command().isBlank()) {
            return;
        }

        if (config.onlyNewPlayers() && event.getPlayer().hasPlayedBefore()) {
            return;
        }

        if (plugin.wasPrompted(event.getPlayer())) {
            return;
        }

        plugin.scheduleFirstJoinPrompt(event.getPlayer());
    }
}
