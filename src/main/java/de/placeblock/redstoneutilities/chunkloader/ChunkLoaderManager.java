package de.placeblock.redstoneutilities.chunkloader;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class ChunkLoaderManager implements BlockEntityManager {
    private ChunkLoaderListener chunkLoaderListener;

    public void setup(RedstoneUtilities plugin) {
        this.chunkLoaderListener = new ChunkLoaderListener(plugin);
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.chunkLoaderListener, plugin);

        new ChunkLoaderRecipe().register();

        plugin.getBlockEntityTypeRegistry().register(new ChunkLoaderBlockEntityType(plugin));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.chunkLoaderListener);
    }

}
