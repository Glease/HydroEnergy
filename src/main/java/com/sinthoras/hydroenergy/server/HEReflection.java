package com.sinthoras.hydroenergy.server;

import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Method;

public class HEReflection {

    private static Method dumpFluid;
    static {
        try {
            dumpFluid = GT_MetaTileEntity_MultiBlockBase.class.getDeclaredMethod("dumpFluid", FluidStack.class, boolean.class);
            dumpFluid.setAccessible(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean invokeDumpFluid(GT_MetaTileEntity_MultiBlockBase object, FluidStack fluidStack) {
        try {
            return (boolean)dumpFluid.invoke(object, fluidStack, false);
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
