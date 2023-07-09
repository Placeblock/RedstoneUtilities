package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;

public abstract class WirelessBlockEntityType<B extends WirelessBlockEntity> extends BlockEntityType<B> {
    public WirelessBlockEntityType(String name, ItemStack itemStack) {
        super(name, itemStack, true, List.of(Material.REDSTONE_WIRE));
    }

    private void spawnEntities(Location location, Material material, Consumer<Interaction> consumer) {
        World world = location.getWorld();

        Location displayLocation = location.clone().add(0.25, 0, 0.25);
        Location interactionLocation = location.clone().add(0.5, 0, 0.5);

        Interaction interaction = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(0.6F);
            i.setInteractionHeight(0.4F);
            consumer.accept(i);
        });
        world.spawn(displayLocation, BlockDisplay.class, bd -> {
            bd.setBlock(material.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.5F, 0.5F, 0.5F), new AxisAngle4f()));
            EntityStructureUtil entityStructureUtil = this.plugin.getEntityStructureUtil();
            entityStructureUtil.addEntity(interaction, bd.getUniqueId());
        });
    }
}
