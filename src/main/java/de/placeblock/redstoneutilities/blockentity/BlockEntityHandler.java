package de.placeblock.redstoneutilities.blockentity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BlockEntityHandler implements Listener {
    private final Map<ItemStack, Entry> entries = new HashMap<>();

    public void register(Entry entry) {
        this.entries.put(entry.getItemStack(), entry);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Block clickedBlock = event.getBlockAgainst();
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItemInHand();
        Entry entry = this.getEntry(item);
        if (entry == null) return;

        Player player = event.getPlayer();

        Location targetLoc = this.getTargetLocation(clickedBlock.getLocation(), event.getBlock().getLocation(), entry);
        boolean accept = entry.getOnPlace().apply(player, targetLoc.getBlock());
        if (!accept) return;

        if (!entry.isRemoveItem()) {
            item.setAmount(item.getAmount()+1);
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getHand() != EquipmentSlot.HAND ||
            event.getClickedBlock() == null ||
            event.getAction() != Action.RIGHT_CLICK_BLOCK ||
            item == null) return;
        Entry entry = this.getEntry(item);
        if (entry == null) return;
        event.setCancelled(true);

        Player player = event.getPlayer();

        Block clickedBlock = event.getClickedBlock();
        Block placedBlock = clickedBlock.getRelative(event.getBlockFace());

        Location targetLoc = this.getTargetLocation(clickedBlock.getLocation(), placedBlock.getLocation(), entry);

        boolean accept = entry.getOnPlace().apply(player, targetLoc.getBlock());
        if (!accept) return;

        if (entry.isRemoveItem()) {
            ItemStack removeItem = entry.getItemStack().clone();
            removeItem.setAmount(1);
            player.getInventory().removeItem(removeItem);
        }

        player.playSound(player, Sound.BLOCK_STONE_PLACE, 1.0F, 1.0F);
    }

    private Location getTargetLocation(Location placedAgainstLoc, Location placedLocation, Entry entry) {
        Block clickedBlock = placedAgainstLoc.getBlock();
        boolean replace = false;
        for (Material material : entry.getReplaceTypes()) {
            if (clickedBlock.getType() == material) {
                replace = true;
            }
        }
        if (replace) {
            return placedAgainstLoc;
        } else {
            return placedLocation;
        }
    }

    private Entry getEntry(ItemStack itemStack) {
        for (ItemStack stack : this.entries.keySet()) {
            if (itemStack.isSimilar(stack)) {
                return this.entries.get(stack);
            }
        }
        return null;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Entry {
        private final ItemStack itemStack;
        private final BiFunction<Player, Block, Boolean> onPlace;
        private final Function<Player, Interaction> onBreak;
        private final Function<PlayerInteractEvent, Interaction> onInteract;
        private final boolean removeItem;
        private final List<Material> replaceTypes;
    }

}
