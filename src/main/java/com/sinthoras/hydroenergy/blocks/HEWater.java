package com.sinthoras.hydroenergy.blocks;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HEUtil;
import com.sinthoras.hydroenergy.client.HEClient;
import com.sinthoras.hydroenergy.config.HEConfig;
import com.sinthoras.hydroenergy.server.HEServer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HEWater extends BlockFluidBase {

	private int waterId;

	public HEWater(int waterId) {
		super(FluidRegistry.WATER, Material.water);
		this.waterId = waterId;
		setHardness(100.0F);
		setLightOpacity(0);
		setBlockName("water");
		setBlockTextureName(HE.MODID + ":" + HE.dummyTexture);
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
		float waterLevelInBlock = getWaterLevel() - blockY;
		waterLevelInBlock = HEUtil.clamp(waterLevelInBlock, 0.0f, 1.0f);
		return Math.round(waterLevelInBlock * 8);
	}

	@Override
	public boolean canCollideCheck(int meta, boolean fullHit) {
		return false;
	}

	@Override
	public int getLightOpacity() {
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			return HE.waterOpacity;
		}
		else {
			return 0;
		}
	}

	@Override
	public int getMaxRenderHeightMeta() {
		return 0;
	}

	public float getWaterLevel() {
		if(HE.logicalClientLoaded) {
			return HEClient.getDam(waterId).getWaterLevelForPhysicsAndLighting();
		}
		else {
			return HEServer.instance.getWaterLevel(getWaterId());
		}
	}
	
	public boolean canFlowInto(IBlockAccess world, int blockX, int blockY, int blockZ) {
		Block block = world.getBlock(blockX, blockY, blockZ);
		return block.getMaterial() == Material.air
				|| (canDisplace(world, blockX, blockY, blockZ) && !(block instanceof BlockLiquid))
				|| (block.getMaterial() == Material.water && !(block instanceof HEWater));
	}

	// For Block.setupFog, Block.updateFogColor and Block.getFOVModifier
	public Material getMaterial(EntityLivingBase entity) {
		return getMaterial(ActiveRenderInfo.projectViewFromEntity(entity, 0).yCoord);
	}


	/*public Material getMaterial(Entity entity) {
		return getMaterial(entity.posY);
	}*/

	// For World.handleMaterialAcceleration
	public Material getMaterial(int blockY) {
		return (Math.floor(getWaterLevel() - HEConfig.clippingOffset)) < blockY ? Material.air : Material.water;
	}

	// For Block.isInsideOfMaterial
	public Material getMaterial(double blockY) {
		// This constant is so magic i'm gonna die!
		// Without this constant there is a gap between rendered water and all under water effects
		// and the player cannot exit water without half slabs. Not sure where this comes from...yet
		return (getWaterLevel() + 0.120f) < blockY ? Material.air : Material.water;
	}
	
	public int getWaterId() {
		return waterId;
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName() + waterId;
	}
}
