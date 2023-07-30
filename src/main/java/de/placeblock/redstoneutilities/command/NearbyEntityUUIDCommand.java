package de.placeblock.redstoneutilities.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NearbyEntityUUIDCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        List<Entity> nearbyEntities = player.getNearbyEntities(0.2, 0.2, 0.2);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof ItemDisplay) {
                player.sendMessage(Component.text(nearbyEntity.getUniqueId().toString()));
            }
        }
        return true;
    }
}
