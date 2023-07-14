package de.placeblock.redstoneutilities.wireless.receiver;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntityType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public class ReceiverBlockEntityType extends WirelessBlockEntityType<ReceiverBlockEntity> {
    public ReceiverBlockEntityType(RedstoneUtilities plugin, String name) {
        super(plugin, name, Items.RECEIVER_ITEM);
    }

    @Override
    public ReceiverBlockEntity loadBlockEntity(Interaction interaction) {
        return null;
    }

    @Override
    public boolean onPlace(Player player, Block block) {
        this.spawnEntities(block.getLocation(), Material.CALIBRATED_SCULK_SENSOR);
        player.sendMessage(Messages.PLACE_RECEIVER);
        return true;
    }

    @Override
    public ReceiverBlockEntity spawn(Block block) {
        return null;
    }

    @Override
    public void disable() {

    }
}
