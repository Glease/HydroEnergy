package com.sinthoras.hydroenergy.hewater;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.controller.HEDamsClient;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public abstract class HEWater extends BlockFluidBase {

	public HEWater() {
		super(FluidRegistry.WATER, Material.water);
	}

	@Override
	public FluidStack drain(World world, int x, int y, int z, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canDrain(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public int getQuantaValue(IBlockAccess world, int x, int y, int z) {
		float val = getRenderedWaterLevel(world, x, y, z) - y;
		if (val < 0.0f)
			return 0;
		if (val >= 1.0f)
			return 8;
		return Math.round(val * 8);
	}

	@Override
	public boolean canCollideCheck(int meta, boolean fullHit) {
		return false;
	}

	@Override
	public int getMaxRenderHeightMeta() {
		return 0;
	}
	
	@Override
	public int getLightOpacity(IBlockAccess world, int x, int y, int z)
    {
		if (getRenderedWaterLevel(world, x, y, z) <= y)
        {
        	return 0;
        }
        return getLightOpacity();
    }
	
	public float getRenderedWaterLevel(IBlockAccess world, int x, int y, int z)
	{
		if(HE.logicalClientLoaded)
		{
			return HEDamsClient.instance.getRenderedWaterLevel(getId());
		}
		else
		{
			return HEDams.instance.getRenderedWaterLevel(getId());
		}
	}
	
	private boolean canReplaceBlock(Block block)
	{
		if (displacements.containsKey(block))
        {
            return displacements.get(block);
        }
		return false;
	}

	private float getWaterLevel() {
		if(HE.logicalClientLoaded)
		{
			return HEDamsClient.instance.getRenderedWaterLevel(getId());
		}
		else
		{
			return HEDams.instance.getRenderedWaterLevel(getId());
		}
	}
	
	public boolean canFlowInto(IBlockAccess world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		return block.getMaterial() == Material.air
				|| canDisplace(world, x, y, z)
				|| (block.getMaterial() == Material.water
					&& !(block instanceof HEWater));
	}

	public Material getMaterial(EntityLivingBase entity) {
		// TODO: offset
		return getMaterial(ActiveRenderInfo.projectViewFromEntity(entity, 0).yCoord);
	}
	
	public Material getMaterial(Entity entity) {
		return getMaterial(entity.posY);
	}

	public Material getMaterial(int y) {
		return Math.ceil(getWaterLevel()) <= y ? Material.air : Material.water;
	}

	public Material getMaterial(double y) {
		return getWaterLevel() < y ? Material.air : Material.water;
	}
	
	public abstract int getId();
}
