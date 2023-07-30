package de.placeblock.redstoneutilities.chunkloader;

import de.placeblock.redstoneutilities.BlockEntityManager;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class ChunkLoaderManager implements BlockEntityManager {
    private ChunkLoaderListener chunkLoaderListener;
    private BukkitTask chunkTicker;

    public void setup(RedstoneUtilities plugin) {
        this.chunkLoaderListener = new ChunkLoaderListener(plugin);
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this.chunkLoaderListener, plugin);

        new ChunkLoaderRecipe().register();

        plugin.getBlockEntityTypeRegistry().register(new ChunkLoaderBlockEntityType(plugin));

        BlockEntityRegistry blockEntityRegistry = plugin.getBlockEntityRegistry();
        this.chunkTicker = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<ChunkLoaderBlockEntity> blockEntities = blockEntityRegistry.get(ChunkLoaderBlockEntity.class);
            for (ChunkLoaderBlockEntity blockEntity : blockEntities) {
                blockEntity.tickChunks();
            }
        }, 0, 1);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this.chunkLoaderListener);
        this.chunkTicker.cancel();
    }

}
