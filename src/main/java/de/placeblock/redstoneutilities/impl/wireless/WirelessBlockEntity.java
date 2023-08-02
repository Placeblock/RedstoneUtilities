package de.placeblock.redstoneutilities.impl.wireless;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityTypeRegistry;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class WirelessBlockEntity<B extends WirelessBlockEntity<B, BT>, BT extends WirelessBlockEntityType<B, BT>> extends BlockEntity<B, BT> {

    public static final Vector TYPE_ENTITY_VEC = new Vector(0.26, 0, 0);
    public static final String WIRELESS_TYPE_NAME = "WIRELESS_TYPE";



    private Material wirelessType;
    private List<UUID> typeEntities = new ArrayList<>();

    public WirelessBlockEntity(BlockEntityType<B, BT> type, UUID uuid, Location location) {
        super(type, uuid, location);
    }

    public void setWirelessType(Material type) {
        this.wirelessType = type;
        this.removeTypeEntities();
        this.spawnTypeEntities(type);
    }

    private void spawnTypeEntities(Material type) {
        Location centerLocation = this.getCenterLocation().add(0, -0.4, 0);
        Util.summonCircleItemDisplays(centerLocation, 4, TYPE_ENTITY_VEC, new ItemStack(type), 0.2F, (id, rot) -> {
            BlockEntityTypeRegistry.setType(id, WIRELESS_TYPE_NAME);
            this.typeEntities.add(id.getUniqueId());
            this.entityStructure.add(id.getUniqueId());
        });
    }

    private void removeTypeEntities() {
        Util.removeEntities(this.typeEntities);
        this.entityStructure.removeAll(this.typeEntities);
        this.typeEntities.clear();
    }

    public int getPower() {
        RedstoneWire wire = Util.getRedstone(this.getInteraction());
        if (wire == null) return -1;
        return wire.getPower();
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (Items.isInfometer(item)) {
            this.handleInfometerInteraction(player);
        } else {
            this.setWirelessType(item.getType());
        }
    }

    protected abstract void handleInfometerInteraction(Player player);

    @Override
    public void summon(Location location) {
        World world = location.getWorld();
        Location interactionLocation = location.clone().add(0.5, 0, 0.5);

        this.uuid = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(0.6F);
            i.setInteractionHeight(0.4F);
        }).getUniqueId();
    }

    @Override
    public void load() {
        super.load();
        Interaction interaction = this.getInteraction();
        List<UUID> entities = EntityStructureUtil.getEntityUUIDs(interaction, WirelessBlockEntity.WIRELESS_TYPE_NAME);
        this.setTypeEntities(entities);
        this.setWirelessType(WirelessPDCUtil.getType(interaction));
    }

    @Override
    public void store() {
        super.store();
        WirelessPDCUtil.setType(this.getInteraction(), this.wirelessType);
    }
}
