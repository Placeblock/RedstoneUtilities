package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Recipe;

public class AutoCrafterBlockEntity extends BlockEntity<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> {
    private Recipe recipe;
    private BlockFace fuelFace;
    private BlockDisplay fuelInputDisplay;

    public AutoCrafterBlockEntity(BlockEntityType<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> type, Interaction interaction) {
        super(type, interaction);
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {

    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void store() {
        super.store();
    }
}
