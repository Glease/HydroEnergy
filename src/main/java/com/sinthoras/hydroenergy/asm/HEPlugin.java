package com.sinthoras.hydroenergy.asm;

import com.sinthoras.hydroenergy.HE;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion(HE.MC_VERSION)
@IFMLLoadingPlugin.TransformerExclusions(HE.COM_SINTHORAS_HYDROENERGY + ".asm.HETransformer")
public class HEPlugin  implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {HE.COM_SINTHORAS_HYDROENERGY + ".asm.HETransformer"};
    }

    @Override
    public String getModContainerClass() {
        return HE.COM_SINTHORAS_HYDROENERGY + ".asm.HEModContainer";
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
        return null;
    }
}
