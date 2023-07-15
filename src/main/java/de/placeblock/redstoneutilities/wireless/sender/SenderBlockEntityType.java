package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntityType;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public class SenderBlockEntityType extends WirelessBlockEntityType<SenderBlockEntity, SenderBlockEntityType> {

    public SenderBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "WIRELESS_SENDER", Items.SENDER_ITEM);
    }


    @Override
    public SenderBlockEntity getBlockEntity(Interaction interaction) {
        return new SenderBlockEntity(this, interaction);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        return true;
    }

    @Override
    public void disable() {
    }
}
