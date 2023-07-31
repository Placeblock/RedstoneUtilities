package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeleporterListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL ||
            event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        if (block.getType() != Material.WARPED_PRESSURE_PLATE) return;
        Interaction interaction = Util.getInteraction(block.getLocation());
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        TeleporterBlockEntity blockEntity = blockEntityRegistry.get(interaction, TeleporterBlockEntity.class);
        if (blockEntity == null) return;
        TeleporterBlockEntity targetTeleporter = blockEntity.getTargetTeleporter();
        Location teleportLocation = targetTeleporter.getCenterLocation();
        event.getPlayer().teleport(teleportLocation);
    }

}
