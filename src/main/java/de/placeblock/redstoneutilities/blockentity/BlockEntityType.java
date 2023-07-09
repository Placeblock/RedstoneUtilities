package de.placeblock.redstoneutilities.blockentity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class BlockEntityType<B extends BlockEntity> {

    private final String name;
    private final ItemStack itemStack;
    private final boolean removeItem;
    private final List<Material> replaceTypes;

    public abstract B loadBlockEntity(Interaction interaction);

    public abstract boolean onPlace(Player player, Block block);

    public abstract B spawn(Block block);

}
