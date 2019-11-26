package me.poma123.automessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Automessage extends JavaPlugin implements TabCompleter {

    BukkitTask task;

    int sd = 0;// = 10;
    int seconds = 0;
    String prefix;
    List<String> messages;
    int count = 0;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        saveConfig();

        getCommand("automessage").setTabCompleter(this);

        prefix = getConfig().getString("prefix");
        messages = getConfig().getStringList("messages");
        seconds = getConfig().getInt("delay")*20;


        scheduleTask(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        task.cancel();
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label,
                                      String[] args) {
        // TODO Auto-generated method stub
        if (cmd.getName().equalsIgnoreCase("automessage")) {
            if (sender.hasPermission("automessage.reload")) {

                if (args.length == 1) {
                    ArrayList<String> names = new ArrayList<String>();
                    List<String> list = new ArrayList<>();


                    list = Arrays.asList("reload");
                    if (!args[0].equals("")) {
                        for (String name : list) {
                            if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                                names.add(name);
                            }

                        }
                    } else {
                        for (String name : list) {
                            names.add(name);
                        }
                    }

                    Collections.sort(names);

                    return names;
                }


            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("automessage")) {
            if (sender.hasPermission("automessage.reload")) {
                if (args.length < 1) {
                    sender.sendMessage("§eAutoMessage help:");
                    sender.sendMessage("§7/automessage §breload §f- Reload the configuration");
                } else {
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            task.cancel();
                            reloadConfig();
                            sender.sendMessage("§aConfiguration reloaded!");

                            seconds = getConfig().getInt("delay")*20;
                            prefix = getConfig().getString("prefix");
                            messages = getConfig().getStringList("messages");

                            scheduleTask(this);
                        } else {
                            sender.sendMessage("§eAutoMessage help:");
                            sender.sendMessage("§7/automessage §breload §f- Reload the configuration");
                        }
                    }
                }
            } else {
                sender.sendMessage("§cYou do not have permission to perform this command!");
            }
        }
        return true;
    }

    public void scheduleTask(Plugin plugin) {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (messages.size() > 0) {
                    if (count < messages.size()) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + " " + ChatColor.translateAlternateColorCodes('&', messages.get(count)).replace("%newline%", System.lineSeparator()));

                        }
                        count = count + 1;
                    } else {
                        count = 0;
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + " " + ChatColor.translateAlternateColorCodes('&', messages.get(count)).replace("%newline%", System.lineSeparator()));
                        }
                        count = count + 1;
                    }
                } else {
                    getLogger().warning("No messages found in the configuration.");
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, (long)seconds);
    }
}
