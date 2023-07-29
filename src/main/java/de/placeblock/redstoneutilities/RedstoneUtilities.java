package de.placeblock.redstoneutilities;
import de.placeblock.redstoneutilities.autocrafting.AutoCrafterBlockEntityType;
import de.placeblock.redstoneutilities.autocrafting.AutoCraftingManager;
import de.placeblock.redstoneutilities.blockentity.*;
import de.placeblock.redstoneutilities.chunkloader.ChunkLoaderBlockEntityType;
import de.placeblock.redstoneutilities.chunkloader.ChunkLoaderManager;
import de.placeblock.redstoneutilities.filter.FilterManager;
import de.placeblock.redstoneutilities.filter.FilterBlockEntityType;
import de.placeblock.redstoneutilities.upgrades.Upgrade;
import de.placeblock.redstoneutilities.upgrades.UpgradeItems;
import de.placeblock.redstoneutilities.wireless.WirelessManager;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntityType;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntityType;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class RedstoneUtilities extends JavaPlugin {

    public static final TextColor PRIMARY_COLOR = TextColor.color(131, 95, 250);
    public static final TextColor INFERIOR_COLOR = TextColor.color(153, 153, 153);

    @Getter
    private static RedstoneUtilities instance;

    private BlockEntityListener blockEntityListener;

    private BlockEntityTypeRegistry blockEntityTypeRegistry;
    private BlockEntityRegistry blockEntityRegistry;
    private TextInputHandler textInputHandler;

    private WirelessManager wirelessManager;
    private AutoCraftingManager autoCraftingManager;
    private FilterManager filterManager;
    private ChunkLoaderManager chunkLoaderManager;

    @Override
    public void onEnable() {
        RedstoneUtilities.instance = this;

        this.blockEntityListener = new BlockEntityListener(this);
        this.blockEntityTypeRegistry = new BlockEntityTypeRegistry();
        this.blockEntityRegistry = new BlockEntityRegistry(this);

        this.wirelessManager = new WirelessManager();
        this.wirelessManager.setup(this);
        this.autoCraftingManager = new AutoCraftingManager();
        this.autoCraftingManager.setup(this);
        this.filterManager = new FilterManager();
        this.filterManager.setup(this);
        this.chunkLoaderManager = new ChunkLoaderManager();
        this.chunkLoaderManager.setup(this);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(this.blockEntityListener, this);
        this.textInputHandler = new TextInputHandler();
        pluginManager.registerEvents(this.textInputHandler, this);

        this.blockEntityTypeRegistry.register(new ReceiverBlockEntityType(this));
        this.blockEntityTypeRegistry.register(new SenderBlockEntityType(this));
        this.blockEntityTypeRegistry.register(new AutoCrafterBlockEntityType(this));
        this.blockEntityTypeRegistry.register(new FilterBlockEntityType(this));
        this.blockEntityTypeRegistry.register(new ChunkLoaderBlockEntityType(this));

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            this.getLogger().info("Storing Data in Entities");
            for (BlockEntity<?, ?> blockEntity : this.blockEntityRegistry.getBlockEntities().values()) {
                blockEntity.store();
            }
        }, 20*60*15, 20*60*15);

        for (Upgrade upgrade : Upgrade.values()) {
            UpgradeItems.registerRecipes(upgrade);
        }
    }

    @Override
    public void onDisable() {
        for (BlockEntityType<?, ?> blockEntityType : this.blockEntityTypeRegistry.getBlockEntityTypes().values()) {
            blockEntityType.disable();
        }
        for (BlockEntity<?, ?> blockEntity : this.blockEntityRegistry.getBlockEntities().values()) {
            blockEntity.disable();
            blockEntity.store();
        }
        this.wirelessManager.disable();
        this.autoCraftingManager.disable();
        this.filterManager.disable();
        this.chunkLoaderManager.disable();
    }

}