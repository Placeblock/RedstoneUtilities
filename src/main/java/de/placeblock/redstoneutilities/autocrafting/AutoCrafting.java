package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityListener;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AutoCrafting {

    public void setup() {
        this.registerCrafterBlock();
    }

    public void registerCrafterBlock() {
        BlockEntityListener.Entry entry = new BlockEntityListener.Entry(
                Items.AUTOCRAFTER_ITEM,
                this::placeAutoCrafter,
                true,
                new ArrayList<>()
        );
        RedstoneUtilities.getInstance().getBlockEntityListener().register(entry);
    }

    public boolean placeAutoCrafter(Player player, Block block) {

    }

}
