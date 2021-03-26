package com.sinthoras.hydroenergy.api;

import net.minecraft.block.material.Material;

public interface IHEHasCustomMaterialCalculation {

    public Material getMaterial(int blockY);

    public Material getMaterial(double blockY);
}
