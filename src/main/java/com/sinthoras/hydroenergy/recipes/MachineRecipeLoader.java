package com.sinthoras.hydroenergy.recipes;

import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import net.minecraft.item.ItemStack;

public class MachineRecipeLoader implements Runnable {
    @Override
    public void run() {
        for (Materials tMat : Materials.values()) {
            if (tMat.mStandardMoltenFluid != null && tMat.contains(SubTag.SOLDERING_MATERIAL) && !(GregTech_API.mUseOnlyGoodSolderingMaterials && !tMat.contains(SubTag.SOLDERING_MATERIAL_GOOD))) {
                int tMultiplier = tMat.contains(SubTag.SOLDERING_MATERIAL_GOOD) ? 1 : tMat.contains(SubTag.SOLDERING_MATERIAL_BAD) ? 4 : 2;
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Casing_SolidSteel.get(1L), new ItemStack(GregTech_API.sBlockConcretes, 2, 8),  ItemList.Cover_Screen.get(1L), ItemList.FluidRegulator_LV.get(2L), GT_Utility.getIntegratedCircuit(1)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.HydroDam.get(1L), 200, 30);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_LV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 2L), ItemList.Electric_Motor_LV.get(1L), ItemList.Electric_Pump_LV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Tin, 2L), GT_Utility.getIntegratedCircuit(1)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Pump_LV.get(1L), 200, 30);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_LV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 2L), ItemList.Electric_Motor_LV.get(1L), ItemList.Electric_Pump_LV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Tin, 2L), GT_Utility.getIntegratedCircuit(2)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Dynamo_LV.get(1L), 200, 30);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_MV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.Aluminium, 2L), ItemList.Electric_Motor_MV.get(1L), ItemList.Electric_Pump_MV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Copper, 2L), GT_Utility.getIntegratedCircuit(1)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Pump_MV.get(1L), 200, 64);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_MV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.Aluminium, 2L), ItemList.Electric_Motor_MV.get(1L), ItemList.Electric_Pump_MV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Copper, 2L), GT_Utility.getIntegratedCircuit(2)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Dynamo_MV.get(1L), 200, 64);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_HV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 2L), ItemList.Electric_Motor_HV.get(1L), ItemList.Electric_Pump_HV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Gold, 2L), GT_Utility.getIntegratedCircuit(1)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Pump_HV.get(1L), 200, 120);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_HV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 2L), ItemList.Electric_Motor_HV.get(1L), ItemList.Electric_Pump_HV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Gold, 2L), GT_Utility.getIntegratedCircuit(2)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Dynamo_HV.get(1L), 200, 120);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_EV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 2L), ItemList.Electric_Motor_EV.get(1L), ItemList.Electric_Pump_EV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 2L), GT_Utility.getIntegratedCircuit(1)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Pump_EV.get(1L), 200, 265);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_EV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 2L), ItemList.Electric_Motor_EV.get(1L), ItemList.Electric_Pump_EV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 2L), GT_Utility.getIntegratedCircuit(2)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Dynamo_EV.get(1L), 200, 256);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_IV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 2L), ItemList.Electric_Motor_IV.get(1L), ItemList.Electric_Pump_IV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Platinum, 2L), GT_Utility.getIntegratedCircuit(1)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Pump_IV.get(1L), 200, 480);
                    GT_Values.RA.addAssemblerRecipe(new ItemStack[]{ItemList.Hull_IV.get(1L), GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 2L), ItemList.Electric_Motor_IV.get(1L), ItemList.Electric_Pump_IV.get(1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Platinum, 2L), GT_Utility.getIntegratedCircuit(2)}, tMat.getMolten(144L * tMultiplier / 2L), Hydro_ItemList.Hydro_Dynamo_IV.get(1L), 200, 480);
            }
        }
    }
}

