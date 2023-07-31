package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TeleporterBlockEntityType extends BlockEntityType<TeleporterBlockEntity, TeleporterBlockEntityType> {
    public TeleporterBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "teleporter", Items.CHUNKLOADER_ITEM, true, List.of());
    }

    @Override
    public TeleporterBlockEntity getBlockEntity(UUID uuid, Location location) {
        return new TeleporterBlockEntity(this, uuid, location);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        return true;
    }

    @Override
    public void disable() {

    }
}
