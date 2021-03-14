package com.sinthoras.hydroenergy.client.gui;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.blocks.HEControllerTileEntity;
import com.sinthoras.hydroenergy.client.HEClient;
import com.sinthoras.hydroenergy.client.gui.widgets.HEWidgetModes;
import com.sinthoras.hydroenergy.client.gui.widgets.HEWidgetPowerInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class HEDamGui extends GuiContainer {

    public static final ResourceLocation backgroundTextureLocation = new ResourceLocation(HE.MODID, HE.damBackgroundLocation);

    private HEWidgetModes widgetModes;
    private HEWidgetPowerInfo widgetPowerInfo;

    private GuiTextField textField;
    private GuiButton changeWest;
    private GuiButton changeEast;
    private GuiButton changeDown;
    private GuiButton changeUp;
    private GuiButton changeNorth;
    private GuiButton changeSouth;

    private HELimitGui[] limitGuis = new HELimitGui[6];

    private int centerX = 0;
    private int centerY = 0;

    private int waterId;
    private HEControllerTileEntity controllerTileEntity;

    private static final Color lineGrey = new Color(155, 155, 155);
    private static final Color textGrey = new Color(104, 104, 104);

    public HEDamGui(InventoryPlayer inventoryPlayer, HEControllerTileEntity controllerTileEntity) {
        super(new HEDamContainer(inventoryPlayer, controllerTileEntity));
        xSize = 256;
        ySize = 176;
        this.waterId = controllerTileEntity.getWaterId();
        this.controllerTileEntity = controllerTileEntity;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        centerX = guiLeft + xSize / 2;
        centerY = guiTop + ySize / 2;
        int popupLeft = centerX - HELimitGui.xSize / 2;
        int popupTop = centerY - HELimitGui.ySize / 2;

        int pixelX = guiLeft + 20;
        int pixelY = guiTop + 50;
        int width = 100;
        int height = 30;
        textField = new GuiTextField(Minecraft.getMinecraft().fontRenderer, pixelX, pixelY, width, height);
        textField.setEnableBackgroundDrawing(true);
        textField.setMaxStringLength(10);
        textField.setTextColor(Color.BLACK.getRGB());
        textField.setVisible(true);
        textField.setFocused(true);
        textField.setEnabled(true);

        int id = 0;
        pixelX = guiLeft + 10;
        pixelY = guiTop + 86;
        width = 42;
        height = 20;
        changeWest = new GuiButton(id, pixelX, pixelY, width, height, "Change");
        buttonList.add(changeWest);
        limitGuis[0] = new HELimitGui("Western limit (-X)", popupLeft, popupTop, HEClient.limitsWest[waterId]);

        pixelY += 30;
        changeDown = new GuiButton(id, pixelX, pixelY, width, height, "Change");
        buttonList.add(changeDown);
        limitGuis[1] = new HELimitGui("Lower limit (-Y)", popupLeft, popupTop, HEClient.limitsDown[waterId]);

        pixelY += 30;
        changeNorth = new GuiButton(id, pixelX, pixelY, width, height, "Change");
        buttonList.add(changeNorth);
        limitGuis[2] = new HELimitGui("Northern limit (-Z)", popupLeft, popupTop, HEClient.limitsNorth[waterId]);

        pixelX = guiLeft + xSize - width - 10;
        pixelY = guiTop + 86;
        changeEast = new GuiButton(id, pixelX, pixelY, width, height, "Change");
        buttonList.add(changeEast);
        limitGuis[3] = new HELimitGui("Eastern limit (+X)", popupLeft, popupTop, HEClient.limitsEast[waterId]);

        pixelY += 30;
        changeUp = new GuiButton(id, pixelX, pixelY, width, height, "Change");
        buttonList.add(changeUp);
        limitGuis[4] = new HELimitGui("Upper limit (+Y)", popupLeft, popupTop, HEClient.limitsUp[waterId]);

        pixelY += 30;
        changeSouth = new GuiButton(id, pixelX, pixelY, width, height, "Change");
        buttonList.add(changeSouth);
        limitGuis[5] = new HELimitGui("Southern limit (+Z)", popupLeft, popupTop, HEClient.limitsSouth[waterId]);

        widgetModes = new HEWidgetModes(waterId, guiLeft + xSize - 75, guiTop + 5);
        widgetModes.init(buttonList);

        widgetPowerInfo = new HEWidgetPowerInfo(controllerTileEntity, guiLeft + 20, guiTop + 30, xSize - 40);

        for(HELimitGui limitGui : limitGuis) {
            limitGui.init(0, buttonList);
        }
    }

    @Override
    protected void actionPerformed(final GuiButton button)
    {
        if(button == changeWest) {
            widgetModes.setEnabled(false);
            limitGuis[0].show();
        }
        else if(button == changeDown) {
            widgetModes.setEnabled(false);
            limitGuis[1].show();
        }
        else if(button == changeNorth) {
            widgetModes.setEnabled(false);
            limitGuis[2].show();
        }
        else if(button == changeEast) {
            widgetModes.setEnabled(false);
            limitGuis[3].show();
        }
        else if(button == changeUp) {
            widgetModes.setEnabled(false);
            limitGuis[4].show();
        }
        else if(button == changeSouth) {
            widgetModes.setEnabled(false);
            limitGuis[5].show();
        }
        for(HELimitGui limitGui : limitGuis) {
            limitGui.actionPerformed(button);
        }
        widgetModes.actionPerformed(button);
    }

    private void updateValues() {
        if(limitGuis[0].getValueChangedAndReset()) {
            HEClient.limitsWest[waterId] = limitGuis[0].getValue();
            HEClient.configRequest(waterId);
        }
        if(limitGuis[1].getValueChangedAndReset()) {
            HEClient.limitsDown[waterId] = limitGuis[1].getValue();
            HEClient.configRequest(waterId);
        }
        if(limitGuis[2].getValueChangedAndReset()) {
            HEClient.limitsNorth[waterId] = limitGuis[2].getValue();
            HEClient.configRequest(waterId);
        }
        if(limitGuis[3].getValueChangedAndReset()) {
            HEClient.limitsEast[waterId] = limitGuis[3].getValue();
            HEClient.configRequest(waterId);
        }
        if(limitGuis[4].getValueChangedAndReset()) {
            HEClient.limitsUp[waterId] = limitGuis[4].getValue();
            HEClient.configRequest(waterId);
        }
        if(limitGuis[5].getValueChangedAndReset()) {
            HEClient.limitsSouth[waterId] = limitGuis[5].getValue();
            HEClient.configRequest(waterId);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        updateValues();

        Minecraft minecraft = Minecraft.getMinecraft();
        FontRenderer fontRenderer = minecraft.fontRenderer;

        GL11.glColor4f(1F, 1F, 1F, 1F);
        minecraft.getTextureManager().bindTexture(backgroundTextureLocation);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        {
            // Title
            fontRenderer.drawString("Hydroelectric Power Station", guiLeft + 15, guiTop + 6, Color.BLACK.getRGB());

            // Reset color
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            // Draw title bar buttons if popup is open
            widgetModes.draw(minecraft, mouseX, mouseY);

            widgetPowerInfo.draw(minecraft);
        }

        {
            // Disable buttons if a popup is open and render them before the popup is rendered
            boolean enableElements = !isAnyLimitGuiOpen();
            widgetModes.setEnabled(enableElements);
            changeWest.enabled = enableElements;
            changeDown.enabled = enableElements;
            changeNorth.enabled = enableElements;
            changeEast.enabled = enableElements;
            changeUp.enabled = enableElements;
            changeSouth.enabled = enableElements;

            if(!enableElements) {
                changeWest.visible = true;
                changeDown.visible = true;
                changeNorth.visible = true;
                changeEast.visible = true;
                changeUp.visible = true;
                changeSouth.visible = true;

                changeWest.drawButton(minecraft, mouseX, mouseY);
                changeDown.drawButton(minecraft, mouseX, mouseY);
                changeNorth.drawButton(minecraft, mouseX, mouseY);
                changeEast.drawButton(minecraft, mouseX, mouseY);
                changeUp.drawButton(minecraft, mouseX, mouseY);
                changeSouth.drawButton(minecraft, mouseX, mouseY);
            }

            changeWest.visible = enableElements;
            changeDown.visible = enableElements;
            changeNorth.visible = enableElements;
            changeEast.visible = enableElements;
            changeUp.visible = enableElements;
            changeSouth.visible = enableElements;
        }

        {
            // Spread Limits
            fontRenderer.drawString("Spread Limits", guiLeft + 20, guiTop + 75, textGrey.getRGB());

            String limitWest = "" + HEClient.limitsWest[waterId];
            String limitDown = "" + HEClient.limitsDown[waterId];
            String limitNorth = "" + HEClient.limitsNorth[waterId];
            String limitEast = "" + HEClient.limitsEast[waterId];
            String limitUp = "" + HEClient.limitsUp[waterId];
            String limitSouth = "" + HEClient.limitsSouth[waterId];

            int constStringWidthHalf = fontRenderer.getStringWidth(" < X < ") / 2;
            fontRenderer.drawString(" < X < ", centerX - constStringWidthHalf, guiTop + 92, Color.BLACK.getRGB());
            fontRenderer.drawString(" < Y < ", centerX - constStringWidthHalf, guiTop + 122, Color.BLACK.getRGB());
            fontRenderer.drawString(" < Z < ", centerX - constStringWidthHalf, guiTop + 152, Color.BLACK.getRGB());

            int stringWidth = fontRenderer.getStringWidth(limitWest);
            drawHorizontalLine(guiLeft + 57, centerX - constStringWidthHalf - stringWidth - 5, guiTop + 95, lineGrey.getRGB());
            fontRenderer.drawString(limitWest, centerX - constStringWidthHalf - stringWidth, guiTop + 92, Color.BLACK.getRGB());

            stringWidth = fontRenderer.getStringWidth(limitDown);
            drawHorizontalLine(guiLeft + 57, centerX - constStringWidthHalf - stringWidth - 5, guiTop + 125, lineGrey.getRGB());
            fontRenderer.drawString(limitDown, centerX - constStringWidthHalf - stringWidth, guiTop + 122, Color.BLACK.getRGB());

            stringWidth = fontRenderer.getStringWidth(limitNorth);
            drawHorizontalLine(guiLeft + 57, centerX - constStringWidthHalf - stringWidth - 5, guiTop + 155, lineGrey.getRGB());
            fontRenderer.drawString(limitNorth, centerX - constStringWidthHalf - stringWidth, guiTop + 152, Color.BLACK.getRGB());

            stringWidth = fontRenderer.getStringWidth(limitEast);
            drawHorizontalLine(centerX + constStringWidthHalf + stringWidth + 5, guiLeft + xSize - 57, guiTop + 95, lineGrey.getRGB());
            fontRenderer.drawString(limitEast, centerX + constStringWidthHalf, guiTop + 92, Color.BLACK.getRGB());

            stringWidth = fontRenderer.getStringWidth(limitUp);
            drawHorizontalLine(centerX + constStringWidthHalf + stringWidth + 5, guiLeft + xSize - 57, guiTop + 125, lineGrey.getRGB());
            fontRenderer.drawString(limitUp, centerX + constStringWidthHalf, guiTop + 122, Color.BLACK.getRGB());

            stringWidth = fontRenderer.getStringWidth(limitSouth);
            drawHorizontalLine(centerX + constStringWidthHalf + stringWidth + 5, guiLeft + xSize - 57, guiTop + 155, lineGrey.getRGB());
            fontRenderer.drawString(limitSouth, centerX + constStringWidthHalf, guiTop + 152, Color.BLACK.getRGB());

            // Reset color
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }

        for(HELimitGui limitGui : limitGuis) {
            limitGui.draw(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        widgetModes.drawTooltip(Minecraft.getMinecraft().fontRenderer, mouseX - guiLeft, mouseY - guiTop, mouseX, mouseY, width - guiLeft);
    }

    @Override
    protected void mouseClicked(int pixelX, int pixelY, int mouseButtonId) {
        super.mouseClicked(pixelX, pixelY, mouseButtonId);
        for(HELimitGui limitGui : limitGuis) {
            limitGui.mouseClicked(pixelX, pixelY, mouseButtonId);
        }
    }

    @Override
    protected void keyTyped(char c, int keyCode) {
        if (!passKeyTypesToLimitGuis(c, keyCode)) {
            super.keyTyped(c, keyCode);
        }
    }

    private boolean passKeyTypesToLimitGuis(char c, int keyCode) {
        for(HELimitGui limitGui : limitGuis) {
            if(limitGui.keyTyped(c, keyCode)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnyLimitGuiOpen() {
        for(HELimitGui limitGui : limitGuis) {
            if(limitGui.visible) {
                return true;
            }
        }
        return false;
    }
}
