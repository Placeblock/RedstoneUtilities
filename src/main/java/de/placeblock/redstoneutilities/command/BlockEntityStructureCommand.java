package de.placeblock.redstoneutilities.command;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BlockEntityStructureCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        Entity targetEntity = player.getTargetEntity(20);
        if (!(targetEntity instanceof Interaction interaction)) return false;
        BlockEntity<?, ?> blockEntity = blockEntityRegistry.get(interaction);
        if (blockEntity != null) {
            List<UUID> entityStructure = blockEntity.getEntityStructure();
            player.sendMessage(Component.text(entityStructure.toString()));
            return true;
        }
        return false;
    }
}
