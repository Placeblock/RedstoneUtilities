package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

@Getter
@Setter
public class TeleporterBlockEntity extends BlockEntity<TeleporterBlockEntity, TeleporterBlockEntityType> {
    private TeleporterBlockEntity targetTeleporter;

    public TeleporterBlockEntity(BlockEntityType<TeleporterBlockEntity, TeleporterBlockEntityType> type, UUID uuid, Location location) {
        super(type, uuid, location);
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {

    }

    @Override
    public void summon(Location location) {

    }

    @Override
    public void disable() {

    }
}
