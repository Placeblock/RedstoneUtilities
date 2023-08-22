package de.placeblock.redstoneutilities.impl.wireless.infometer;

import de.placeblock.betterinventories.content.item.ClickData;
import de.placeblock.betterinventories.content.item.GUIButton;
import de.placeblock.betterinventories.gui.GUI;
import de.placeblock.betterinventories.util.ItemBuilder;
import de.placeblock.redstoneutilities.Powerable;
import de.placeblock.redstoneutilities.Util;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TogglePowerGUIButton extends GUIButton {
    private static final List<TextComponent> LORE = List.of(
            Util.inferior("Shift-Click zum"),
            Util.primary("genauen steuern")
    );
    private final Powerable powerable;

    private static ItemStack getItemStack(Powerable powerable) {
        int power = powerable.getPower();
        Material mat = power == 0 ? Material.RED_WOOL : Material.GREEN_WOOL;
        return new ItemBuilder(Util.primary("Aktuell: " + power), mat)
                .lore(LORE)
                .build();
    }

    public TogglePowerGUIButton(GUI gui, Powerable powerable) {
        super(gui, getItemStack(powerable));
        this.powerable = powerable;
    }

    @Override
    public void onClick(ClickData data) {
        boolean on = this.powerable.getPower() > 0;
        this.powerable.setPower(on ? 0 : 15);
        this.getGui().update();
    }

    @Override
    public void onShiftLeftClick(ClickData data) {
        int power = this.powerable.getPower();
        power = Math.max(0, power-1);
        this.powerable.setPower(power);
        this.getGui().update();
    }

    @Override
    public void onShiftRightClick(ClickData data) {
        int power = this.powerable.getPower();
        power = Math.min(15, power+1);
        this.powerable.setPower(power);
        this.getGui().update();
    }
}
