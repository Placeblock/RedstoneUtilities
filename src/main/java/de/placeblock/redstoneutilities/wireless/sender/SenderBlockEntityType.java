package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntityType;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public class SenderBlockEntityType extends WirelessBlockEntityType<SenderBlockEntity> {
    public SenderBlockEntityType(String name) {
        super(name, Items.SENDER_ITEM);
    }

    @Override
    public SenderBlockEntity loadBlockEntity(Interaction interaction) {
        return null;
    }

    @Override
    public boolean onPlace(Player player, Block block) {
        return false;
    }

    @Override
    public SenderBlockEntity spawn(Block block) {
        return null;
    }
}
