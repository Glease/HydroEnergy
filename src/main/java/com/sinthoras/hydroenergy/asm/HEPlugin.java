package com.sinthoras.hydroenergy.asm;

import com.sinthoras.hydroenergy.asm.biomesoplenty.FogHandlerTransformer;
import com.sinthoras.hydroenergy.asm.galaxyspace.GSPlanetFogHandlerTransformer;
import com.sinthoras.hydroenergy.asm.gregtech.GT_PollutionRendererTransformer;
import com.sinthoras.hydroenergy.asm.minecraft.*;
import com.sinthoras.hydroenergy.asm.witchery.ClientEventsTransformer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.SortingIndex(1001)  // Run after remapper. Thanks @mitchej123
@IFMLLoadingPlugin.MCVersion(HEPlugin.MC_VERSION)
@IFMLLoadingPlugin.TransformerExclusions("com.sinthoras.hydroenergy.asm")
@IFMLLoadingPlugin.Name(HEPlugin.HYDROENERGYCORE)
public class HEPlugin  implements IFMLLoadingPlugin {

    public static final String HYDROENERGYCORE = "HydroEnergyCore";
    public static final String MC_VERSION = "1.7.10";

    private static Logger LOG = LogManager.getLogger(HYDROENERGYCORE);

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                FogHandlerTransformer.class.getName(),
                GSPlanetFogHandlerTransformer.class.getName(),
                GT_PollutionRendererTransformer.class.getName(),
                ClientEventsTransformer.class.getName(),
                ActiveRenderInfoTransformer.class.getName(),
                ChunkProviderClientTransformer.class.getName(),
                ChunkTransformer.class.getName(),
                EntityRendererTransformer.class.getName(),
                EntityTransformer.class.getName(),
                WorldRendererTransformer.class.getName(),
                WorldTransformer.class.getName()
        };
    }

    @Override
    public String getModContainerClass() {
        return HEModContainer.class.getName();
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

    public static void debug(String message) {
        LOG.debug(formatMessage(message));
    }

    public static void info(String message) {
        LOG.info(formatMessage(message));
    }

    public static void warn(String message) {
        LOG.warn(formatMessage(message));
    }

    public static void error(String message) {
        LOG.error(formatMessage(message));
    }

    private static String formatMessage(String message) {
        return "[" + HYDROENERGYCORE + "]" + message;
    }
}
