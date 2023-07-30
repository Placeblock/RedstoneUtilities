package de.placeblock.redstoneutilities.filter;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.BlockEntityTypeRegistry;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.*;
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
import java.util.UUID;

@Getter
@Setter
public class FilterBlockEntity extends BlockEntity<FilterBlockEntity, FilterBlockEntityType> {
    public static final String FILTER_ENTITY_NAME = "filter_type";
    public static final NamespacedKey FILTER_ITEM_META = new NamespacedKey(RedstoneUtilities.getInstance(), "filter_item_metaa");
    public static final Vector FILTER_ENTITY_VEC = new Vector(0.26, 0, 0);


    private ItemStack filter;
    private List<UUID> filterEntities = new ArrayList<>();
    private Hopper hopper;

    public FilterBlockEntity(BlockEntityType<FilterBlockEntity, FilterBlockEntityType> type, UUID uuid) {
        super(type, uuid);
    }

    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        this.setFilter(item.clone());
    }

    private void setFilter(ItemStack item) {
        this.filter = item;
        this.removeFilterEntities();
        if (this.filter != null) {
            this.summonFilterEntities();
        }
    }

    private void removeFilterEntities() {
        Util.removeEntities(this.filterEntities);
        this.entityStructure.removeAll(this.filterEntities);
        this.filterEntities.clear();
    }
    private void summonFilterEntities() {
        Util.summonCircleItemDisplays(this.getCenterLocation(), 4, FILTER_ENTITY_VEC, this.filter, 0.2F, (id, rot) -> {
            BlockEntityTypeRegistry.setType(id, FILTER_ENTITY_NAME);
            this.filterEntities.add(id.getUniqueId());
            this.entityStructure.add(id.getUniqueId());
        });
    }

    @Override
    public void summon(Location location) {
        Location blockDisplayLoc = location.toBlockLocation().add(0.24, 0.24, 0.24);
        Location interactionLoc = location.toCenterLocation().add(0, -0.3, 0);
        World world = location.getWorld();
        this.uuid = world.spawn(interactionLoc, Interaction.class, i -> {
            i.setInteractionWidth(0.55F);
            i.setInteractionHeight(0.55F);
        }).getUniqueId();
        world.spawn(blockDisplayLoc, BlockDisplay.class, bd -> {
            bd.setBlock(Material.IRON_BLOCK.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.52F, 0.52F, 0.52F), new AxisAngle4f()));
            bd.setBrightness(new Display.Brightness(15, 15));
            this.entityStructure.add(bd.getUniqueId());
        });
    }

    @Override
    public void load() {
        super.load();
        Interaction interaction = this.getInteraction();
        List<UUID> filterEntities = EntityStructureUtil.getEntityUUIDs(interaction, FILTER_ENTITY_NAME);
        this.setFilterEntities(filterEntities);
        Block block = interaction.getLocation().getBlock();
        this.hopper = (Hopper) block.getState();

        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String serializedFilter = pdc.get(FILTER_ITEM_META, PersistentDataType.STRING);
        if (serializedFilter != null) {
            Server server = RedstoneUtilities.getInstance().getServer();
            this.filter = server.getItemFactory().createItemStack(serializedFilter);
        }
    }

    @Override
    public void store() {
        super.store();
        if (this.filter != null) {
            String metaData = this.filter.getItemMeta().getAsString();
            String key = this.filter.getType().getKey().asString();
            String serialized = key + metaData;

            PersistentDataContainer pdc = this.getInteraction().getPersistentDataContainer();
            pdc.set(FILTER_ITEM_META, PersistentDataType.STRING, serialized);
        }
    }

    @Override
    public void disable() {

    }
}
