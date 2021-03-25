package com.sinthoras.hydroenergy.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class HECoreConfig {
    private static class Defaults {
        public static boolean isFastcraftInstalled = false;
    }

    private class Categories {
        public static final String modInteroperability = "Mod interoperability";
    }

    public static boolean isFastcraftInstalled = Defaults.isFastcraftInstalled;

    private static Configuration configuration;

    public static void syncronizeConfiguration(java.io.File configurationFile) {
        configuration = new Configuration(configurationFile);
        configuration.load();

        Property isFastcraftInstalledProperty = configuration.get(Categories.modInteroperability,
                "isFastcraftInstalled", HECoreConfig.Defaults.isFastcraftInstalled, "[CLIENT] only.");
        isFastcraftInstalled = isFastcraftInstalledProperty.getBoolean();

        if(configuration.hasChanged()) {
            configuration.save();
        }
    }
}
