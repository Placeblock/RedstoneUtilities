package de.placeblock.redstoneutilities.impl.itemnetwork.networkcontroller;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class NetworkControllerBlockEntityType extends BlockEntityType<NetworkControllerBlockEntity, NetworkControllerBlockEntityType> {
    public NetworkControllerBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "network_controller", Items.NETWORK_CONTROLLER_ITEM, true, List.of());
    }

    @Override
    public NetworkControllerBlockEntity getBlockEntity(UUID uuid, Location targetLoc) {
        return new NetworkControllerBlockEntity(this, uuid, targetLoc);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        return true;
    }

    @Override
    public void disable() {

    }
}
