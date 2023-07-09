package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.*;
import de.placeblock.redstoneutilities.blockentity.BlockEntityHandler;
import de.placeblock.redstoneutilities.blockentity.BlockEntityUtil;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import de.placeblock.redstoneutilities.wireless.listener.BlockBreakListener;
import de.placeblock.redstoneutilities.wireless.listener.BlockInteractListener;
import de.placeblock.redstoneutilities.wireless.listener.RedstoneListener;
import de.placeblock.redstoneutilities.wireless.recipes.ConnectorRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.InfometerRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.ReceiverRecipe;
import de.placeblock.redstoneutilities.wireless.recipes.SenderRecipe;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Consumer;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;

@RequiredArgsConstructor
public class Wireless {

    private final RedstoneUtilities plugin;

    @Getter
    private ConnectorHandler connectorHandler;
    @Getter
    private TextInputHandler textInputHandler;

    public void setup() {
        new ReceiverRecipe().register();
        new SenderRecipe().register();
        new ConnectorRecipe().register();
        new InfometerRecipe().register();

        PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        pluginManager.registerEvents(new BlockBreakListener(), this.plugin);
        pluginManager.registerEvents(new BlockInteractListener(), this.plugin);
        pluginManager.registerEvents(new RedstoneListener(), this.plugin);

        this.connectorHandler = new ConnectorHandler();
        this.connectorHandler.start(this.plugin);
        pluginManager.registerEvents(this.connectorHandler, this.plugin);
        this.textInputHandler = new TextInputHandler();
        pluginManager.registerEvents(this.textInputHandler, this.plugin);

        this.registerSenderBlock();
        this.registerReceiverBlock();
    }

    private void registerSenderBlock() {
        BlockEntityHandler.Entry entry = new BlockEntityHandler.Entry(
                Items.SENDER_ITEM,
                this::placeSender,
                true,
                List.of(Material.REDSTONE_WIRE)
        );
        this.plugin.getBlockEntityHandler().register(entry);
    }

    private void registerReceiverBlock() {
        BlockEntityHandler.Entry entry = new BlockEntityHandler.Entry(
                Items.RECEIVER_ITEM,
                this::placeReceiver,
                true,
                List.of(Material.REDSTONE_WIRE)
        );
        this.plugin.getBlockEntityHandler().register(entry);
    }

    public void addReceiverToSender(Location sender, Location receiver) {
        Interaction entity = Util.getInteraction(sender);
        if (entity == null) return;
        if (!InteractionPDCUtil.isSender(entity)) return;
        InteractionPDCUtil.addReceiver(entity, receiver);
    }

    public void addSenderToReceiver(Location sender, Location receiver) {
        Interaction entity = Util.getInteraction(receiver);
        if (entity == null) return;
        if (!InteractionPDCUtil.isReceiver(entity)) return;
        InteractionPDCUtil.addSender(entity, sender);
    }

    public boolean placeSender(Player player, Block block) {
        Location location = block.getLocation();
        if (this.plugin.getBlockEntityUtil().isBlockEntity(block)) return false;
        this.spawnEntities(location, Material.SCULK_SHRIEKER, InteractionPDCUtil::markSender);
        return true;
    }

    public boolean placeReceiver(Player player, Block block) {
        Location location = block.getLocation();
        if (this.plugin.getBlockEntityUtil().isBlockEntity(block)) return false;
        this.spawnEntities(location, Material.CALIBRATED_SCULK_SENSOR, InteractionPDCUtil::markReceiver);
        player.sendMessage(Messages.PLACE_RECEIVER);
        return true;
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

    public void setPower(Interaction interaction, int power) {
        RedstoneWire redstoneWire = Util.getRedstone(interaction);
        if (redstoneWire == null) return;
        redstoneWire.setPower(power);
        interaction.getLocation().getBlock().setBlockData(redstoneWire);
    }

    public boolean isInfometer(ItemStack item) {
        return item.getType() == Items.INFOMETER_MATERIAL
                && Items.REDSTONE_INFOMETER_DISPLAYNAME.equals(item.getItemMeta().displayName());
    }

    public void disable() {
        this.connectorHandler.stop();
    }
}
