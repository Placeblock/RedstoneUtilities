package de.placeblock.redstoneutilities;
import de.placeblock.redstoneutilities.blockentity.*;
import de.placeblock.redstoneutilities.wireless.Wireless;
import de.placeblock.redstoneutilities.wireless.receiver.ReceiverBlockEntityType;
import de.placeblock.redstoneutilities.wireless.sender.SenderBlockEntityType;
import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
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
    private Wireless wireless;

    @Override
    public void onEnable() {
        RedstoneUtilities.instance = this;

        this.blockEntityListener = new BlockEntityListener(this);
        this.blockEntityTypeRegistry = new BlockEntityTypeRegistry();
        this.blockEntityRegistry = new BlockEntityRegistry();
        this.wireless = new Wireless();
        this.wireless.setup(this);
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(this.blockEntityListener, this);
        this.textInputHandler = new TextInputHandler();
        pluginManager.registerEvents(this.textInputHandler, this);

        this.blockEntityTypeRegistry.register(new ReceiverBlockEntityType(this, "WIRELESS_RECEIVER"));
        this.blockEntityTypeRegistry.register(new SenderBlockEntityType(this, "WIRELESS_SENDER"));
    }

    @Override
    public void onDisable() {
        for (BlockEntityType<?> blockEntityType : this.blockEntityTypeRegistry.getBlockEntityTypes().values()) {
            blockEntityType.disable();
        }
    }

}