package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class AutoCrafterBlockEntity extends BlockEntity<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> {
    public static Vector CORNERS_VEC = new Vector(0.44, 0.44, 0.44);
    private Recipe recipe;

    public AutoCrafterBlockEntity(BlockEntityType<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> type, Interaction interaction) {
        super(type, interaction);
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().isSneaking()) return;
        AutoCrafterGUI autoCrafterGUI = new AutoCrafterGUI(event.getPlayer());
        autoCrafterGUI.register();
        autoCrafterGUI.setup();
        autoCrafterGUI.show();
    }

    @Override
    public void summon(Location location) {
        World world = location.getWorld();

        Location interactionLocation = location.clone().add(0.5, -0.025, 0.5);
        Location craftingDisplayLoc = location.clone().add(-0.005, -1, -0.005);

        this.interaction = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(1.011F);
            i.setInteractionHeight(0.05F);
        });
        world.spawn(craftingDisplayLoc, BlockDisplay.class, bd -> {
            bd.setBlock(Material.CRAFTING_TABLE.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.01F, 1.01F, 1.01F), new AxisAngle4f()));
            this.entityStructure.add(bd);
        });
        Location craftingCenterLoc = location.clone().add(0.5, -0.5, 0.5);
        this.summonIronBlocks(world, craftingCenterLoc, CORNERS_VEC);
        this.summonIronBlocks(world, craftingCenterLoc, CORNERS_VEC.multiply(-1));
    }

    private void summonIronBlocks(World world, Location craftingCenterLoc, Vector cornersVec) {
        for (int i = 0; i < 4; i++) {
            Vector ironBlockVec = cornersVec.clone().rotateAroundY(i*(Math.PI/2));
            Location ironBlockLoc = craftingCenterLoc.clone().add(ironBlockVec).add(-0.1F, -0.1F, -0.1F);
            world.spawn(ironBlockLoc, BlockDisplay.class, id -> {
                id.setBlock(Material.IRON_BLOCK.createBlockData());
                id.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f()));
                id.setBrightness(new Display.Brightness(15, 15));
                this.entityStructure.add(id);
            });
        }
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
