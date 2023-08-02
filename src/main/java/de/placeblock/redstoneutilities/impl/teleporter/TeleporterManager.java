package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class TeleporterManager implements BlockEntityManager {
    public static final String TELEPORTER_NAME = "teleporter";
    private TeleporterListener teleporterListener;

    @Override
    public void setup(RedstoneUtilities plugin) {
        this.teleporterListener = new TeleporterListener();
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.teleporterListener, plugin);

        new TeleporterRecipe().register();

        plugin.getBlockEntityTypeRegistry().register(new TeleporterBlockEntityType(plugin));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.teleporterListener);
    }
}
