package com.sinthoras.hydroenergy.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

//@IFMLLoadingPlugin.SortingIndex(1001)  // Run after remapper. Thanks @mitchej123
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions("com.sinthoras.hydroenergy.asm")
@IFMLLoadingPlugin.Name(HEPlugin.HydroEnergyCore)
public class HEPlugin  implements IFMLLoadingPlugin {

    public static final String HydroEnergyCore = "HydroEnergyCore";
    public static Logger LOG = LogManager.getLogger(HydroEnergyCore);

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return "com.sinthoras.hydroenergy.asm.HEModContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return "com.sinthoras.hydroenergy.asm.HETransformer";
    }
}
