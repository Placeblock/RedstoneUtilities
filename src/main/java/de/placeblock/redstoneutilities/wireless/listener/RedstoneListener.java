package de.placeblock.redstoneutilities.wireless.listener;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.wireless.InteractionPDCUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RedstoneListener implements Listener {

    @EventHandler
    public void on(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.REDSTONE_WIRE) return;
        Location location = block.getLocation().add(0.5, 0.5, 0.5);
        Interaction interaction = Util.getInteraction(location);
        if (interaction == null) return;
        if (InteractionPDCUtil.isSender(interaction)) {
            this.summonSenderParticles(interaction);
            for (Location receiverLoc : InteractionPDCUtil.getReceivers(interaction)) {
                Block receiverBlock = receiverLoc.getBlock();
                this.summonReceiverParticles(receiverBlock.getLocation().add(0.5, 0.7, 0.5));
                if (receiverBlock.getType() != Material.REDSTONE_WIRE) continue;
                RedstoneWire redstoneWire = (RedstoneWire) receiverBlock.getBlockData();
                redstoneWire.setPower(event.getNewCurrent());
                receiverBlock.setBlockData(redstoneWire);
            }
        } else if (InteractionPDCUtil.isReceiver(interaction)
                && event.getNewCurrent() <= event.getOldCurrent()) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    private void summonSenderParticles(Interaction interaction) {
        Location location = interaction.getLocation();
        World world = location.getWorld();
        final int[] i = {10};
        new BukkitRunnable() {
            @Override
            public void run() {
                world.spawnParticle(Particle.SHRIEK, location, 1, 0 ,0, 0, 1, 1);
                --i[0];
                if (i[0] == 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(RedstoneUtilities.getInstance(), 0, 5);
    }

    private void summonReceiverParticles(Location location) {
        World world = location.getWorld();
        world.spawnParticle(Particle.SCULK_CHARGE, location, 1, 0, 0, 0, 0, 0.0F);
    }

}
