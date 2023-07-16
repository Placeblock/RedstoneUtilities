package de.placeblock.redstoneutilities.blockentity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@RequiredArgsConstructor
public abstract class BlockEntity<B extends BlockEntity<B, BT>, BT extends BlockEntityType<B, BT>> {

    protected final BlockEntityType<B, BT> type;
    protected Interaction interaction;
    @Setter
    protected List<UUID> entityStructure = new ArrayList<>();

    public BlockEntity(BlockEntityType<B, BT> type, Interaction interaction) {
        this.type = type;
        this.interaction = interaction;
    }

    public UUID getUUID() {
        return this.interaction.getUniqueId();
    }

    public void remove(Player player) {
        this.remove(player, true);
    }

    public List<Entity> getStructureEntities() {
        List<Entity> entities = new ArrayList<>();
        for (UUID uuid : this.entityStructure) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public void remove(Player player, boolean drop) {
        this.disable();
        for (Entity entity : this.getStructureEntities()) {
            entity.remove();
        }
        this.interaction.remove();
        if (drop) {
            this.drop();
        }
    }

    public Location getCenterLocation() {
        return this.interaction.getLocation().toCenterLocation();
    }

    public Location getBlockLocation() {
        return this.interaction.getLocation().toBlockLocation();
    }

    public abstract void onInteract(PlayerInteractAtEntityEvent event);

    public void drop() {
        Location centerLocation = this.getCenterLocation();
        World world = centerLocation.getWorld();
        world.dropItem(centerLocation, this.type.getItemStack());
    }

    public abstract void summon(Location location);

    public void load() {
        this.setEntityStructure(EntityStructureUtil.getEntityUUIDs(this.interaction));
    }

    public void store() {
        EntityStructureUtil.setEntities(this.interaction, this.entityStructure);
    }

    public abstract void disable();

}
