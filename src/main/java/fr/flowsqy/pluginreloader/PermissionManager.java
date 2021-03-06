package fr.flowsqy.pluginreloader;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.permissions.Permission;

public class PermissionManager implements Listener {

    public final static String BASE_PERM = "pluginreloader.command";
    public final static String GLOBAL_PERM = BASE_PERM + ".*";

    public PermissionManager(PluginReloaderPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event){
        registerPermission(event.getPlugin().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event){
        Bukkit.getPluginManager().removePermission(BASE_PERM+"."+event.getPlugin().getName());
    }

    private void registerPermission(String plugin){
        System.out.println(plugin);
        final Permission permission = new Permission(BASE_PERM+"."+plugin);
        permission.addParent(GLOBAL_PERM, true);
        permission.getChildren().put(BASE_PERM, true);
        permission.recalculatePermissibles();
        Bukkit.getPluginManager().addPermission(permission);
    }

}
