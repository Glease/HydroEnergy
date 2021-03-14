package com.sinthoras.hydroenergy.client.light;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class HELightSMPHooks {

    // this method filters @Side.SERVER out and only passes client side calls on
    public static void onSetBlock(World world, int blockX, int blockY, int blockZ, Block block, Block oldBlock) {
        if(world.isRemote && oldBlock != null) {
            HELightManager.onSetBlock(blockX, blockY, blockZ, block, oldBlock);
        }
    }
}
