package de.placeblock.redstoneutilities.wireless.receiver;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntityType;
import de.placeblock.redstoneutilities.wireless.WirelessPDCUtil;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReceiverBlockEntityType extends WirelessBlockEntityType<ReceiverBlockEntity, ReceiverBlockEntityType> {
    public ReceiverBlockEntityType(RedstoneUtilities plugin, String name) {
        super(plugin, name, Items.RECEIVER_ITEM);
    }


    @Override
    public ReceiverBlockEntity getBlockEntity(Interaction interaction) {
        return new ReceiverBlockEntity(this, interaction);
    }

    @Override
    public Interaction onPlace(Player player, Block block) {
        player.sendMessage(Messages.PLACE_RECEIVER);
        return this.spawnEntities(block.getLocation(), Material.CALIBRATED_SCULK_SENSOR);
    }

    @Override
    public void disable() {

    }
}
