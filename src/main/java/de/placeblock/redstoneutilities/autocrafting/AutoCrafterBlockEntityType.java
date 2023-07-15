package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AutoCrafterBlockEntityType extends BlockEntityType<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> {
    public AutoCrafterBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "AUTO_CRAFTER", Items.AUTOCRAFTER_ITEM, true, new ArrayList<>());
    }

    @Override
    public AutoCrafterBlockEntity getBlockEntity(Interaction interaction) {
        return new AutoCrafterBlockEntity(this, interaction);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        Block block = location.getBlock();
        if (block.getRelative(BlockFace.DOWN).getType() != Material.DROPPER) {
            player.sendMessage("Du kannst AutoCrafter nur auf Spendern plazieren");
            return false;
        }
        return true;
    }

    @Override
    public void disable() {

    }
}
