package com.sinthoras.hydroenergy.client.gui.widgets;

import com.sinthoras.hydroenergy.client.HEClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class HEWidgetModes extends Gui {

    private int waterId;
    private int pixelX;
    private int pixelY;

    private HEButtonTextured.Drain buttonDrain;
    private HEButtonTextured.Debug buttonDebug;
    private HEButtonTextured.Water buttonWater;
    private boolean isEnabled;

    public HEWidgetModes(int waterId, int pixelX, int pixelY) {
        this.waterId = waterId;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
    }

    public void init(List buttonList) {
        buttonDrain = new HEButtonTextured.Drain(0, pixelX, pixelY);
        buttonDebug = new HEButtonTextured.Debug(1, pixelX + 25, pixelY);
        buttonWater = new HEButtonTextured.Water(2, pixelX + 50, pixelY);

        buttonList.add(buttonDrain);
        buttonList.add(buttonDebug);
        buttonList.add(buttonWater);
    }

    public void actionPerformed(final GuiButton button)
    {
        if(button == buttonDrain) {
            HEClient.drainStates[waterId] = true;
            HEClient.debugStates[waterId] = true;
            HEClient.configRequest(waterId);
            buttonDrain.enabled = true;
            buttonDebug.enabled = false;
            buttonWater.enabled = false;
        }
        else if(button == buttonDebug) {
            HEClient.drainStates[waterId] = false;
            HEClient.debugStates[waterId] = true;
            HEClient.configRequest(waterId);
            buttonDrain.enabled = false;
            buttonDebug.enabled = true;
            buttonWater.enabled = false;
        }
        else if(button == buttonWater) {
            HEClient.drainStates[waterId] = false;
            HEClient.debugStates[waterId] = false;
            HEClient.configRequest(waterId);
            buttonDrain.enabled = false;
            buttonDebug.enabled = false;
            buttonWater.enabled = true;
        }
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;

        if(isEnabled) {
            buttonDrain.enabled = !HEClient.drainStates[waterId];
            buttonDebug.enabled = HEClient.drainStates[waterId] || !HEClient.debugStates[waterId];
            buttonWater.enabled = HEClient.drainStates[waterId] || HEClient.debugStates[waterId];
        }
        else {
            buttonDrain.enabled = false;
            buttonDebug.enabled = false;
            buttonWater.enabled = false;
        }

        buttonDrain.visible = isEnabled;
        buttonDebug.visible = isEnabled;
        buttonWater.visible = isEnabled;
    }

    // This draw call handles the button manually before the overlay is drawn if a overlay is active
    public void draw(Minecraft minecraft, int mouseX, int mouseY) {
        if(!isEnabled) {
            buttonDrain.visible = true;
            buttonDebug.visible = true;
            buttonWater.visible = true;

            buttonDrain.drawButton(minecraft, mouseX, mouseY);
            buttonDebug.drawButton(minecraft, mouseX, mouseY);
            buttonWater.drawButton(minecraft, mouseX, mouseY);

            buttonDrain.visible = false;
            buttonDebug.visible = false;
            buttonWater.visible = false;
        }
    }

    public void drawTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int buttonX, int buttonY, int screenWidth) {
        if(isEnabled) {
            if(buttonDrain.isOverButton(buttonX, buttonY)) {
                drawTooltip("Remove water from world", mouseX, mouseY, fontRenderer, screenWidth);
            }
            if(buttonDebug.isOverButton(buttonX, buttonY)) {
                drawTooltip("Spread and show all water", mouseX, mouseY, fontRenderer, screenWidth);
            }
            if(buttonWater.isOverButton(buttonX, buttonY)) {
                drawTooltip("Spread and show water", mouseX, mouseY, fontRenderer, screenWidth);
            }
        }
    }

    private void drawTooltip(String s, int mouseX, int mouseY, FontRenderer fontRenderer, int rightMostPosition) {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        mouseX += 12;
        mouseY += 12;
        int stringWidth = fontRenderer.getStringWidth(s);

        // Make sure the end does not leave the image
        mouseX -= Math.max(0, mouseX + stringWidth + 3 - rightMostPosition);

        int color = 0xf0100010;
        this.drawGradientRect(mouseX - 3, mouseY - 4, mouseX + stringWidth + 3, mouseY - 3, color, color);
        this.drawGradientRect(mouseX - 3, mouseY + 11, mouseX + stringWidth + 3, mouseY + 12, color, color);
        this.drawGradientRect(mouseX - 3, mouseY - 3, mouseX + stringWidth + 3, mouseY + 11, color, color);
        this.drawGradientRect(mouseX - 4, mouseY - 3, mouseX - 3, mouseY + 11, color, color);
        this.drawGradientRect(mouseX + stringWidth + 3, mouseY - 3, mouseX + stringWidth + 4, mouseY + 11, color, color);
        int color1 = 0x505000ff;
        int color2 = (color1 & 0xfefefe) >> 1 | color1 & 0xff000000 ;
        this.drawGradientRect(mouseX - 3, mouseY - 2, mouseX - 2, mouseY + 10, color1, color2);
        this.drawGradientRect(mouseX + stringWidth + 2, mouseY - 2, mouseX + stringWidth + 3, mouseY + 10, color1, color2);
        this.drawGradientRect(mouseX - 3, mouseY - 3, mouseX + stringWidth + 3, mouseY - 2, color1, color1);
        this.drawGradientRect(mouseX - 3, mouseY + 10, mouseX + stringWidth + 3, mouseY + 11, color2, color2);

        fontRenderer.drawStringWithShadow(s, mouseX, mouseY, 0xffffffff);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
}
