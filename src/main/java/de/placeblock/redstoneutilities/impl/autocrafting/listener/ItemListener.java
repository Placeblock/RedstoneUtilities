package de.placeblock.redstoneutilities.impl.autocrafting.listener;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.impl.autocrafting.AutoCrafterBlockEntity;
import de.placeblock.redstoneutilities.impl.autocrafting.RecipeUtil;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Map;
import java.util.stream.Collectors;

public class ItemListener implements Listener {

    @EventHandler
    public void on(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        if (source.getHolder() instanceof Dropper dropper) {
            AutoCrafterBlockEntity blockEntity = this.getBlockEntity(dropper);
            if (blockEntity != null) {
                event.setCancelled(true);
                return;
            }
        }
        Inventory destination = event.getDestination();
        if (destination.getHolder() instanceof Dropper dropper) {
            AutoCrafterBlockEntity blockEntity = this.getBlockEntity(dropper);
            if (blockEntity == null) return;
            event.setCancelled(true);
            if (!dropper.getChunk().isLoaded()) return;
            ItemStack itemCopy = event.getItem().clone();

            Bukkit.getScheduler().runTaskLater(RedstoneUtilities.getInstance(), () ->
                this.handleItemMove(event.getItem(), dropper.getInventory(), itemCopy, blockEntity), 1);
        }
    }

    private int handleItemMove(ItemStack initiatorItem, Inventory inventory, ItemStack item, AutoCrafterBlockEntity blockEntity) {
        int invalidItems = 0;
        for (int i = 0; i < item.getAmount(); i++) {
            Recipe recipe = blockEntity.getRecipe();
            int bestSlot = RecipeUtil.findBestSlot(inventory, recipe, item);
            if (bestSlot == -1) {
                invalidItems++;
                continue;
            }
            if (initiatorItem != null) {
                initiatorItem.setAmount(initiatorItem.getAmount()-1);
            }
            ItemStack slotItem = inventory.getItem(bestSlot);
            if (slotItem == null) {
                ItemStack itemClone = item.clone();
                itemClone.setAmount(1);
                inventory.setItem(bestSlot, itemClone);
            } else {
                slotItem.setAmount(slotItem.getAmount() + 1);
            }
        }
        return invalidItems;
    }

    private AutoCrafterBlockEntity getBlockEntity(Dropper dropper) {
        Block block = dropper.getBlock();
        Location interactionLoc = block.getLocation().toCenterLocation().add(0, 0.5, 0);
        Interaction interaction = Util.getInteraction(interactionLoc);
        if (interaction == null) return null;
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        return blockEntityRegistry.get(interaction, AutoCrafterBlockEntity.class);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (((event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                (event.getClickedInventory() != null &&
                event.getClickedInventory().getHolder() instanceof Dropper))
            && (event.getAction() != InventoryAction.PLACE_ALL ||
                (event.getClickedInventory() != null &&
                event.getClickedInventory().getHolder() instanceof Player))) ||
            !(inventory.getHolder() instanceof Dropper dropper) ||
            event.getCurrentItem() == null) return;

        AutoCrafterBlockEntity blockEntity = this.getBlockEntity(dropper);
        if (blockEntity == null) return;
        event.setCancelled(true);
        ItemStack item;
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            item = event.getCurrentItem();
        } else {
            item = event.getCursor();
        }
        if (item == null) return;
        this.handleItemMove(item, inventory, item.clone(), blockEntity);
    }

    @EventHandler
    public void on(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof Dropper dropper)) return;
        AutoCrafterBlockEntity blockEntity = this.getBlockEntity(dropper);
        if (blockEntity == null) return;

        Map<Integer, ItemStack> newItems = event.getNewItems();
        newItems = newItems.entrySet().stream()
                .filter(a->a.getKey()<9)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Integer slot : event.getNewItems().keySet()) {
            if (slot < 9) {
                ItemStack newItemStack = newItems.get(slot);
                int newAmount = newItemStack.getAmount();
                ItemStack invItem = event.getInventory().getItem(slot);
                int oldAmount = invItem == null ? 0 : invItem.getAmount();
                newItems.get(slot).setAmount(newAmount - oldAmount);
            }
        }

        Map<Integer, ItemStack> finalNewItems = newItems;
        Bukkit.getScheduler().runTaskLater(RedstoneUtilities.getInstance(), () ->
                this.handleItemDrag(event, blockEntity, finalNewItems), 1);
    }

    private void handleItemDrag(InventoryDragEvent event, AutoCrafterBlockEntity blockEntity, Map<Integer, ItemStack> newItems) {
        Inventory inventory = event.getInventory();
        int splitAmount = 0;
        for (Map.Entry<Integer, ItemStack> entry : newItems.entrySet()) {
            ItemStack invItem = inventory.getItem(entry.getKey());
            int removeAmount = entry.getValue().getAmount();
            splitAmount += removeAmount;
            if (invItem == null)  {
                inventory.setItem(entry.getKey(), null);
            } else {
                invItem.setAmount(invItem.getAmount()- removeAmount);
            }
        }
        ItemStack item = event.getOldCursor().clone();
        item.setAmount(splitAmount);
        int invalid = this.handleItemMove(null, inventory, item, blockEntity);
        ItemStack invalidItem = event.getOldCursor().clone();
        invalidItem.setAmount(invalid);
        event.getWhoClicked().getInventory().addItem(invalidItem);
    }

}
