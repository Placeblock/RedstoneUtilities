package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
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

    protected final RedstoneUtilities plugin;
    protected final String name;
    protected final ItemStack itemStack;
    protected final boolean removeItem;
    protected final List<Material> replaceTypes;

    public abstract B loadBlockEntity(Interaction interaction);

    public abstract boolean onPlace(Player player, Block block);

    public abstract B spawn(Block block);

    public abstract void disable();

}
