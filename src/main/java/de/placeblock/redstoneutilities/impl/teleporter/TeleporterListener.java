package de.placeblock.redstoneutilities.impl.teleporter;

import org.bukkit.block.Block;
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
    }

}
