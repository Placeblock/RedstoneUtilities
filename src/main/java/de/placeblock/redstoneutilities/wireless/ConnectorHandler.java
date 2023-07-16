package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ConnectorHandler implements Listener {
    private BukkitTask actionBarTask;

    private final Map<Player, ConnectorInfo> players = new HashMap<>();

    public void start(Plugin plugin) {
        this.actionBarTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Map.Entry<Player, ConnectorInfo> entry : this.players.entrySet()) {
                Player player = entry.getKey();
                if (entry.getValue().isDestroying()) continue;
                int cost = this.getCost(player);
                TextComponent message = Component.text("Kosten: ").color(RedstoneUtilities.INFERIOR_COLOR)
                        .append(Component.text(cost + " Redstone").color(RedstoneUtilities.PRIMARY_COLOR));
                player.sendActionBar(message);
            }
        }, 0, 10);
    }

    public int getCost(Player player) {
        Location location = this.players.get(player).getSenderBlockEntity().getCenterLocation();
        Entity targetEntity = player.getTargetEntity(100, false);
        Location targetLoc;
        if (targetEntity == null) {
            Block targetBlock = player.getTargetBlockExact(100, FluidCollisionMode.NEVER);
            BlockFace targetBlockFace = player.getTargetBlockFace(100, FluidCollisionMode.NEVER);
            if (targetBlock == null || targetBlockFace == null) {
                targetLoc = player.getLocation();
            }else {
                targetLoc = targetBlock.getRelative(targetBlockFace).getLocation();
            }
        } else {
            targetLoc = targetEntity.getLocation();
        }

        return this.getCost(location, targetLoc);
    }

    public int getCost(Location location, Location target) {
        return (int) location.distance(target);
    }

    public boolean removeCost(Player player, int cost) {
        if (player.getInventory().contains(Material.REDSTONE, cost)) {
            player.getInventory().removeItem(new ItemStack(Material.REDSTONE, cost));
            return true;
        } else {
            player.sendMessage(Messages.NOT_ENOUGH_REDSTONE);
            return false;
        }
    }

    public void giveCost(Player player, Location sender, Location receiver) {
        int cost = this.getCost(sender, receiver);
        player.getInventory().addItem(new ItemStack(Material.REDSTONE, cost));
        player.sendMessage(Messages.REDSTONE_RECEIVED);
    }

    public void stop() {
        if (this.actionBarTask == null) return;
        this.actionBarTask.cancel();
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        this.players.remove(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking() && event.getAction() == Action.RIGHT_CLICK_AIR) {
            this.players.remove(event.getPlayer());
        }
    }

    public void addPlayer(Player player, SenderBlockEntity sender, boolean destorying) {
        this.players.put(player, new ConnectorInfo(sender, destorying));
    }

    public boolean hasPlayer(Player player) {
        return this.players.containsKey(player);
    }

    public ConnectorInfo getPlayer(Player player) {
        return this.players.get(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }


    @Getter
    @RequiredArgsConstructor
    public static class ConnectorInfo {
        private final SenderBlockEntity senderBlockEntity;
        private final boolean destroying;
    }
}
