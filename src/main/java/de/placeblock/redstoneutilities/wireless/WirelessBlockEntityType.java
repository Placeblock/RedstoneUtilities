package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class WirelessBlockEntityType<B extends WirelessBlockEntity<B, BT>, BT extends WirelessBlockEntityType<B, BT>> extends BlockEntityType<B, BT> {
    public WirelessBlockEntityType(RedstoneUtilities plugin, String name, ItemStack itemStack) {
        super(plugin, name, itemStack, true, List.of(Material.REDSTONE_WIRE));
    }
}
