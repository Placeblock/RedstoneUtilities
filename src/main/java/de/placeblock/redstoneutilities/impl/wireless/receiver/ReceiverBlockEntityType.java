package de.placeblock.redstoneutilities.impl.wireless.receiver;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.impl.wireless.WirelessBlockEntityType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReceiverBlockEntityType extends WirelessBlockEntityType<ReceiverBlockEntity, ReceiverBlockEntityType> {
    public ReceiverBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "WIRELESS_RECEIVER", Items.RECEIVER_ITEM);
    }

    @Override
    public ReceiverBlockEntity getBlockEntity(UUID uuid, Location location) {
        return new ReceiverBlockEntity(this, uuid, location);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        return true;
    }

    @Override
    public void disable() {

    }
}
