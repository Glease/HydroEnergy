package com.sinthoras.hydroenergy.client.gui;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.blocks.HEControllerTileEntity;
import com.sinthoras.hydroenergy.client.HEClient;
import com.sinthoras.hydroenergy.client.gui.widgets.HEWidgetModes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class HEDamGui extends GuiContainer {

    public static final ResourceLocation backgroundTextureLocation = new ResourceLocation(HE.MODID, HE.damBackgroundLocation);

    private HEWidgetModes widgetModes;

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
        textField.setTextColor(0x000000);
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
            fontRenderer.drawString("Hydroelectric Power Station", guiLeft + 15, guiTop + 6, 0x000000);

            // Reset color
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            // Draw title bar buttons if popup is open
            widgetModes.draw(minecraft, mouseX, mouseY);
        }

        {
            // Power info
            int euStored = controllerTileEntity.getEnergyStored();
            int euCapacity = controllerTileEntity.getEnergyCapacity();
            float euRelative = ((float)euStored) / ((float)euCapacity);

            minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            IIcon iconStill = FluidRegistry.WATER.getStillIcon();
            GL11.glColor4f(0.6f, 0.6f, 0.6f, 1.0f);
            drawTexturedBar(guiLeft + 20, guiTop + 40, xSize - 40, 10, iconStill, euRelative);

            int slashWidth = fontRenderer.getStringWidth("/");
            int storedWidth = fontRenderer.getStringWidth("" + euStored + " EU ");
            fontRenderer.drawString("" + euStored + " EU / " + euCapacity + " EU", centerX - slashWidth / 2 - storedWidth, guiTop + 30, 0x000000);

            float percentage = euRelative * 100.0f;
            String relativeInfo = String.format("%.2f", percentage) + "%";
            int relativeInfoWidth = fontRenderer.getStringWidth(relativeInfo);
            fontRenderer.drawString(relativeInfo, centerX - relativeInfoWidth / 2, guiTop + 42, 0xFFFFFF);

            int euPerTickIn = controllerTileEntity.getEnergyPerTickIn();
            int euPerTickOut = controllerTileEntity.getEnergyPerTickOut();
            String in = "IN: " + euPerTickIn + " EU/t";
            String out = "OUT: " + euPerTickOut + " EU/t";
            fontRenderer.drawString(in, guiLeft + 20, guiTop + 53, 0x000000);
            int constStringWidth = fontRenderer.getStringWidth(out);
            fontRenderer.drawString(out, guiLeft + xSize - 20 - constStringWidth, guiTop + 53, 0x000000);

            // Reset color
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
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
            fontRenderer.drawString("Spread Limits", guiLeft + 20, guiTop + 75, 0x686868);

            changeWest.drawButton(minecraft, mouseX, mouseY);
            changeDown.drawButton(minecraft, mouseX, mouseY);
            changeNorth.drawButton(minecraft, mouseX, mouseY);
            changeEast.drawButton(minecraft, mouseX, mouseY);
            changeUp.drawButton(minecraft, mouseX, mouseY);
            changeSouth.drawButton(minecraft, mouseX, mouseY);

            int limitWest = HEClient.limitsWest[waterId];
            int limitDown = HEClient.limitsDown[waterId];
            int limitNorth = HEClient.limitsNorth[waterId];
            int limitEast = HEClient.limitsEast[waterId];
            int limitUp = HEClient.limitsUp[waterId];
            int limitSouth = HEClient.limitsSouth[waterId];

            int constStringWidthHalf = fontRenderer.getStringWidth(" < X < ") / 2;
            fontRenderer.drawString(" < X < ", centerX - constStringWidthHalf, guiTop + 92, 0x000000);
            fontRenderer.drawString(" < Y < ", centerX - constStringWidthHalf, guiTop + 122, 0x000000);
            fontRenderer.drawString(" < Z < ", centerX - constStringWidthHalf, guiTop + 152, 0x000000);

            int stringWidth = fontRenderer.getStringWidth("" + limitWest);
            drawHorizontalLine(guiLeft + 42 + 10 + 5, centerX - constStringWidthHalf - stringWidth - 5, guiTop + 95, 0xFF9B9B9B);
            fontRenderer.drawString("" + limitWest, centerX - constStringWidthHalf - stringWidth, guiTop + 92, 0x000000);

            stringWidth = fontRenderer.getStringWidth("" + limitDown);
            drawHorizontalLine(guiLeft + 42 + 10 + 5, centerX - constStringWidthHalf - stringWidth - 5, guiTop + 125, 0xFF9B9B9B);
            fontRenderer.drawString("" + limitDown, centerX - constStringWidthHalf - stringWidth, guiTop + 122, 0x000000);

            stringWidth = fontRenderer.getStringWidth("" + limitNorth);
            drawHorizontalLine(guiLeft + 42 + 10 + 5, centerX - constStringWidthHalf - stringWidth - 5, guiTop + 155, 0xFF9B9B9B);
            fontRenderer.drawString("" + limitNorth, centerX - constStringWidthHalf - stringWidth, guiTop + 152, 0x000000);

            stringWidth = fontRenderer.getStringWidth("" + limitEast);
            drawHorizontalLine(centerX + constStringWidthHalf + stringWidth + 5, guiLeft + xSize - 42 - 10 - 5, guiTop + 95, 0xFF9B9B9B);
            fontRenderer.drawString("" + limitEast, centerX + constStringWidthHalf, guiTop + 92, 0x000000);

            stringWidth = fontRenderer.getStringWidth("" + limitUp);
            drawHorizontalLine(centerX + constStringWidthHalf + stringWidth + 5, guiLeft + xSize - 42 - 10 - 5, guiTop + 125, 0xFF9B9B9B);
            fontRenderer.drawString("" + limitUp, centerX + constStringWidthHalf, guiTop + 122, 0x000000);

            stringWidth = fontRenderer.getStringWidth("" + limitSouth);
            drawHorizontalLine(centerX + constStringWidthHalf + stringWidth + 5, guiLeft + xSize - 42 - 10 - 5, guiTop + 155, 0xFF9B9B9B);
            fontRenderer.drawString("" + limitSouth, centerX + constStringWidthHalf, guiTop + 152, 0x000000);

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

    private void drawTexturedBar(int pixelX, int pixelY, int width, int height, IIcon icon, float progress) {
        int pixelProgress = Math.round(width * progress);
        int iconHeight = icon.getIconHeight();
        int completeTextures = pixelProgress / iconHeight;
        for(int i=0;i<completeTextures;i++) {
            int tmpX = pixelX + i * iconHeight;
            drawTexturedModelRectFromIcon(tmpX, pixelY, icon, iconHeight, height);
        }
        int remainder = pixelProgress % iconHeight;
        if(remainder > 0) {
            int tmpX = pixelX + completeTextures * iconHeight;
            drawTexturedModelRectFromIcon(tmpX, pixelY, icon,  remainder, height);
        }
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
