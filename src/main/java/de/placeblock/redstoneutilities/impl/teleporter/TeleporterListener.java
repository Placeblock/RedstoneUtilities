package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
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
        if (block.getType() != Material.WARPED_PRESSURE_PLATE) return;
        Interaction interaction = Util.getInteraction(block.getLocation());
        if (interaction == null) return;
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        TeleporterBlockEntity blockEntity = blockEntityRegistry.get(interaction, TeleporterBlockEntity.class, TeleporterBlockEntityType.class);
        Player player = event.getPlayer();
        if (blockEntity == null ||
            blockEntity.getIncomingPlayers().contains(player)) return;
        TeleporterBlockEntity targetTeleporter = blockEntity.getTargetTeleporter();
        if (targetTeleporter == null) return;
        this.scheduleTeleport(player, blockEntity, targetTeleporter);
    }

    private void scheduleTeleport(Player player, TeleporterBlockEntity teleporter, TeleporterBlockEntity targetTeleporter) {
        int speedLevel = teleporter.getUpgradeLevel(Upgrade.SPEED, 0);
        int seconds = Upgrade.EFFICIENCY.getMaxLevel()-speedLevel;
        if (seconds != 0) {
            this.sendTitles(player, seconds);
            Bukkit.getScheduler().runTaskLater(RedstoneUtilities.getInstance(), () ->
                    this.teleportPlayer(player, targetTeleporter), 20L * seconds);
        } else {
            this.teleportPlayer(player, targetTeleporter);
        }
    }

    private void teleportPlayer(Player player, TeleporterBlockEntity targetTeleporter) {
        Location teleportLocation = targetTeleporter.getCenterLocation();
        targetTeleporter.getIncomingPlayers().add(player);
        player.teleport(teleportLocation);
        player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT.key(), Sound.Source.AMBIENT, 1, 1));
        Bukkit.getScheduler().runTaskLaterAsynchronously(RedstoneUtilities.getInstance(), () ->
                targetTeleporter.getIncomingPlayers().remove(player), 40);
    }

    private void sendTitles(Player player, int seconds) {
        for (int i = 0; i < seconds; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLaterAsynchronously(RedstoneUtilities.getInstance(), () -> {
                player.showTitle(Title.title(Component.text(seconds - finalI).color(RedstoneUtilities.PRIMARY_COLOR), Component.empty()));
                player.playSound(Sound.sound(org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP.key(), Sound.Source.AMBIENT, 1, 1));
            }, 20L * i);
        }
    }

}
