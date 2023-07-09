package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AutoCrafting {

    public void setup() {
        this.registerCrafterBlock();
    }

    public void registerCrafterBlock() {
        BlockEntityHandler.Entry entry = new BlockEntityHandler.Entry(
                Items.AUTOCRAFTER_ITEM,
                this::placeAutoCrafter,
                true,
                new ArrayList<>()
        );
        RedstoneUtilities.getInstance().getBlockEntityHandler().register(entry);
    }

    public boolean placeAutoCrafter(Player player, Block block) {

    }

}
