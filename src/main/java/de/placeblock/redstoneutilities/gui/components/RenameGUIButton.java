package de.placeblock.redstoneutilities.gui.components;

import de.placeblock.betterinventories.content.item.ClickData;
import de.placeblock.betterinventories.content.item.GUIButton;
import de.placeblock.betterinventories.gui.GUI;
import de.placeblock.betterinventories.gui.impl.textinput.TextInputGUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.Messages;
import de.placeblock.redstoneutilities.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class RenameGUIButton extends GUIButton {
    public static final ItemStack CHANGE_NAME_ITEM = new ItemBuilder(Util.primary("Umbenennen"), Material.PAPER).build();

    private final BiConsumer<Player, String> consumer;

    public RenameGUIButton(GUI gui, BiConsumer<Player, String> consumer) {
        super(gui, CHANGE_NAME_ITEM);
        this.consumer = consumer;
    }

    @Override
    public void onClick(ClickData data) {
        Player player = data.getPlayer();
        TextInputGUI textInputGUI = new TextInputGUI(this.getGui().getPlugin(),
                Util.primary("Umbenennen"), player, "", ((finalText, abort) -> {
            if (abort) return;
            this.consumer.accept(player, finalText);
            player.sendMessage(Messages.RENAMED);
        }));
        textInputGUI.showPlayer(player);
    }
}
