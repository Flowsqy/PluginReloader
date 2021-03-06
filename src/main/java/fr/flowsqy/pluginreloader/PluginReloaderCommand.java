package fr.flowsqy.pluginreloader;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginReloaderCommand implements TabExecutor {

    public PluginReloaderCommand(PluginReloaderPlugin plugin) {
        final PluginCommand command = plugin.getCommand("pluginreload");
        assert command != null;
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1){
            final String arg = args[0].toLowerCase(Locale.ROOT);
            if(arg.isEmpty())
                return false;
            final PluginManager pluginManager = Bukkit.getPluginManager();
            final Optional<Plugin> optionalPlugin = Stream.of(pluginManager.getPlugins())
                    .filter(plugin -> plugin.getName().toLowerCase(Locale.ROOT).startsWith(arg))
                    .findAny();
            if(!optionalPlugin.isPresent()){
                sender.sendMessage(args[0] + " is not a plugin or is not loaded");
                return true;
            }

            final Plugin plugin = optionalPlugin.get();
            if(!plugin.isEnabled()){
                sender.sendMessage(plugin.getName() + " is not loaded");
                return true;
            }

            final File pluginFile;
            try {
                pluginFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            } catch (URISyntaxException e) {
                sender.sendMessage("Can not get the plugin file name");
                return true;
            }
            pluginManager.disablePlugin(plugin);
            try {
                pluginManager.loadPlugin(pluginFile);
                sender.sendMessage("Successfully load the plugin");
                return true;
            } catch (InvalidPluginException | InvalidDescriptionException e) {
                sender.sendMessage("Can not load the plugin after his disabling");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1 && sender.hasPermission(PermissionManager.BASE_PERM)){
            final String arg = args[0].toLowerCase(Locale.ROOT);
            final Stream<String> stream = Stream.of(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName);
            if(arg.isEmpty())
                return stream.collect(Collectors.toList());
            return stream.filter(name -> name.toLowerCase(Locale.ROOT).startsWith(arg)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
