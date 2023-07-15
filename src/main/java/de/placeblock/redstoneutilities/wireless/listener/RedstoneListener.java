package de.placeblock.redstoneutilities.wireless.listener;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntity;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntity;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

@RequiredArgsConstructor
public class RedstoneListener implements Listener {
    private final RedstoneUtilities plugin;

    @EventHandler
    public void on(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.REDSTONE_WIRE) return;
        Location location = block.getLocation().add(0.5, 0.5, 0.5);
        BlockEntity<?, ?> blockEntity = this.plugin.getBlockEntityRegistry().get(location);
        if (blockEntity instanceof SenderBlockEntity senderBlockEntity) {
            senderBlockEntity.summonParticles();
            for (ReceiverBlockEntity receiver : senderBlockEntity.getReceivers()) {
                receiver.summonParticles();
                receiver.setPower(event.getNewCurrent());
            }
        } else if (blockEntity instanceof ReceiverBlockEntity) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

}
