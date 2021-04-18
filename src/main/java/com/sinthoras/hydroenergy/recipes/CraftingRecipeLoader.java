package com.sinthoras.hydroenergy.recipes;

import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.util.GT_ModHandler;
import net.minecraft.item.ItemStack;


public class CraftingRecipeLoader implements Runnable {

    private static final long bits = GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE | GT_ModHandler.RecipeBits.DISMANTLEABLE | GT_ModHandler.RecipeBits.BUFFERED;

    @Override
    public void run() {

        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.HydroDam.get(1L), bits, new Object[]{"CDC", "RMR", "CSC", 'C', new ItemStack(GregTech_API.sBlockConcretes, 1, 8), 'D', ItemList.Cover_Screen, 'R', ItemList.FluidRegulator_LV, 'M', ItemList.Casing_SolidSteel, 'S', OrePrefixes.plate.get(Materials.Steel)});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Pump_LV.get(1L), bits, new Object[]{"SPC", "YXY", "SMC", 'S', OrePrefixes.rotor.get(Materials.Steel), 'P', ItemList.Electric_Pump_LV, 'C', OrePrefixes.cableGt01.get(Materials.Tin), 'Y', OrePrefixes.plate.get(Materials.Steel), 'X',  ItemList.Hull_LV, 'M', ItemList.Electric_Motor_LV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Dynamo_LV.get(1L), bits, new Object[]{"CMS", "YXY", "CPS", 'S', OrePrefixes.rotor.get(Materials.Steel), 'P', ItemList.Electric_Pump_LV, 'C', OrePrefixes.cableGt01.get(Materials.Tin), 'Y', OrePrefixes.plate.get(Materials.Steel), 'X',  ItemList.Hull_LV, 'M', ItemList.Electric_Motor_LV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Pump_MV.get(1L), bits, new Object[]{"SPC", "YXY", "SMC", 'S', OrePrefixes.rotor.get(Materials.Aluminium), 'P', ItemList.Electric_Pump_MV, 'C', OrePrefixes.cableGt01.get(Materials.Copper), 'Y', OrePrefixes.plate.get(Materials.Aluminium), 'X',  ItemList.Hull_MV, 'M', ItemList.Electric_Motor_MV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Dynamo_MV.get(1L), bits, new Object[]{"CMS", "YXY", "CPS", 'S', OrePrefixes.rotor.get(Materials.Aluminium), 'P', ItemList.Electric_Pump_MV, 'C', OrePrefixes.cableGt01.get(Materials.Copper), 'Y', OrePrefixes.plate.get(Materials.Aluminium), 'X',  ItemList.Hull_MV, 'M', ItemList.Electric_Motor_MV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Pump_HV.get(1L), bits, new Object[]{"SPC", "YXY", "SMC", 'S', OrePrefixes.rotor.get(Materials.StainlessSteel), 'P', ItemList.Electric_Pump_HV, 'C', OrePrefixes.cableGt01.get(Materials.Gold), 'Y', OrePrefixes.plate.get(Materials.StainlessSteel), 'X',  ItemList.Hull_HV, 'M', ItemList.Electric_Motor_HV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Dynamo_HV.get(1L), bits, new Object[]{"CMS", "YXY", "CPS", 'S', OrePrefixes.rotor.get(Materials.StainlessSteel), 'P', ItemList.Electric_Pump_HV, 'C', OrePrefixes.cableGt01.get(Materials.Gold), 'Y', OrePrefixes.plate.get(Materials.StainlessSteel), 'X',  ItemList.Hull_HV, 'M', ItemList.Electric_Motor_HV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Pump_EV.get(1L), bits, new Object[]{"SPC", "YXY", "SMC", 'S', OrePrefixes.rotor.get(Materials.Titanium), 'P', ItemList.Electric_Pump_EV, 'C', OrePrefixes.cableGt01.get(Materials.Aluminium), 'Y', OrePrefixes.plate.get(Materials.Titanium), 'X',  ItemList.Hull_EV, 'M', ItemList.Electric_Motor_EV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Dynamo_EV.get(1L), bits, new Object[]{"CMS", "YXY", "CPS", 'S', OrePrefixes.rotor.get(Materials.Titanium), 'P', ItemList.Electric_Pump_EV, 'C', OrePrefixes.cableGt01.get(Materials.Aluminium), 'Y', OrePrefixes.plate.get(Materials.Titanium), 'X',  ItemList.Hull_EV, 'M', ItemList.Electric_Motor_EV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Pump_IV.get(1L), bits, new Object[]{"SPC", "YXY", "SMC", 'S', OrePrefixes.rotor.get(Materials.StainlessSteel), 'P', ItemList.Electric_Pump_IV, 'C', OrePrefixes.cableGt01.get(Materials.Platinum), 'Y', OrePrefixes.plate.get(Materials.StainlessSteel), 'X',  ItemList.Hull_IV, 'M', ItemList.Electric_Motor_IV});
        GT_ModHandler.addCraftingRecipe(Hydro_ItemList.Hydro_Dynamo_IV.get(1L), bits, new Object[]{"CMS", "YXY", "CPS", 'S', OrePrefixes.rotor.get(Materials.StainlessSteel), 'P', ItemList.Electric_Pump_IV, 'C', OrePrefixes.cableGt01.get(Materials.Platinum), 'Y', OrePrefixes.plate.get(Materials.StainlessSteel), 'X',  ItemList.Hull_IV, 'M', ItemList.Electric_Motor_IV});

    }
}