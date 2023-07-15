package de.placeblock.redstoneutilities.wireless;

import de.placeblock.redstoneutilities.Items;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityTypeRegistry;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class WirelessBlockEntity<B extends WirelessBlockEntity<B, BT>, BT extends WirelessBlockEntityType<B, BT>> extends BlockEntity<B, BT> {
    public static final NamespacedKey WIRELESS_TYPE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "wireless_type");

    public static final Vector TYPE_ENTITY_VEC = new Vector(0.26, 0, 0);
    public static final String WIRELESS_TYPE_NAME = "WIRELESS_TYPE";

    private Material wirelessType;
    private List<ItemDisplay> typeEntities;

    public WirelessBlockEntity(BlockEntityType<B, BT> type, Interaction interaction) {
        super(type, interaction);
    }

    public void setWirelessType(Material type) {
        this.wirelessType = type;
        this.removeTypeEntities();
        this.spawnTypeEntities(type);
    }

    private void spawnTypeEntities(Material type) {
        Location centerLocation = this.interaction.getLocation().toCenterLocation().add(0, -0.4, 0);
        World world = centerLocation.getWorld();
        for (int i = 0; i < 4; i+=1) {
            Vector rotationVec = TYPE_ENTITY_VEC.clone().rotateAroundY(i * (Math.PI / 2));
            Location location = centerLocation.clone().add(rotationVec);
            float finalI = i;
            world.spawn(location, ItemDisplay.class, id -> {
                id.setItemStack(new ItemStack(type));
                id.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f((float) (finalI * (Math.PI / 2) + Math.PI/2), 0F, 1F, 0F), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f()));
                BlockEntityTypeRegistry.setType(id, WIRELESS_TYPE_NAME);
                this.typeEntities.add(id);
                this.entityStructure.add(id);
            });
        }
    }

    private void removeTypeEntities() {
        for (Entity entity : this.typeEntities) {
            entity.remove();
        }
        this.typeEntities.clear();
    }

    public int getPower() {
        RedstoneWire wire = Util.getRedstone(this.interaction);
        if (wire == null) return -1;
        return wire.getPower();
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isSimilar(Items.CONNECTOR_ITEM)) {
            this.handleConnectorInteraction(player);
        } else if (Items.isInfometer(item)) {
            this.handleInfometerInteraction(player);
        } else {
            this.setWirelessType(item.getType());
        }
    }

    protected abstract void handleInfometerInteraction(Player player);

    protected abstract void handleConnectorInteraction(Player player);

    @Override
    public void load() {
        super.load();
        Interaction interaction = this.getInteraction();
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String wirelessTypeName = pdc.get(WIRELESS_TYPE_KEY, PersistentDataType.STRING);
        if (wirelessTypeName != null) {
            this.setWirelessType(Material.valueOf(wirelessTypeName));
        }
        List<Entity> entities = EntityStructureUtil.getEntities(interaction, WirelessBlockEntity.WIRELESS_TYPE_NAME);
        if (entities != null) {
            List<ItemDisplay> itemDisplays = new ArrayList<>();
            for (Entity entity : entities) {
                itemDisplays.add((ItemDisplay) entity);
            }
            this.setTypeEntities(itemDisplays);
        }
    }

    @Override
    public void store() {
        super.store();
        WirelessPDCUtil.setType(this.interaction, this.wirelessType);
    }
}
