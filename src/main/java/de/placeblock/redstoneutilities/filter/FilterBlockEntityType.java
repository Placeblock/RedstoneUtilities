package de.placeblock.redstoneutilities.filter;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FilterBlockEntityType extends BlockEntityType<FilterBlockEntity, FilterBlockEntityType> {
    public FilterBlockEntityType(RedstoneUtilities plugin) {
        super(plugin, "filter", Items.FILTER_ITEM, true, List.of(Material.HOPPER));
    }

    @Override
    public FilterBlockEntity getBlockEntity(UUID uuid) {
        return new FilterBlockEntity(this, uuid);
    }

    @Override
    public boolean canBePlaced(Player player, Location location) {
        return true;
    }

    @Override
    public void disable() {

    }
}
