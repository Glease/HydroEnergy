package com.sinthoras.hydroenergy.client.gui;

import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_GUIContainer_MultiMachineEM;
import com.sinthoras.hydroenergy.network.container.HEHydroDamWaterContainer;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HEHydroDamWaterGuiContainer extends GT_GUIContainer_MultiMachineEM {

    private static final Color textColor = new Color(250, 250, 255);

    private HEHydroDamWaterContainer hydroDamContainer;

    public HEHydroDamWaterGuiContainer(InventoryPlayer inventoryPlayer, IGregTechTileEntity hydroDamMetaTileEntity, String aName, String textureFile) {
        super(inventoryPlayer, hydroDamMetaTileEntity, aName, textureFile, false, false, false);
        hydroDamContainer = new HEHydroDamWaterContainer(inventoryPlayer, hydroDamMetaTileEntity);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString("Hydro Dam", 7, 8, textColor.getRGB());
        fontRendererObj.drawString("Running perfectly.", 7, 16, textColor.getRGB());

        long waterCapacity = hydroDamContainer.getWaterCapacity();
        long waterStored = hydroDamContainer.getWaterStored();
        long mBPerTickIn = hydroDamContainer.getWaterPerTickIn();
        long mBPerTickOut = hydroDamContainer.getWaterPerTickOut();
        float fillMultiplier = waterCapacity == 0.0f ? 0.0f : ((float)waterStored) / ((float)waterCapacity);

        int slashWidth = fontRendererObj.getStringWidth("/");
        int storedWidth = fontRendererObj.getStringWidth("" + (waterStored / 1000) + " kB ");
        fontRendererObj.drawString("" + (waterStored / 1000) + " kB / " + (waterCapacity / 1000) + " kB", 99 - slashWidth / 2 - storedWidth, 35, textColor.getRGB());

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        IIcon iconStill = FluidRegistry.WATER.getStillIcon();
        GL11.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);
        drawTexturedBar(7, 45, 184, 16, iconStill, fillMultiplier);

        String relativeInfo = String.format("%.2f", fillMultiplier * 100.0f) + "%";
        int relativeInfoWidth = fontRendererObj.getStringWidth(relativeInfo);
        fontRendererObj.drawString(relativeInfo, 99 - relativeInfoWidth / 2, 45 + 5, textColor.getRGB());

        String in = "IN: " + mBPerTickIn + " mB/t";
        String out = "OUT: " + mBPerTickOut + " mB/t";
        fontRendererObj.drawString(in, 7, 45 + 20, textColor.getRGB());
        int constStringWidth = fontRendererObj.getStringWidth(out);
        fontRendererObj.drawString(out, 7 + 184 - constStringWidth, 45 + 20, textColor.getRGB());
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
}
