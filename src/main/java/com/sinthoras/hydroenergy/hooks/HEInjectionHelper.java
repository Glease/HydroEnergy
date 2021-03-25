package com.sinthoras.hydroenergy.hooks;

import com.sinthoras.hydroenergy.blocks.HEWater;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class HEInjectionHelper {

    public static Material getMaterialWrapper(Block block, int blockY) {
        if(block instanceof HEWater) {
            return ((HEWater)block).getMaterial(blockY);
        }
        else {
            return block.getMaterial();
        }
    }

    public static Material getMaterialWrapper(Block block, double blockY) {
        if(block instanceof HEWater) {
            return ((HEWater)block).getMaterial(blockY);
        }
        else {
            return block.getMaterial();
        }
    }
}
