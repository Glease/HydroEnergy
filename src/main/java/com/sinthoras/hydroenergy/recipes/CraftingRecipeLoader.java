package com.sinthoras.hydroenergy.recipes;

import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.util.GT_ModHandler;


public class CraftingRecipeLoader implements Runnable {

    private static final long bits = GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE | GT_ModHandler.RecipeBits.DISMANTLEABLE | GT_ModHandler.RecipeBits.BUFFERED;

    @Override
    public void run() {

        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.HydroDam.get(1L), bits, new Object[]{"CDC", "RMR", "CSC", 'C', GregTech_API.sBlockConcretes, 8, 'D', ItemList.Cover_Screen, 'R', ItemList.FluidRegulator_LV, 'M', ItemList.Casing_SolidSteel, 'S', OrePrefixes.plate.get(Materials.Steel)});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Pump_LV.get(1L), bits, new Object[]{"SPC", "YXY", "SMC", 'S', OrePrefixes.rotor.get(Materials.Steel), 'P', ItemList.Pump_LV, 'C', OrePrefixes.cableGt01.get(Materials.Tin), 'Y', OrePrefixes.plate.get(Materials.Steel), 'X',  ItemList.Hull_LV, 'M', ItemList.Electric_Motor_LV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Dynamo_LV.get(1L), bits, new Object[]{"CMS", "YXY", "CPS", 'S', OrePrefixes.rotor.get(Materials.Steel), 'P', ItemList.Pump_LV, 'C', OrePrefixes.cableGt01.get(Materials.Tin), 'Y', OrePrefixes.plate.get(Materials.Steel), 'X',  ItemList.Hull_LV, 'M', ItemList.Electric_Motor_LV});

    }
}