package de.placeblock.redstoneutilities.impl.wireless.sender;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.impl.wireless.WirelessBlockEntityType;
import de.placeblock.redstoneutilities.impl.wireless.WirelessManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SenderBlockEntityType extends WirelessBlockEntityType<SenderBlockEntity, SenderBlockEntityType> {

    public SenderBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, WirelessManager.SENDER_NAME, Items.SENDER_ITEM);
    }


    @Override
    public SenderBlockEntity getBlockEntity(UUID uuid, Location location) {
        return new SenderBlockEntity(this, uuid, location);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        return true;
    }

    @Override
    public void disable() {

    }
}
