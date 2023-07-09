package de.placeblock.redstoneutilities.wireless.receiver;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntityType;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public class ReceiverBlockEntityType extends WirelessBlockEntityType<ReceiverBlockEntity> {
    public ReceiverBlockEntityType(String name) {
        super(name, Items.RECEIVER_ITEM);
    }

    @Override
    public ReceiverBlockEntity loadBlockEntity(Interaction interaction) {
        return null;
    }

    @Override
    public boolean onPlace(Player player, Block block) {
        return false;
    }

    @Override
    public ReceiverBlockEntity spawn(Block block) {
        return null;
    }
}
