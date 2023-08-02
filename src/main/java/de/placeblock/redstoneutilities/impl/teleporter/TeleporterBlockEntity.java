package de.placeblock.redstoneutilities.impl.teleporter;

import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.*;
import de.placeblock.redstoneutilities.connector.Connectable;
import de.placeblock.redstoneutilities.pdc.PDCUUIDListUtil;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import de.placeblock.redstoneutilities.upgrades.Upgradeable;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

@Getter
@Setter
public class TeleporterBlockEntity extends BlockEntity<TeleporterBlockEntity, TeleporterBlockEntityType> implements Connectable<TeleporterBlockEntity, TeleporterBlockEntity>, Upgradeable {
    public static final NamespacedKey TARGET_TELEPORTER_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "target_teleporter");
    public static final NamespacedKey TELEPORTER_NAME_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "teleporter_name");
    public static final String TARGET_TELEPORTER_ENTITY_NAME = "target_name_entity";

    private List<Player> incomingPlayers = new ArrayList<>();
    private TeleporterBlockEntity targetTeleporter;
    private UUID targetTeleporterEntity;
    private String teleporterName = "default";
    private Map<Upgrade, Integer> upgrades = new HashMap<>();

    public TeleporterBlockEntity(BlockEntityType<TeleporterBlockEntity, TeleporterBlockEntityType> type, UUID uuid, Location location) {
        super(type, uuid, location);
    }

    public void setTargetTeleporter(TeleporterBlockEntity teleporter) {
        this.removeTargetTeleporterEntity();
        this.targetTeleporter = teleporter;
        if (this.targetTeleporter != null) {
            this.summonTargetTeleporterEntity();
        }
    }

    public void removeTargetTeleporterEntity() {
        if (this.targetTeleporterEntity == null) return;
        Entity entity = Bukkit.getEntity(this.targetTeleporterEntity);
        this.entityStructure.remove(this.targetTeleporterEntity);
        if (entity != null) entity.remove();
        this.targetTeleporterEntity = null;
    }

    public void summonTargetTeleporterEntity() {
        World world = this.location.getWorld();
        world.spawn(this.getCenterLocation().add(0, 0.5, 0), TextDisplay.class, td -> {
            td.text(Component.text(this.targetTeleporter.getTeleporterName()).color(RedstoneUtilities.PRIMARY_COLOR));
            td.setBillboard(Display.Billboard.CENTER);
            BlockEntityTypeRegistry.setType(td, TARGET_TELEPORTER_ENTITY_NAME);
            this.targetTeleporterEntity = td.getUniqueId();
            this.entityStructure.add(td.getUniqueId());
        });
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        TeleporterGUI autoCrafterGUI = new TeleporterGUI(event.getPlayer(), this);
        autoCrafterGUI.register();
        autoCrafterGUI.setup();
        autoCrafterGUI.show();
    }

    /**
     * Returns all Teleports which have this teleporter as a target
     */
    public List<TeleporterBlockEntity> getSelfTargetTeleporters() {
        BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
        List<TeleporterBlockEntity> teleporters = blockEntityRegistry.get(TeleporterBlockEntity.class);
        return teleporters.stream().filter(teleporter -> this.equals(teleporter.getTargetTeleporter())).toList();
    }

    @Override
    public void summon(Location location) {
        World world = location.getWorld();
        Location interactionLocation = location.clone().add(0.5, 0, 0.5);
        Location enderPearlLocation = location.clone().add(0.53, 0.05, 0.53);
        Location ironBlockCenterLoc = location.clone().add(0.5, 0, 0.5);

        this.uuid = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(0.9F);
            i.setInteractionHeight(0.1F);
        }).getUniqueId();
        world.spawn(enderPearlLocation, ItemDisplay.class, bd -> {
            bd.setItemStack(new ItemStack(Material.ENDER_PEARL));
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f((float) (Math.PI/2),1,0,0), new Vector3f(1.0F, 1.0F, 1.0F), new AxisAngle4f()));
            bd.setBrightness(new Display.Brightness(15, 15));
            this.entityStructure.add(bd.getUniqueId());
        });
        Util.summonCircleItemDisplays(ironBlockCenterLoc, 4, new Vector(0.405, 0, 0.405), new ItemStack(Material.IRON_BLOCK), 0.2F, (id, rot) ->
                this.entityStructure.add(id.getUniqueId()));
    }

    @Override
    public void load() {
        super.load();
        this.loadUpgrades();
        UUID targetTeleporterUUID = PDCUUIDListUtil.getUUID(this.getInteraction(), TARGET_TELEPORTER_KEY);
        if (targetTeleporterUUID != null) {
            Entity entity = Bukkit.getEntity(targetTeleporterUUID);
            if (entity instanceof Interaction interaction) {
                BlockEntityRegistry blockEntityRegistry = RedstoneUtilities.getInstance().getBlockEntityRegistry();
                this.targetTeleporter = blockEntityRegistry.get(interaction, TeleporterBlockEntity.class, TeleporterBlockEntityType.class);
            }
        }
        PersistentDataContainer pdc = this.getInteraction().getPersistentDataContainer();
        String teleporterName = pdc.get(TELEPORTER_NAME_KEY, PersistentDataType.STRING);
        if (teleporterName != null) {
            this.teleporterName = teleporterName;
        }
        UUID targetNameEntity = EntityStructureUtil.getEntityUUID(this.getInteraction(), TARGET_TELEPORTER_ENTITY_NAME);
        this.setTargetTeleporterEntity(targetNameEntity);
    }

    @Override
    public void store() {
        super.store();
        this.storeUpgrades();
        if (this.targetTeleporter != null) {
            PDCUUIDListUtil.setUUID(this.getInteraction(), this.targetTeleporter.getUuid(), TARGET_TELEPORTER_KEY);
        }
        PersistentDataContainer pdc = this.getInteraction().getPersistentDataContainer();
        pdc.set(TELEPORTER_NAME_KEY, PersistentDataType.STRING, this.teleporterName);
    }

    @Override
    public Class<TeleporterBlockEntity> getTargetType() {
        return TeleporterBlockEntity.class;
    }

    @Override
    public void onConnect(Player player, Connectable<TeleporterBlockEntity, TeleporterBlockEntity> connected) {
        this.setTargetTeleporter((TeleporterBlockEntity) connected);
    }

    @Override
    public void onDisconnect(Player player, Connectable<TeleporterBlockEntity, TeleporterBlockEntity> disconnected) {
        this.setTargetTeleporter(null);
    }

    @Override
    public boolean canConnect(Player player, Connectable<TeleporterBlockEntity, TeleporterBlockEntity> connectable) {
        TeleporterBlockEntity teleporterBlockEntity = (TeleporterBlockEntity) connectable;
        double distance = connectable.getCenterLocation().distance(this.getCenterLocation());
        int effLevelConnectable = teleporterBlockEntity.getUpgradeLevel(Upgrade.EFFICIENCY, 0);
        int effLevelSelf = this.getUpgradeLevel(Upgrade.EFFICIENCY, 0);
        int maxDistanceConnectable = getMaxDistance(effLevelConnectable);
        int maxDistanceSelf = getMaxDistance(effLevelSelf);
        int maxLevel = Upgrade.EFFICIENCY.getMaxLevel();
        if ((distance > maxDistanceConnectable || distance > maxDistanceSelf) &&
                (effLevelConnectable != maxLevel || effLevelSelf != maxLevel)) {
            player.sendMessage(Messages.TELEPORTER_TOO_FAR_AWAY);
            return false;
        }
        return !connectable.equals(this);
    }

    private static int getMaxDistance(int level) {
        return level * 30 + 30;
    }

    @Override
    public Material getCostType() {
        return null;
    }

    @Override
    public void remove(Player player, boolean drop) {
        this.dropUpgradeItems();
        super.remove(player, drop);
        for (TeleporterBlockEntity teleporter : this.getSelfTargetTeleporters()) {
            teleporter.setTargetTeleporter(null);
        }
    }

    @Override
    public String toString() {
        return "TeleporterBlockEntity{" +
                "targetTeleporter=" + (targetTeleporter != null ? targetTeleporter.getUuid() : "") +
                ", uuid=" + uuid +
                '}';
    }

    @Override
    public void beforeUpgrade(Upgrade upgrade, Integer level) {

    }

    @Override
    public void afterUpgrade(Upgrade upgrade, Integer level) {

    }

    @Override
    public List<Upgrade> getAllowedUpgrades() {
        return List.of(Upgrade.SPEED, Upgrade.EFFICIENCY);
    }
}
