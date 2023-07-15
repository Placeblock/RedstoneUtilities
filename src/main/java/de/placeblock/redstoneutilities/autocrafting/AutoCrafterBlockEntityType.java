package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AutoCrafterBlockEntityType extends BlockEntityType<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> {
    public AutoCrafterBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "AUTOCRAFTER", itemStack, true, new ArrayList<>());
    }

    @Override
    public AutoCrafterBlockEntity getBlockEntity(Interaction interaction) {
        return new AutoCrafterBlockEntity(this, interaction);
    }

    @Override
    public Interaction onPlace(Player player, Block block) {
        if (block.getRelative(BlockFace.DOWN).getType() != Material.DROPPER) {
            player.sendMessage("Du kannst AutoCrafter nur auf Spendern plazieren");
            return null;
        }
        this.spawnEntities(block.getLocation());
        return null;
    }

    protected Interaction spawnEntities(Location location) {
        World world = location.getWorld();

        Location interactionLocation = location.clone().add(0.5, 0, 0.5);

        Interaction interaction = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(1.1F);
            i.setInteractionHeight(1.1F);
        });
        return interaction;
    }

    @Override
    public void disable() {

    }
}
