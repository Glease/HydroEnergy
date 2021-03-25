package com.sinthoras.hydroenergy.asm;

import com.sinthoras.hydroenergy.config.HECoreConfig;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;

//@IFMLLoadingPlugin.SortingIndex(1001)  // Run after remapper. Thanks @mitchej123
@IFMLLoadingPlugin.MCVersion(HEPlugin.MC_VERSION)
@IFMLLoadingPlugin.TransformerExclusions("com.sinthoras.hydroenergy.asm")
@IFMLLoadingPlugin.Name(HEPlugin.HYDROENERGYCORE)
public class HEPlugin  implements IFMLLoadingPlugin {

    public static final String HYDROENERGYCORE = "HydroEnergyCore";
    public static final String MC_VERSION = "1.7.10";

    private static Logger LOG = LogManager.getLogger(HYDROENERGYCORE);

    static {
        HECoreConfig.syncronizeConfiguration(new File("config/hydroenergycore.cfg"));
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
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
        return HETransformer.class.getName();
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
