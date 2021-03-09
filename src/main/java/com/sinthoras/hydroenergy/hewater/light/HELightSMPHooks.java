package com.sinthoras.hydroenergy.hewater.light;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

public class HELightSMPHooks {

    public static void onSetBlock(World world, int blockX, int blockY, int blockZ, Block block, Block oldBlock) {
        if(world instanceof WorldClient && oldBlock != null) {
            HELightManager.onSetBlock(blockX, blockY, blockZ, block, oldBlock);
        }
    }
}
