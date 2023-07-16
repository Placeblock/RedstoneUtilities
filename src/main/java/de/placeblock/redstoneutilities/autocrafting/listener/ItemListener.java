package de.placeblock.redstoneutilities.autocrafting.listener;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.autocrafting.AutoCrafterBlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    @EventHandler
    public void on(InventoryMoveItemEvent event) {
        System.out.println(event.getDestination().getHolder());
        if (event.getDestination().getHolder() instanceof Dropper dropper) {
            Block block = dropper.getBlock();
            Location interactionLoc = block.getLocation().toCenterLocation().add(0, 0.5, 0);
            System.out.println(interactionLoc);
            Interaction interaction = Util.getInteraction(interactionLoc);
            System.out.println(interaction);
            if (interaction == null) return;
            BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
            AutoCrafterBlockEntity blockEntity = blockEntityRegistry.get(interaction, AutoCrafterBlockEntity.class);
            System.out.println(blockEntity);
            if (blockEntity == null) return;

            event.setCancelled(true);
            ItemStack item = event.getItem();
            int bestSlot = blockEntity.findBestSlot(item);
            System.out.println(bestSlot);
            if (bestSlot == -1) return;

            event.getInitiator().remove(item);
            ItemStack slotItem = event.getDestination().getItem(bestSlot);
            if (slotItem == null) {
                event.getDestination().setItem(bestSlot, item);
            } else {
                slotItem.setAmount(slotItem.getAmount() + item.getAmount());
            }
        }
    }

}
