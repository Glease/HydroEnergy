package com.sinthoras.hydroenergy.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class HENumberInput extends GuiTextField {
    public HENumberInput(FontRenderer fontRenderer, int pixelX, int pixelY, int pixelWidth, int pixelHeight) {
        super(fontRenderer, pixelX, pixelY, pixelWidth, pixelHeight);
    }

    public int minValue = Integer.MIN_VALUE;
    public int maxValue = Integer.MAX_VALUE;

    @Override
    public void writeText(final String newText) {
        String oldText = getText();
        super.writeText(newText);
        try {
            int value = Integer.parseInt(getText());
            if(value != parseValue(value)) {
                setText(oldText);
            }
        }
        catch(NumberFormatException e) {
            setText(oldText);
        }
    }

    public void add(int offset) {
        try {
            int value = Integer.parseInt(getText());
            value = parseValue(value + offset);
            setText("" + value);
        }
        catch(Exception e) {}
    }

    public int parseValue(int value) {
        return Math.min(Math.max(value, minValue), maxValue);
    }
}
