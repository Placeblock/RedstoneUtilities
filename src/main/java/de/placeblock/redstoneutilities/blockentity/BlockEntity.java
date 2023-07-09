package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class BlockEntity {
    private static final NamespacedKey TYPE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "block_entity_type");

    protected final BlockEntityType type;
    protected final Interaction interaction;
    protected final List<Entity> entityStructure;

    public void setType() {
        this.interaction.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, this.type.getName());
    }

    public void remove() {
        this.remove(true);
    }

    public void remove(boolean drop) {
        for (Entity entity : this.entityStructure) {
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

    public void store() {

    }

}
