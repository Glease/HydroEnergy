package com.sinthoras.hydroenergy.fluids;

import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class HEPressurizedWater extends Fluid {

    private static class Tags {
        public static final String pressure = "pres";
    }

    public HEPressurizedWater() {
        super("pressurized_water");
        setUnlocalizedName("Pressurized Water");
    }

    @Override
    public int getColor() {
        return FluidRegistry.WATER.getColor();
    }

    @Override
    public IIcon getStillIcon() {
        return FluidRegistry.WATER.getStillIcon();
    }

    @Override
    public IIcon getFlowingIcon() {
        return FluidRegistry.WATER.getFlowingIcon();
    }

    public void setPressure(FluidStack fluidStack, int pressure) {
        fluidStack.tag.setInteger(Tags.pressure, pressure);
    }

    public int getPressure(FluidStack fluidStack) {
        return fluidStack.tag.hasKey(Tags.pressure) ? fluidStack.tag.getInteger(Tags.pressure) : -1;
    }
}
