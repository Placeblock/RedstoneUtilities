package de.placeblock.redstoneutilities.autocrafting;

import de.placeblock.redstoneutilities.RedstoneUtilities;
import de.placeblock.redstoneutilities.blockentity.BlockEntity;
import de.placeblock.redstoneutilities.blockentity.BlockEntityType;
import de.placeblock.redstoneutilities.blockentity.BlockEntityTypeRegistry;
import de.placeblock.redstoneutilities.blockentity.EntityStructureUtil;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Dropper;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

@Setter
public class AutoCrafterBlockEntity extends BlockEntity<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> {
    private static final String RECIPE_ENTITY_NAME = "AUTO_CRAFTER_RECIPE";
    private static final NamespacedKey RECIPE_KEY_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "recipe_key");
    private static final NamespacedKey RECIPE_NAMESPACE_KEY = new NamespacedKey(RedstoneUtilities.getInstance(), "recipe_namespace");
    public static Vector CORNERS_VEC = new Vector(0.44, 0.44, 0.44);
    private Recipe recipe;
    private List<ItemDisplay> recipeEntities = new ArrayList<>();
    private Dropper dropper;

    public AutoCrafterBlockEntity(BlockEntityType<AutoCrafterBlockEntity, AutoCrafterBlockEntityType> type, Interaction interaction) {
        super(type, interaction);
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

        Vector relVec = this.calculateRecipeEntityVec(i);
        Location itemDisplayLoc = centerLoc.clone().add(relVec);

        world.spawn(itemDisplayLoc, ItemDisplay.class, id -> {
            assert material != null;
            id.setItemStack(new ItemStack(material));
            id.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f((float) (Math.PI/2F), 1, 0 ,0), new Vector3f(0.1F, 0.1F, 0.1F), new AxisAngle4f()));
            BlockEntityTypeRegistry.setType(id, RECIPE_ENTITY_NAME);
            this.recipeEntities.add(id);
            this.entityStructure.add(id);
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
        for (ItemDisplay recipeEntity : this.recipeEntities) {
            recipeEntity.remove();
        }
        this.recipeEntities.clear();
    }

    private NamespacedKey getRecipeKey() {
        if (this.recipe instanceof Keyed keyed) {
            return keyed.getKey();
        }
        return null;
    }

    public boolean canCraft() {
        if (this.recipe == null) return false;
        Inventory inventory = dropper.getInventory();
        if (this.recipe instanceof ShapedRecipe shapedRecipe) {
            for (int i = 0; i < 9; i++) {
                RecipeChoice choice = this.getChoice(shapedRecipe, i);
                ItemStack item = inventory.getItem(i);
                if ((choice != null && item == null) ||
                    (choice == null || !choice.test(item))) return false;
            }
        } else if (this.recipe instanceof ShapelessRecipe shapelessRecipe) {
            List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
            for (RecipeChoice choice : shapelessRecipe.getChoiceList()) {
                Optional<ItemStack> matching = items.stream()
                        .filter(choice)
                        .findFirst();
                if (matching.isEmpty()) return false;
                items.remove(matching.get());
            }
        }
        return true;
    }

    public int findBestSlot(ItemStack item) {
        if (this.recipe instanceof ShapedRecipe) {
            List<Integer> possibleSlots = this.getPossibleSlotsShaped(item);
            Integer bestSlot = this.getBestSlot(possibleSlots);
            if (bestSlot != null) return bestSlot;
        } else if (this.recipe instanceof ShapelessRecipe) {
            if (!this.hasPossibleSlot(item)) return -1;
            List<RecipeChoice> missingChoicesShapeless = this.getMissingChoicesShapeless();
            for (RecipeChoice choice : missingChoicesShapeless) {
                if (choice.test(item)) return this.getEmptySlot();
            }
            List<Integer> existingSlots = this.getExistingSlotsShapeless(item);
            Integer bestSlot = this.getBestSlot(existingSlots);
            if (bestSlot != null) return bestSlot;
        }
        return -1;
    }

    private boolean hasPossibleSlot(ItemStack item) {
        for (RecipeChoice choice : ((ShapelessRecipe) this.recipe).getChoiceList()) {
            if (choice.test(item)) return true;
        }
        return false;
    }

    private int getEmptySlot() {
        Inventory inventory = this.dropper.getInventory();
        for (int i = 0; i < inventory.getContents().length; i++) {
            if (inventory.getContents()[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private List<RecipeChoice> getMissingChoicesShapeless() {
        Inventory inventory = this.dropper.getInventory();
        List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
        ShapelessRecipe shapelessRecipe = (ShapelessRecipe) this.recipe;
        List<RecipeChoice> missingChoices = new ArrayList<>();
        for (RecipeChoice choice : shapelessRecipe.getChoiceList()) {
            Optional<ItemStack> matching = items.stream()
                    .filter(choice)
                    .findFirst();
            if (matching.isEmpty()) {
                missingChoices.add(choice);
            }
        }
        return missingChoices;
    }

    private Integer getBestSlot(List<Integer> possibleSlots) {
        if (possibleSlots.size() == 0) return null;
        Inventory inventory = this.dropper.getInventory();
        int bestSlot = -1;
        int bestSlotAmount = -1;
        for (Integer possibleSlot : possibleSlots) {
            ItemStack invitem = inventory.getItem(possibleSlot);
            int slotAmount = invitem == null ? 0 : invitem.getAmount();
            if (slotAmount > bestSlotAmount) {
                bestSlot = possibleSlot;
                bestSlotAmount = slotAmount;
            }
        }
        return bestSlot;
    }

    private List<Integer> getExistingSlotsShapeless(ItemStack item) {
        Inventory inventory = this.dropper.getInventory();
        List<Integer> existingSlots = new ArrayList<>();
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack invitem = inventory.getContents()[i];
            if (!item.isSimilar(invitem)) continue;
            existingSlots.add(i);
        }
        return existingSlots;
    }

    private List<Integer> getPossibleSlotsShaped(ItemStack item) {
        Inventory inventory = this.dropper.getInventory();
        ShapedRecipe shapedRecipe = (ShapedRecipe) this.recipe;
        List<Integer> possibleSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            RecipeChoice choice = this.getChoice(shapedRecipe, i);
            ItemStack invitem = inventory.getItem(i);
            if (choice == null) continue;
            if (((invitem == null) || choice.test(invitem)) &&
                choice.test(item)) {
                possibleSlots.add(i);
            }
        }
        return possibleSlots;
    }

    private RecipeChoice getChoice(ShapedRecipe shapedRecipe, int index) {
        int rowi = index/3;
        String row = shapedRecipe.getShape()[rowi];
        char character = row.charAt(index%3);
        return shapedRecipe.getChoiceMap().get(character);
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

        this.interaction = world.spawn(interactionLocation, Interaction.class, i -> {
            i.setInteractionWidth(1.011F);
            i.setInteractionHeight(0.05F);
        });
        world.spawn(craftingDisplayLoc, BlockDisplay.class, bd -> {
            bd.setBlock(Material.CRAFTING_TABLE.createBlockData());
            bd.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1.01F, 1.01F, 1.01F), new AxisAngle4f()));
            this.entityStructure.add(bd);
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
                this.entityStructure.add(id);
            });
        }
    }

    @Override
    public void load() {
        super.load();
        Interaction interaction = this.getInteraction();
        List<Entity> entities = EntityStructureUtil.getEntities(interaction, RECIPE_ENTITY_NAME);
        if (entities != null) {
            List<ItemDisplay> itemDisplays = new ArrayList<>();
            for (Entity entity : entities) {
                itemDisplays.add((ItemDisplay) entity);
            }
            this.setRecipeEntities(itemDisplays);
        }
        PersistentDataContainer pdc = this.interaction.getPersistentDataContainer();
        String recipeNamespace = pdc.get(RECIPE_NAMESPACE_KEY, PersistentDataType.STRING);
        String recipeKey = pdc.get(RECIPE_KEY_KEY, PersistentDataType.STRING);
        if (recipeNamespace != null && recipeKey != null) {
            NamespacedKey recipeNamespacedKey = new NamespacedKey(recipeNamespace, recipeKey);
            this.recipe = Bukkit.getRecipe(recipeNamespacedKey);
        }

        this.dropper = ((Dropper) this.getBlockLocation().getBlock().getState());
    }

    @Override
    public void store() {
        super.store();
        NamespacedKey recipeKey = this.getRecipeKey();
        if (recipeKey != null) {
            PersistentDataContainer pdc = this.interaction.getPersistentDataContainer();
            pdc.set(RECIPE_NAMESPACE_KEY, PersistentDataType.STRING, recipeKey.getNamespace());
            pdc.set(RECIPE_KEY_KEY, PersistentDataType.STRING, recipeKey.getKey());
        }
    }
}
