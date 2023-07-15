package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public abstract class BlockEntityType<B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> {

    protected final RedstoneUtilities plugin;
    protected final String name;
    protected final ItemStack itemStack;
    protected final boolean removeItem;
    protected final List<Material> replaceTypes;

    public BlockEntityType(RedstoneUtilities plugin, String name, ItemStack itemStack, boolean removeItem, List<Material> replaceTypes) {
        this.plugin = plugin;
        this.name = name;
        this.itemStack = itemStack;
        this.removeItem = removeItem;
        this.replaceTypes = replaceTypes;

        this.plugin.getLogger().info("Registered BlockEntityType " + name);
    }

    public abstract B getBlockEntity(Interaction interaction);

    public abstract Interaction onPlace(Player player, Block block);

    public abstract void disable();
}
