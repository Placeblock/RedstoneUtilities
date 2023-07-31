package de.placeblock.redstoneutilities.impl.chunkloader;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

@RequiredArgsConstructor
public class ChunkLoaderListener implements Listener {
    private final RedstoneUtilities plugin;

    @EventHandler
    public void on(ChunkLoadEvent event) {
        Chunk loadedChunk = event.getChunk();
        BlockEntityRegistry blockEntityRegistry = this.plugin.getBlockEntityRegistry();
        for (Entity entity : loadedChunk.getEntities()) {
            if (!(entity instanceof Interaction interaction)) continue;
            ChunkLoaderBlockEntity blockEntity = blockEntityRegistry.get(interaction, ChunkLoaderBlockEntity.class);
            if (blockEntity == null) continue;

            blockEntity.setForceLoad();
        }
    }

}
