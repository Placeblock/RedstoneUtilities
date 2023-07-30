package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.Util;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.BlockEntityTypeRegistry;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import de.placeblock.redstoneutilities.upgrades.Upgradeable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftShapedRecipe;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

@Setter
@Getter
public class AutoCrafterBlockEntity extends BlockEntity<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> implements Upgradeable {
    private static final String RECIPE_ENTITY_NAME = "AUTO_CRAFTER_RECIPE";
    private static final NamespacedKey RECIPE_KEY_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "recipe_key");
    private static final NamespacedKey RECIPE_NAMESPACE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "recipe_namespace");
    public static Vector CORNERS_VEC = new Vector(0.44, 0.44, 0.44);
    private Recipe recipe;
    private List<UUID> recipeEntities = new ArrayList<>();
    private Dropper dropper;
    private BukkitTask craftScheduler;
    Map<Upgrade, Integer> upgrades = new HashMap<>();

    public AutoCrafterBlockEntity(BlockEntityType<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> type, UUID uuid) {
        super(type, uuid);
    }

    public void setRecipe(Recipe recipe) {
        if (!(recipe instanceof ShapedRecipe) &&
            !(recipe instanceof ShapelessRecipe)) {
            throw new IllegalArgumentException("Recipe has to be a craftingtable recipe");
        }
        this.recipe = recipe;
        this.removeRecipeEntities();
        this.summonRecipeEntities();
    }

    private void summonRecipeEntities() {
        Location centerLoc = this.getCenterLocation().add(0, 0.51, 0);
        if (this.recipe instanceof ShapedRecipe shapedRecipe) {
            String[] shape = shapedRecipe.getShape();
            Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
            for (int rowindex = 0; rowindex < shape.length; rowindex++) {
                String row = shape[rowindex];
                for (int columnindex = 0; columnindex < row.length(); columnindex++) {
                    char character = row.charAt(columnindex);
                    int i = rowindex*3 + columnindex;
                    RecipeChoice choice = choiceMap.get(character);
                    if (choice == null) continue;
                    this.summonRecipeEntity(centerLoc, i, choice);
                }
            }
        } else if (this.recipe instanceof ShapelessRecipe shapelessRecipe) {
            List<RecipeChoice> choiceList = shapelessRecipe.getChoiceList();
            for (int i = 0; i < choiceList.size(); i++) {
                RecipeChoice choice = choiceList.get(i);
                this.summonRecipeEntity(centerLoc, i, choice);
            }
        }
    }

    private void summonRecipeEntity(Location centerLoc, int i, RecipeChoice recipeChoice) {
        World world = centerLoc.getWorld();
        Material material = this.getRecipeChoiceMaterial(recipeChoice);
        assert material != null;

        Vector relVec = this.calculateRecipeEntityVec(i);
        Location itemDisplayLoc = centerLoc.clone().add(relVec);

        world.spawn(itemDisplayLoc, ItemDisplay.class, id -> {
            id.setItemStack(new ItemStack(material));
            id.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f((float) (Math.PI/2F), 1, 0 ,0), new Vector3f(0.1F, 0.1F, 0.1F), new AxisAngle4f()));
            BlockEntityTypeRegistry.setType(id, RECIPE_ENTITY_NAME);
            id.setBrightness(new Display.Brightness(15, 15));
            this.recipeEntities.add(id.getUniqueId());
            this.entityStructure.add(id.getUniqueId());
        });
    }

    @NotNull
    private Vector calculateRecipeEntityVec(int i) {
        /*0 -> -1, 1
        1 ->  0, 1
        2 ->  1, 1
        3 -> -1, 0
        4 ->  0, 0
        5 ->  1, 0
        6 -> -1, -1
        7 ->  0, -1
        8 ->  1, -1*/
        return new Vector(-1 + (i % 3), 0, 1 - i / 3).multiply(0.19);
    }

    private Material getRecipeChoiceMaterial(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return exactChoice.getItemStack().getType();
        } else if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return materialChoice.getItemStack().getType();
        }
        return null;
    }

    private void removeRecipeEntities() {
        Util.removeEntities(this.recipeEntities);
        this.entityStructure.removeAll(this.recipeEntities);
        this.recipeEntities.clear();
    }

    private NamespacedKey getRecipeKey() {
        if (this.recipe instanceof Keyed keyed) {
            return keyed.getKey();
        }
        return null;
    }

    private void craftCycle() {
        Integer efficiencyLevel = this.getUpgradeLevel(Upgrade.EFFICIENCY, 0);
        for (int i = 0; i < efficiencyLevel + 1; i++) {
            if (this.canCraft()) {
                List<Material> removedItems = this.removeItemsForRecipe();
                this.craftItem(removedItems);
            }
        }
        Integer speedLevel = this.getUpgradeLevel(Upgrade.SPEED, 0);
        int speedMaxLevel = Upgrade.SPEED.getMaxLevel();
        int delay = ((speedMaxLevel - speedLevel) * 33) + 4;
        this.craftScheduler = Bukkit.getScheduler().runTaskLater(RedstoneUtilities.getInstance(), this::craftCycle, delay);
    }

    private boolean canCraft() {
        if (this.recipe == null ||
            !this.dropper.getChunk().isLoaded()) return false;
        Block belowBlock = this.dropper.getBlock().getRelative(BlockFace.DOWN);
        if (!(belowBlock.getState() instanceof Hopper container)
            || !RecipeUtil.canAddItem(container.getInventory(), this.recipe)) return false;
        Inventory inventory = this.dropper.getInventory();
        if (this.recipe instanceof ShapedRecipe shapedRecipe) {
            for (int i = 0; i < 9; i++) {
                RecipeChoice choice = RecipeUtil.getChoice(shapedRecipe, i);
                ItemStack item = inventory.getItem(i);
                if (choice != null &&
                        (item == null || !choice.test(item))) return false;
            }
        } else if (this.recipe instanceof ShapelessRecipe shapelessRecipe) {
            List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
            for (RecipeChoice choice : shapelessRecipe.getChoiceList()) {
                Optional<ItemStack> matching = items.stream()
                        .filter(Objects::nonNull)
                        .filter(choice)
                        .findFirst();
                if (matching.isEmpty()) return false;
                items.remove(matching.get());
            }
        }
        return true;
    }

    private List<Material> removeItemsForRecipe() {
        List<Material> removedItems = new ArrayList<>();
        Inventory inventory = this.dropper.getInventory();
        inventory.getItem(0);
        if (this.recipe instanceof ShapedRecipe shapedRecipe) {
            for (int i = 0; i < 9; i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) continue;
                RecipeChoice choice = RecipeUtil.getChoice(shapedRecipe, i);
                if (choice == null) continue;
                if (choice.test(item)) {
                    removedItems.add(item.getType());
                    item.setAmount(item.getAmount()-1);
                    inventory.setItem(i, item);
                }
            }
        } else if (this.recipe instanceof ShapelessRecipe shapelessRecipe) {
            Map<Integer, ItemStack> items = new HashMap<>();
            for (int i = 0; i < inventory.getContents().length; i++) {
                ItemStack itemStack = inventory.getContents()[i];
                if (itemStack != null) {
                    items.put(i, itemStack);
                }
            }
            List<RecipeChoice> choiceList = shapelessRecipe.getChoiceList();
            for (RecipeChoice recipeChoice : shapelessRecipe.getChoiceList()) {
                Map.Entry<Integer, ItemStack> matching = null;
                for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                    if (recipeChoice.test(entry.getValue())) {
                        matching = entry;
                        choiceList.remove(recipeChoice);
                    }
                }
                if (matching != null) {
                    Integer index = matching.getKey();
                    items.remove(index);
                    ItemStack item = inventory.getItem(index);
                    assert item != null;
                    removedItems.add(item.getType());
                    item.setAmount(item.getAmount()-1);
                }
            }
        }
        return removedItems;
    }

    private void craftItem(List<Material> removedItems) {
        Block bottomBlock = this.dropper.getBlock().getRelative(BlockFace.DOWN);
        if (bottomBlock.getState() instanceof Container container) {
            Inventory inventory = container.getInventory();
            for (Material removedItem : removedItems) {
                Material remaining = removedItem.getCraftingRemainingItem();
                if (remaining != null) {
                    inventory.addItem(new ItemStack(remaining));
                }
            }
            inventory.addItem(this.recipe.getResult());
        }
    }


    @Override
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().isSneaking()) return;
        AutoCrafterGUI autoCrafterGUI = new AutoCrafterGUI(event.getPlayer(), this);
        autoCrafterGUI.register();
        autoCrafterGUI.setup();
        autoCrafterGUI.show();
    }

    @Override
    public void summon(Location location) {
        World world = location.getWorld();

        Location interactionLocation = location.clone().add(0.5, -0.025, 0.5);
        Location craftingDisplayLoc = location.clone().add(-0.005, -1.005, -0.005);
        Location outputDisplayLoc = location.clone().add(0.25, -1.01, 0.25);

        this.uuid = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(1.011F);
            i.setInteractionHeight(0.05F);
        }).getUniqueId();
        world.spawn(craftingDisplayLoc, BlockDisplay.class, bd -> {
            bd.setBlock(Material.CRAFTING_TABLE.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.01F, 1.01F, 1.01F), new AxisAngle4f()));
            bd.setBrightness(new Display.Brightness(15, 15));
            this.entityStructure.add(bd.getUniqueId());
        });
        world.spawn(outputDisplayLoc, BlockDisplay.class, bd -> {
            org.bukkit.block.data.type.Dispenser dispenser = (org.bukkit.block.data.type.Dispenser) Material.DROPPER.createBlockData();
            dispenser.setFacing(BlockFace.DOWN);
            bd.setBlock(dispenser);
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.5F, 0.01F, 0.5F), new AxisAngle4f()));
            bd.setBrightness(new Display.Brightness(15, 15));
            this.entityStructure.add(bd.getUniqueId());
        });
        Location craftingCenterLoc = location.clone().add(0.5, -0.5, 0.5);
        this.summonIronBlocks(world, craftingCenterLoc, CORNERS_VEC);
        this.summonIronBlocks(world, craftingCenterLoc, CORNERS_VEC.multiply(-1));
    }

    private void summonIronBlocks(World world, Location craftingCenterLoc, Vector cornersVec) {
        for (int i = 0; i < 4; i++) {
            Vector ironBlockVec = cornersVec.clone().rotateAroundY(i*(Math.PI/2));
            Location ironBlockLoc = craftingCenterLoc.clone().add(ironBlockVec).add(-0.1F, -0.1F, -0.1F);
            world.spawn(ironBlockLoc, BlockDisplay.class, id -> {
                id.setBlock(Material.IRON_BLOCK.createBlockData());
                id.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.2F, 0.2F, 0.2F), new AxisAngle4f()));
                id.setBrightness(new Display.Brightness(15, 15));
                this.entityStructure.add(id.getUniqueId());
            });
        }
    }

    @Override
    public void load() {
        super.load();
        this.loadUpgrades();
        Interaction interaction = this.getInteraction();
        List<UUID> recipeEntities = EntityStructureUtil.getEntityUUIDs(interaction, RECIPE_ENTITY_NAME);
        this.setRecipeEntities(recipeEntities);
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String recipeNamespace = pdc.get(RECIPE_NAMESPACE_KEY, PersistentDataType.STRING);
        String recipeKey = pdc.get(RECIPE_KEY_KEY, PersistentDataType.STRING);
        if (recipeNamespace != null && recipeKey != null) {
            NamespacedKey recipeNamespacedKey = new NamespacedKey(recipeNamespace, recipeKey);
            this.recipe = Bukkit.getRecipe(recipeNamespacedKey);
        }
        Block block = this.getBlockLocation().getBlock();
        if (block.getState() instanceof Dropper adropper) {
            this.dropper = adropper;
        }

        Bukkit.getScheduler().runTaskLater(RedstoneUtilities.getInstance(), this::craftCycle, 20);
    }

    @Override
    public void store() {
        super.store();
        this.storeUpgrades();
        NamespacedKey recipeKey = this.getRecipeKey();
        if (recipeKey != null) {
            PersistentDataContainer pdc = this.getInteraction().getPersistentDataContainer();
            pdc.set(RECIPE_NAMESPACE_KEY, PersistentDataType.STRING, recipeKey.getNamespace());
            pdc.set(RECIPE_KEY_KEY, PersistentDataType.STRING, recipeKey.getKey());
        }
    }

    @Override
    public void disable() {
        if (this.craftScheduler != null) {
            this.craftScheduler.cancel();
        }
    }
    @Override
    public void remove(Player player, boolean drop) {
        this.dropUpgradeItems();
        super.remove(player, drop);
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
