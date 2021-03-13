package com.sinthoras.hydroenergy.client.gui;

import com.sinthoras.hydroenergy.HE;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;

import javax.swing.*;

@SideOnly(Side.CLIENT)
public class HEDamGuiContainer extends GuiContainer {

    private static final ResourceLocation backgroundTextureLocation = new ResourceLocation(HE.MODID, HE.damBackgroundLocation);

    private GuiTextField textField;

    private int centerX = 0;
    private int centerY = 0;

    public HEDamGuiContainer(InventoryPlayer inventoryPlayer, int waterId) {
        super(new HEDamContainer(inventoryPlayer, waterId));
        xSize = 256;
        ySize = 166;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        centerX = guiLeft + xSize / 2;
        centerY = guiTop + ySize / 2;

        int pixelX = guiLeft + 20;
        int pixelY = guiTop + 50;
        textField = new GuiTextField(Minecraft.getMinecraft().fontRenderer, pixelX, pixelY, 100, 30);
        textField.setEnableBackgroundDrawing(true);
        textField.setMaxStringLength(10);
        textField.setTextColor(0x000000);
        textField.setVisible(true);
        textField.setFocused(true);
        textField.setEnabled(true);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(backgroundTextureLocation);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        //this.textField.drawTextBox();

        int stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth("Hydroelectric Power Station");
        Minecraft.getMinecraft().fontRenderer.drawString("Hydroelectric Power Station", centerX - stringWidth / 2, guiTop + 5, 0x000000);
        // reset color after drawString
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        IIcon iconStill = FluidRegistry.WATER.getStillIcon();
        //drawTexturedModelRectFromIcon(guiLeft + 5, guiTop + 50, iconStill, xSize - 10, 10);
        drawTexturedBar(guiLeft + 5, guiTop + 50, xSize - 10, 10, iconStill, 1.0f);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {

    }

    @Override
    protected void mouseClicked(int pixelX, int pixelY, int mouseButtonId)
    {
        super.mouseClicked(pixelX, pixelY, mouseButtonId);
        this.textField.mouseClicked(pixelX, pixelY, mouseButtonId);
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        if (this.textField.textboxKeyTyped(p_73869_1_, p_73869_2_))
        {

        }
        else
        {
            super.keyTyped(p_73869_1_, p_73869_2_);
        }
    }

    private void drawTexturedBar(int pixelX, int pixelY, int width, int height, IIcon icon, float progress) {
        int pixelProgress = Math.round(width * progress);
        int completeTextures = pixelProgress / height;
        for(int i=0;i<completeTextures;i++) {
            int tmpX = pixelX + i * height;
            drawTexturedModelRectFromIcon(tmpX, pixelY, icon, height, height);
        }
        int remainder = pixelProgress % height;
        if(remainder > 0) {
            int tmpX = pixelX + completeTextures * height;
            float minU = icon.getMinU() * 16;
            float minV = icon.getMinV() * 16;
            float diffU = icon.getMaxU() * 16 - minU;
            float diffV = icon.getMaxV() * 16 - minV;
            func_152125_a(tmpX, pixelY, minU, minV, (int)diffU, (int)diffV, remainder, height, 16.0f, 16.0f);
        }
    }
}
