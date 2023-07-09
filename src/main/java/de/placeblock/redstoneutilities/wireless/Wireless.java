package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.*;
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
    }

    public boolean placeSender(Player player, Block block) {
        Location location = block.getLocation();
        if (this.plugin.getBlockEntityUtil().isBlockEntity(block)) return false;
        this.spawnEntities(location, Material.SCULK_SHRIEKER, WirelessPDCUtil::markSender);
        return true;
    }

    public boolean placeReceiver(Player player, Block block) {
        Location location = block.getLocation();
        if (this.plugin.getBlockEntityUtil().isBlockEntity(block)) return false;
        this.spawnEntities(location, Material.CALIBRATED_SCULK_SENSOR, WirelessPDCUtil::markReceiver);
        player.sendMessage(Messages.PLACE_RECEIVER);
        return true;
    }


    public boolean isInfometer(ItemStack item) {
        return item.getType() == Items.INFOMETER_MATERIAL
                && Items.REDSTONE_INFOMETER_DISPLAYNAME.equals(item.getItemMeta().displayName());
    }

    public void disable() {
        this.connectorHandler.stop();
    }
}
