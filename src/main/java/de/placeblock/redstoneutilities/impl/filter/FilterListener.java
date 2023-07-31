package de.placeblock.redstoneutilities.impl.filter;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FilterListener implements Listener {
    @EventHandler
    public void on(InventoryMoveItemEvent event) {
        if (!(event.getDestination().getHolder() instanceof Hopper hopper)) return;
        FilterBlockEntity filterBlockEntity = getFilterBlockEntity(hopper);
        if (filterBlockEntity == null) return;
        boolean cancelled = this.handleItemFilter(event.getItem(), filterBlockEntity);
        event.setCancelled(cancelled);
    }

    @EventHandler
    public void on(InventoryPickupItemEvent event) {
        if (!(event.getInventory().getHolder() instanceof Hopper hopper)) return;
        FilterBlockEntity filterBlockEntity = getFilterBlockEntity(hopper);
        if (filterBlockEntity == null) return;
        boolean cancelled = this.handleItemFilter(event.getItem().getItemStack(), filterBlockEntity);
        event.setCancelled(cancelled);
    }

    private boolean handleItemFilter(ItemStack item, FilterBlockEntity filterBlockEntity) {
        if (filterBlockEntity == null) return false;
        ItemStack filter = filterBlockEntity.getFilter();
        return filter != null && !filter.isSimilar(item);
    }

    @Nullable
    private FilterBlockEntity getFilterBlockEntity(Hopper hopper) {
        Interaction interaction = Util.getInteraction(hopper.getLocation().toCenterLocation());
        if (interaction == null) return null;
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        return blockEntityRegistry.get(interaction, FilterBlockEntity.class);
    }
}
