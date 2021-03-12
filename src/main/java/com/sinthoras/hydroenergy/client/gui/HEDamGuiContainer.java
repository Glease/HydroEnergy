package com.sinthoras.hydroenergy.client.gui;

import com.sinthoras.hydroenergy.HE;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class HEDamGuiContainer extends GuiContainer {

    private static final ResourceLocation backgroundTextureLocation = new ResourceLocation(HE.MODID, HE.damBackgroundLocation);

    public HEDamGuiContainer(InventoryPlayer inventoryPlayer, int waterId) {
        super(new HEDamContainer(inventoryPlayer, waterId));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(backgroundTextureLocation);
        this.drawTexturedModalRect(this.guiLeft - 79, this.guiTop, 0, 0, 256, 165);
    }
}
