package com.sinthoras.hydroenergy.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class HENumberInput extends GuiTextField {
    public HENumberInput(FontRenderer fontRenderer, int pixelX, int pixelY, int pixelWidth, int pixelHeight) {
        super(fontRenderer, pixelX, pixelY, pixelWidth, pixelHeight);
    }

    @Override
    public void writeText(final String newText) {
        String oldText = getText();
        super.writeText(newText);
        try {
            Integer.parseInt(getText());
        }
        catch(NumberFormatException e) {
            setText(oldText);
        }
    }
}
