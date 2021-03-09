package com.sinthoras.hydroenergy.hewater;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.controller.HEDams;
import com.sinthoras.hydroenergy.controller.HEDamsClient;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HEWater extends BlockFluidBase {

	private int id;

	public HEWater(int id) {
		super(FluidRegistry.WATER, Material.water);
		this.id = id;
	}

	@Override
	public FluidStack drain(World world, int blockX, int blockY, int blockZ, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canDrain(World world, int blockX, int blockY, int blockZ) {
		return false;
	}

	@Override
	public int getQuantaValue(IBlockAccess world, int blockX, int blockY, int blockZ) {
		float val = getRenderedWaterLevel(world, blockX, blockY, blockZ) - blockY;
		val = HEUtil.clamp(val, 0.0f, 1.0f);
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
	public int getLightOpacity(IBlockAccess world, int blockX, int blockY, int blockZ)
    {
		if (getRenderedWaterLevel(world, blockX, blockY, blockZ) <= blockY)
        {
        	return 0;
        }
        return getLightOpacity();
    }
	
	public float getRenderedWaterLevel(IBlockAccess world, int blockX, int blockY, int blockZ)
	{
		if(HE.logicalClientLoaded)
		{
			return HEDamsClient.getRenderedWaterLevel(getId());
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
			return HEDamsClient.getRenderedWaterLevel(getId());
		}
		else
		{
			return HEDams.instance.getRenderedWaterLevel(getId());
		}
	}
	
	public boolean canFlowInto(IBlockAccess world, int blockX, int blockY, int blockZ)
	{
		Block block = world.getBlock(blockX, blockY, blockZ);
		return block.getMaterial() == Material.air
				|| canDisplace(world, blockX, blockY, blockZ)
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

	public Material getMaterial(int blockY) {
		return Math.floor(getWaterLevel()) < blockY ? Material.air : Material.water;
	}

	public Material getMaterial(double blockY) {
		return getWaterLevel() < blockY ? Material.air : Material.water;
	}
	
	public int getId() {
		return id;
	}

	@Override
	public String getUnlocalizedName()
	{
		return super.getUnlocalizedName() + id;
	}
}
