package com.sinthoras.hydroenergy.api;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class HEGetMaterialUtil {

    public static Material getMaterialWrapper(Block block, int blockY) {
        if(block instanceof IHEHasCustomMaterialCalculation) {
            return ((IHEHasCustomMaterialCalculation)block).getMaterial(blockY);
        }
        else {
            return block.getMaterial();
        }
    }

    public static Material getMaterialWrapper(Block block, double blockY) {
        if(block instanceof IHEHasCustomMaterialCalculation) {
            return ((IHEHasCustomMaterialCalculation)block).getMaterial(blockY);
        }
        else {
            return block.getMaterial();
        }
    }
}
