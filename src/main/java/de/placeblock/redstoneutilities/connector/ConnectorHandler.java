package de.placeblock.redstoneutilities.connector;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConnectorHandler implements Listener {
    private BukkitTask actionBarTask;

    private Map<Player, ConnectorInfo> players = new HashMap<>();

    public void start(Plugin plugin) {
        this.actionBarTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<Player, ConnectorInfo> entry : this.players.entrySet()) {
                Player player = entry.getKey();
                ConnectorInfo connectorInfo = entry.getValue();
                if (connectorInfo.isDestroying()) continue;
                TextComponent message;
                if (connectorInfo.costType == null) {
                    message = Component.text("Kosten: ").color(RedstoneUtilities.INFERIOR_COLOR)
                            .append(Component.text("Kostenlos").color(RedstoneUtilities.PRIMARY_COLOR));
                } else {
                    int cost = this.getCost(player);
                    message = Component.text("Kosten: ").color(RedstoneUtilities.INFERIOR_COLOR)
                        .append(Component.text(cost + " " + connectorInfo.costType).color(RedstoneUtilities.PRIMARY_COLOR));
                }
                player.sendActionBar(message);
            }
        }, 0, 10);
    }

    public int getCost(Player player) {
        Location location = this.players.get(player).getConnectable().getCenterLocation();
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

    public boolean removeCost(Player player, int cost, Material costType) {
        if (player.getInventory().contains(costType, cost)) {
            player.getInventory().removeItem(new ItemStack(costType, cost));
            return true;
        } else {
            player.sendMessage(Messages.NOT_ENOUGH_RESSOURCES);
            return false;
        }
    }

    public void giveCost(Player player, int cost, Material costItem) {
        player.getInventory().addItem(new ItemStack(costItem, cost));
        player.sendMessage(Messages.RESSOURCES_RECEIVED);
    }

    public void disable() {
        if (this.actionBarTask == null) return;
        this.actionBarTask.cancel();
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        this.players.remove(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() && event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (this.players.remove(player) != null) {
                player.sendMessage(Messages.CONNECT_CANCELLED);
            }
        }
    }

    public void addPlayer(Player player, Connectable<?, ?> connectable, Material costType, boolean destroying) {
        this.players.put(player, new ConnectorInfo(connectable, costType, destroying));
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

    public void removeConnectable(Connectable<?, ?> connectable) {
        this.players = filterByValue(this.players, v -> !v.connectable.equals(connectable));
    }

    static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
        return map.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Getter
    @RequiredArgsConstructor
    public static class ConnectorInfo {
        private final Connectable<?, ?> connectable;
        private final Material costType;
        private final boolean destroying;
    }
}
