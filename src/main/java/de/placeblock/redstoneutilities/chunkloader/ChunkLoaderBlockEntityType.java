package de.placeblock.redstoneutilities.chunkloader;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ChunkLoaderBlockEntityType extends BlockEntityType<ChunkLoaderBlockEntity, ChunkLoaderBlockEntityType> {
    public ChunkLoaderBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "chunkloader", Items.CHUNKLOADER_ITEM, true, List.of());
    }

    @Override
    public ChunkLoaderBlockEntity getBlockEntity(UUID uuid) {
        return new ChunkLoaderBlockEntity(this, uuid);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        return true;
    }

    @Override
    public void disable() {

    }
}
