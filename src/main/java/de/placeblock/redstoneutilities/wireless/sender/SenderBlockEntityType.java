package de.placeblock.redstoneutilities.wireless.sender;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.WirelessBlockEntityType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

public class SenderBlockEntityType extends WirelessBlockEntityType<SenderBlockEntity> {

    public SenderBlockEntityType(RedstoneUtilities plugin, String name) {
        super(plugin, name, Items.SENDER_ITEM);
    }

    @Override
    public SenderBlockEntity loadBlockEntity(Interaction interaction) {
        return null;
    }

    @Override
    public boolean onPlace(Player player, Block block) {
        this.spawnEntities(block.getLocation(), Material.SCULK_SHRIEKER);
        return true;
    }

    @Override
    public SenderBlockEntity spawn(Block block) {
        return null;
    }

    @Override
    public void disable() {
    }
}
