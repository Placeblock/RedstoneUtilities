package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class BlockEntityListener implements Listener {

    private final RedstoneUtilities plugin;

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Block clickedBlock = event.getBlockAgainst();
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItemInHand();
        BlockEntityTypeRegistry blockEntityTypeRegistry = this.plugin.getBlockEntityTypeRegistry();
        BlockEntityType<?, ?> blockEntityType = blockEntityTypeRegistry.getBlockEntityType(item);
        if (blockEntityType == null) return;

        Player player = event.getPlayer();

        Location targetLoc = this.getTargetLocation(clickedBlock.getLocation(), event.getBlock().getLocation(), blockEntityType);

        this.createBlockEntity(blockEntityType, targetLoc, player);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getHand() != EquipmentSlot.HAND ||
            event.getClickedBlock() == null ||
            event.getAction() != Action.RIGHT_CLICK_BLOCK ||
            item == null) return;
        BlockEntityTypeRegistry blockEntityTypeRegistry = this.plugin.getBlockEntityTypeRegistry();
        BlockEntityType<?, ?> blockEntityType = blockEntityTypeRegistry.getBlockEntityType(item);
        if (blockEntityType == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();

        Block clickedBlock = event.getClickedBlock();
        Block placedBlock = clickedBlock.getRelative(event.getBlockFace());

        Location targetLoc = this.getTargetLocation(clickedBlock.getLocation(), placedBlock.getLocation(), blockEntityType);

        if (!this.createBlockEntity(blockEntityType, targetLoc, player)) return;

        if (blockEntityType.isRemoveItem()) {
            ItemStack removeItem = blockEntityType.getItemStack().clone();
            removeItem.setAmount(1);
            player.getInventory().removeItem(removeItem);
        }

        player.playSound(player, Sound.BLOCK_STONE_PLACE, 1.0F, 1.0F);
    }

    private boolean createBlockEntity(BlockEntityType<?, ?> blockEntityType, Location targetLoc, Player player) {
        if (!blockEntityType.canBePlaced(player, targetLoc)) return false;
        BlockEntity<?, ?> blockEntity = blockEntityType.getBlockEntity(targetLoc);
        blockEntity.summon(targetLoc);
        Interaction interaction = blockEntity.getInteraction();
        BlockEntityTypeRegistry.setType(interaction, blockEntityType);
        blockEntity.store();
        return true;
    }

    @EventHandler
    public void on(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Interaction interaction)) return;
        BlockEntity<?, ?> blockEntity = this.plugin.getBlockEntityRegistry().get(interaction);
        if (blockEntity == null) return;
        blockEntity.onInteract(event);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)
            || !(event.getEntity() instanceof Interaction interaction)) return;
        BlockEntityRegistry blockEntityRegistry = this.plugin.getBlockEntityRegistry();
        BlockEntity<?, ?> blockEntity = blockEntityRegistry.get(interaction);
        blockEntityRegistry.remove(blockEntity);
        blockEntity.remove(player);
    }

    private Location getTargetLocation(Location placedAgainstLoc, Location placedLocation, BlockEntityType<?, ?> entry) {
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
}
