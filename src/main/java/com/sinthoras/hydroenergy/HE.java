package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.blocks.HEControllerBlock;
import com.sinthoras.hydroenergy.blocks.HEWaterStill;
import com.sinthoras.hydroenergy.client.gui.HEGuiHandler;
import com.sinthoras.hydroenergy.config.HEConfig;
import cpw.mods.fml.common.network.IGuiHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class HE {
	public static final String MODID = "hydroenergy";
    public static final String VERSION = "0.1.0";
    public static final String HYDROENERGY = "HydroEnergy";
    public static final String COM_SINTHORAS_HYDROENERGY = "com.sinthoras.hydroenergy";
    public static SimpleNetworkWrapper network;
    private static Logger LOG = LogManager.getLogger(MODID);
    public static final int maxRenderDist = 16;
    public static final int numChunksY = 16;
    public static final int waterOpacity = 3;
    public static final int chunkWidth = 16;
    public static final int chunkHeight = 16;
    public static final int chunkDepth = 16;
    public static final int blockPerSubChunk = chunkWidth * chunkHeight * chunkDepth;
    public static final int underWaterSkylightDepth = (int)Math.ceil(16f / waterOpacity);
    public static final int controllerGuiUpdateDelay = 200;
    public static final int damCapacityRecalculationDelay = 2000;  // in milliseconds

    public static boolean logicalClientLoaded = false;
    public static final String ERROR_serverIdsOutOfBounds = "Server uses invalid waterIds! Server message ignored. " +
            "Please make sure your config \"maxControllers\" is at least as big as the server you are connecting to!";

    public static HEControllerBlock controller;
	public static final HEWaterStill[] waterBlocks = new HEWaterStill[HEConfig.maxDams];
	public static final int[] waterBlockIds = new int[HEConfig.maxDams];
	
	public static boolean DEBUGslowFill = false;
	public static final IGuiHandler guiHandler = new HEGuiHandler();

	// Texture locations
    public static String damBackgroundLocation = "textures/gui/he_dam.png";
    public static String damLimitBackgroundLocation = "textures/gui/he_dam_settings.png";
    public static String damTextureName = "he_dam";
    // To silence the water missing texture error. Points to a random but valid texture
    public static String dummyTexture = damTextureName;

    public static void debug(String message) {
        HE.LOG.debug(formatMessage(message));
    }

    public static void info(String message) {
        HE.LOG.info(formatMessage(message));
    }

    public static void warn(String message) {
        HE.LOG.warn(formatMessage(message));
    }

    public static void error(String message) {
        HE.LOG.error(formatMessage(message));
    }

    private static String formatMessage(String message) {
        return "[" + HYDROENERGY + "]" + message;
    }

    public enum DamMode {
        DRAIN,
        DEBUG,
        SPREAD;

        public int getValue() {
            switch(this) {
                default:
                case DRAIN:
                    return 1;
                case DEBUG:
                    return 2;
                case SPREAD:
                    return 3;
            }
        }

        public static DamMode getMode(int mode) {
            switch(mode) {
                default:
                case 1:
                    return DRAIN;
                case 2:
                    return DEBUG;
                case 3:
                    return SPREAD;
            }
        }
    }
}
