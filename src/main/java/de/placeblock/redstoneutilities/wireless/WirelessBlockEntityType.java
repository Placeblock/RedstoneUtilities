package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;

public abstract class WirelessBlockEntityType<B extends WirelessBlockEntity<B, BT>, BT extends WirelessBlockEntityType<B, BT>> extends BlockEntityType<B, BT> {
    public WirelessBlockEntityType(RedstoneUtilities plugin, String name, ItemStack itemStack) {
        super(plugin, name, itemStack, true, List.of(Material.REDSTONE_WIRE));
    }

    protected Interaction spawnEntities(Location location, Material material) {
        World world = location.getWorld();

        Location displayLocation = location.clone().add(0.25, 0, 0.25);
        Location interactionLocation = location.clone().add(0.5, 0, 0.5);

        Interaction interaction = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(0.6F);
            i.setInteractionHeight(0.4F);
        });
        world.spawn(displayLocation, BlockDisplay.class, bd -> {
            bd.setBlock(material.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.5F, 0.5F, 0.5F), new AxisAngle4f()));
            EntityStructureUtil.addEntity(interaction, bd.getUniqueId());
        });
        return interaction;
    }
}
