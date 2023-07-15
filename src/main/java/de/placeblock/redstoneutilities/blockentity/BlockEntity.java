package de.placeblock.redstoneutilities.blockentity;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
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
    private static final NamespacedKey TYPE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "block_entity_type");

    protected final BlockEntityType<B, BT> type;
    protected Interaction interaction;
    @Setter
    protected List<Entity> entityStructure = new ArrayList<>();

    public BlockEntity(BlockEntityType<B, BT> type, Interaction interaction) {
        this.type = type;
        this.interaction = interaction;
    }

    public void remove(Player player) {
        this.remove(player, true);
    }

    public void remove(Player player, boolean drop) {
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

    public abstract void summon(Location location);

    public void load() {
        this.setEntityStructure(EntityStructureUtil.getEntities(interaction));
    }

    public void store() {
        List<UUID> entityUUIDs = this.entityStructure.stream().map(Entity::getUniqueId).toList();
        System.out.println(entityUUIDs);
        EntityStructureUtil.setEntities(this.interaction, entityUUIDs);
    }

}
