package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.connector.Connectable;
import de.placeblock.redstoneutilities.connector.ConnectorHandler;
import de.placeblock.redstoneutilities.pdc.LocationPDCUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class BlockEntity<B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> {

    protected final BlockEntityType<B, BT> type;
    protected UUID uuid;
    protected final Location location;
    @Setter
    protected List<UUID> entityStructure = new ArrayList<>();

    public BlockEntity(BlockEntityType<B, BT> type, UUID uuid, Location location) {
        this.type = type;
        this.uuid = uuid;
        this.location = location;
    }

    public void remove(Player player) {
        this.remove(player, true);
    }

    public List<Entity> getStructureEntities(Player player) {
        List<Entity> entities = new ArrayList<>();
        for (UUID uuid : this.entityStructure) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entities.add(entity);
            } else {
                player.sendMessage(Component.text("Kein Entity gefunden für UUID: " + uuid));
            }
        }
        return entities;
    }

    public void remove(Player player, boolean drop) {
        this.disable();
        for (Entity entity : this.getStructureEntities(player)) {
            entity.remove();
        }
        if (drop) {
            this.drop();
        }
        this.getInteraction().remove();
        if (this instanceof Connectable<?,?> connectable) {
            ConnectorHandler connectorHandler = RedstoneUtilities.getInstance().getConnectorHandler();
            connectorHandler.removeConnectable(connectable);
        }
    }

    public void onChange() {
        this.store();
    }

    public Interaction getInteraction() {
        this.getLocation().getWorld().getChunkAt(this.getLocation());
        return (Interaction) Bukkit.getEntity(this.uuid);
    }

    public Location getCenterLocation() {
        return this.location.toCenterLocation();
    }

    public Location getBlockLocation() {
        return this.location.toBlockLocation();
    }

    public abstract void onInteract(PlayerInteractAtEntityEvent event);

    public void drop() {
        Location centerLocation = this.getCenterLocation();
        World world = centerLocation.getWorld();
        world.dropItem(centerLocation, this.type.getItemStack());
    }

    public abstract void summon(Location location);

    public void load() {
        this.setEntityStructure(EntityStructureUtil.getEntityUUIDs(this.getInteraction()));
    }

    public void store() {
        EntityStructureUtil.setEntities(this.getInteraction(), this.entityStructure);
        LocationPDCUtil.setLocation(this.getInteraction(), BlockEntityRegistry.BLOCK_ENTITY_LOCATION_KEY, this.location);
    }

    public void disable() {}

    @Override
    public String toString() {
        return "BlockEntity{" +
                "type=" + type +
                ", uuid=" + uuid +
                ", location=" + location +
                ", entityStructure=" + entityStructure +
                '}';
    }
}
