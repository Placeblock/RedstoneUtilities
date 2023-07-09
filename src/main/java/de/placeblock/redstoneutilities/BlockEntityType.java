package de.placeblock.redstoneutilities;

import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class BlockEntityType {

    private final ItemStack itemStack;
    private final BiFunction<Player, Block, Boolean> onPlace;
    private final Function<Player, BlockEntity> onBreak;
    private final Function<Player, BlockEntity> onInteract;
    private final boolean removeItem;
    private final List<Material> replaceTypes;

}
