package com.sinthoras.hydroenergy.hewater.render;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.hewater.HEWaterStatic;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.RenderBlockFluid;

public class HEWaterRenderer extends RenderBlockFluid {
	
	public static HEWaterRenderer instance = new HEWaterRenderer();
	private int renderID = -1;
	
	public HEWaterRenderer()
	{
		if(renderID == -1)
			renderID = RenderingRegistry.getNextAvailableRenderId();
	}
	
	@Override
	public float getFluidHeightForRender(IBlockAccess world, int x, int y, int z, BlockFluidBase block)
    {
		HEWaterStatic water = (HEWaterStatic) block;
		float val = water.getRenderedWaterLevel(world, x, y, z) - y;
		if (val <= 0.0f)
			return 0.0f;
		if (val >= 1.0f)
			return 1.0f;
		return val;
    }
	
	@Override
    public int getRenderId()
    {
        return renderID;
    }
	
	@Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        if (!(block instanceof BlockFluidBase))
        {
            return false;
        }
        
        HEWaterStatic water = (HEWaterStatic) block;
        /*boolean lightNeedsPatching =   world.getBlock(x-1, y, z) != water
        							|| world.getBlock(x+1, y, z) != water
        							|| world.getBlock(x, y, z-1) != water
                					|| world.getBlock(x, y, z+1) != water
                					|| world.getBlock(x, y-1, z) != water;*/
        HERenderManager.instance.addBlock(x, y, z, water.getId(), true);//, lightNeedsPatching);
        if (water.getRenderedWaterLevel(world, x, y, z) < y)
        {
        	return false;
        }

        Tessellator tessellator = Tessellator.instance;
        int color = block.colorMultiplier(world, x, y, z);
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        
        Block waterBlock = Block.getBlockFromName("water");
        BlockFluidBase theFluid = (BlockFluidBase) block;
        int densityDir = -1;
        int bMeta = world.getBlockMetadata(x, y, z);

        boolean renderTop = water.getRenderedWaterLevel(world, x, y, z) > y && water.getRenderedWaterLevel(world, x, y, z) <= y + 1;

        boolean renderBottom = block.shouldSideBeRendered(world, x, y + densityDir, z, 0) && world.getBlock(x, y + densityDir, z) != theFluid;

        boolean[] renderSides = new boolean[]
        {
            block.shouldSideBeRendered(world, x, y, z - 1, 2), 
            block.shouldSideBeRendered(world, x, y, z + 1, 3),
            block.shouldSideBeRendered(world, x - 1, y, z, 4), 
            block.shouldSideBeRendered(world, x + 1, y, z, 5)
        };

        if (!renderTop && !renderBottom && !renderSides[0] && !renderSides[1] && !renderSides[2] && !renderSides[3])
        {
            return false;
        }
        else
        {
            boolean rendered = false;
            double heightNW, heightSW, heightSE, heightNE;
            float flow = getFluidHeightForRender(world, x, y, z, theFluid);

            if (flow > 0)
            {	
                heightNW = flow;
                heightSW = flow;
                heightSE = flow;
                heightNE = flow;
            }
            else
            {
                return false;
            }
            
            final float LIGHT_Y_NEG = 0.5F;
            final float LIGHT_Y_POS = 1.0F;
            final float LIGHT_XZ_NEG = 0.8F;
            final float LIGHT_XZ_POS = 0.6F;
            final double RENDER_OFFSET = 0.0010000000474974513D;
            
            if (renderer.renderAllFaces || renderTop)
            {
                rendered = true;
                IIcon iconStill = waterBlock.getIcon(1, bMeta);

                heightNW -= RENDER_OFFSET;
                heightSW -= RENDER_OFFSET;
                heightSE -= RENDER_OFFSET;
                heightNE -= RENDER_OFFSET;

                double u1, u2, u3, u4, v1, v2, v3, v4;

                u2 = iconStill.getInterpolatedU(0.0D);
                v2 = iconStill.getInterpolatedV(0.0D);
                u1 = u2;
                v1 = iconStill.getInterpolatedV(16.0D);
                u4 = iconStill.getInterpolatedU(16.0D);
                v4 = v1;
                u3 = u4;
                v3 = v2;

                tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
                tessellator.setColorOpaque_F(LIGHT_Y_POS * red, LIGHT_Y_POS * green, LIGHT_Y_POS * blue);

                tessellator.addVertexWithUV(x + 0, y + heightNW, z + 0, u2, v2);
                tessellator.addVertexWithUV(x + 0, y + heightSW, z + 1, u1, v1);
                tessellator.addVertexWithUV(x + 1, y + heightSE, z + 1, u4, v4);
                tessellator.addVertexWithUV(x + 1, y + heightNE, z + 0, u3, v3);

                tessellator.addVertexWithUV(x + 0, y + heightNW, z + 0, u2, v2);
                tessellator.addVertexWithUV(x + 1, y + heightNE, z + 0, u3, v3);
                tessellator.addVertexWithUV(x + 1, y + heightSE, z + 1, u4, v4);
                tessellator.addVertexWithUV(x + 0, y + heightSW, z + 1, u1, v1);
            }

            if (renderer.renderAllFaces || renderBottom)
            {
                rendered = true;
                tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
                tessellator.setColorOpaque_F(LIGHT_Y_NEG * red, LIGHT_Y_NEG * green, LIGHT_Y_NEG * blue);
                renderer.renderFaceYNeg(block, x, y + RENDER_OFFSET, z, waterBlock.getIcon(0, bMeta));
            }

            for (int side = 0; side < 4; ++side)
            {
                int x2 = x;
                int z2 = z;

                switch (side)
                {
                    case 0: --z2; break;
                    case 1: ++z2; break;
                    case 2: --x2; break;
                    case 3: ++x2; break;
                }

                IIcon iconFlow = waterBlock.getIcon(side + 2, bMeta);
                if (renderer.renderAllFaces || renderSides[side])
                {
                    rendered = true;

                    double ty1;
                    double tx1;
                    double ty2;
                    double tx2;
                    double tz1;
                    double tz2;

                    if (side == 0)
                    {
                        ty1 = heightNW;
                        ty2 = heightNE;
                        tx1 = x;
                        tx2 = x + 1;
                        tz1 = z + RENDER_OFFSET;
                        tz2 = z + RENDER_OFFSET;
                    }
                    else if (side == 1)
                    {
                        ty1 = heightSE;
                        ty2 = heightSW;
                        tx1 = x + 1;
                        tx2 = x;
                        tz1 = z + 1 - RENDER_OFFSET;
                        tz2 = z + 1 - RENDER_OFFSET;
                    }
                    else if (side == 2)
                    {
                        ty1 = heightSW;
                        ty2 = heightNW;
                        tx1 = x + RENDER_OFFSET;
                        tx2 = x + RENDER_OFFSET;
                        tz1 = z + 1;
                        tz2 = z;
                    }
                    else
                    {
                        ty1 = heightNE;
                        ty2 = heightSE;
                        tx1 = x + 1 - RENDER_OFFSET;
                        tx2 = x + 1 - RENDER_OFFSET;
                        tz1 = z;
                        tz2 = z + 1;
                    }

                    float u1Flow = iconFlow.getInterpolatedU(0.0D);
                    float u2Flow = iconFlow.getInterpolatedU(8.0D);
                    float v1Flow = iconFlow.getInterpolatedV((1.0D - ty1) * 16.0D * 0.5D);
                    float v2Flow = iconFlow.getInterpolatedV((1.0D - ty2) * 16.0D * 0.5D);
                    float v3Flow = iconFlow.getInterpolatedV(8.0D);
                    tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x2, y, z2));
                    float sideLighting = 1.0F;

                    if (side < 2)
                    {
                        sideLighting = LIGHT_XZ_NEG;
                    }
                    else
                    {
                        sideLighting = LIGHT_XZ_POS;
                    }

                    tessellator.setColorOpaque_F(LIGHT_Y_POS * sideLighting * red, LIGHT_Y_POS * sideLighting * green, LIGHT_Y_POS * sideLighting * blue);

                    tessellator.addVertexWithUV(tx1, y + ty1, tz1, u1Flow, v1Flow);
                    tessellator.addVertexWithUV(tx2, y + ty2, tz2, u2Flow, v2Flow);
                    tessellator.addVertexWithUV(tx2, y + 0, tz2, u2Flow, v3Flow);
                    tessellator.addVertexWithUV(tx1, y + 0, tz1, u1Flow, v3Flow);

                    tessellator.addVertexWithUV(tx1, y + ty1, tz1, u1Flow, v1Flow);
                    tessellator.addVertexWithUV(tx1, y + 0, tz1, u1Flow, v3Flow);
                    tessellator.addVertexWithUV(tx2, y + 0, tz2, u2Flow, v3Flow);
                    tessellator.addVertexWithUV(tx2, y + ty2, tz2, u2Flow, v2Flow);
                }
            }
            renderer.renderMinY = 0;
            renderer.renderMaxY = 1;
            return rendered;
        }
    }
}
