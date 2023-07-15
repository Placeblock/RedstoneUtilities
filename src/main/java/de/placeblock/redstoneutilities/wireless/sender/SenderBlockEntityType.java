package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntity;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntityType;
import de.placeblock.redstoneutilities.wireless.WirelessPDCUtil;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;

public class SenderBlockEntityType extends WirelessBlockEntityType<SenderBlockEntity, SenderBlockEntityType> {

    public SenderBlockEntityType(RedstoneUtilities plugin, String name) {
        super(plugin, name, Items.SENDER_ITEM);
    }


    @Override
    public SenderBlockEntity getBlockEntity(Interaction interaction) {
        return new SenderBlockEntity(this, interaction);
    }

    @Override
    public Interaction onPlace(Player player, Block block) {
        return this.spawnEntities(block.getLocation(), Material.SCULK_SHRIEKER);
    }

    @Override
    public void disable() {
    }
}
