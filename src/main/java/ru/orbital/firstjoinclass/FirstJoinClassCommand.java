package ru.orbital.firstjoinclass;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FirstJoinClassCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = List.of("reload", "reset", "run");

    private final FirstJoinClassPlugin plugin;

    public FirstJoinClassCommand(FirstJoinClassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                plugin.reloadPlugin();
                sender.sendMessage(Component.text("FirstJoinClass reloaded. Command: /"
                        + plugin.config().command(), NamedTextColor.GREEN));
            }
            case "reset" -> reset(sender, label, args);
            case "run" -> run(sender, label, args);
            default -> sendUsage(sender, label);
        }
        return true;
    }

    private void reset(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /" + label + " reset <player>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player must be online to reset: " + args[1],
                    NamedTextColor.RED));
            return;
        }

        plugin.clearPrompted(target);
        sender.sendMessage(Component.text("Reset the class prompt for " + target.getName()
                + ". It will run on their next join.", NamedTextColor.GREEN));
    }

    private void run(CommandSender sender, String label, String[] args) {
        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(Component.text("Player is not online: " + args[1], NamedTextColor.RED));
                return;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage(Component.text("Usage: /" + label + " run <player>", NamedTextColor.RED));
            return;
        }

        plugin.runNow(target);
        sender.sendMessage(Component.text("Ran /" + plugin.config().command()
                + " for " + target.getName() + ".", NamedTextColor.GREEN));
    }

    private static void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(Component.text("Usage:", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/" + label + " reload", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/" + label + " reset <player>", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/" + label + " run [player]", NamedTextColor.YELLOW));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                               @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return SUBCOMMANDS.stream().filter(entry -> entry.startsWith(prefix)).toList();
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("reload")) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    names.add(player.getName());
                }
            }
            return names;
        }

        return List.of();
    }
}
