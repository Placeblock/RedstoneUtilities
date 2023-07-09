package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;

public class BlockEntityUtil {

    private static final NamespacedKey TYPE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "block_entity_type");

    public boolean isBlockEntity(Block block) {
        Interaction existingInteraction = Util.getInteraction(block.getLocation().toCenterLocation());
        if (existingInteraction == null) return false;
        return existingInteraction.getPersistentDataContainer().has(TYPE_KEY);
    }
}
