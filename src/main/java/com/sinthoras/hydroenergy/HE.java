package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.blocks.HEControllerBlock;
import com.sinthoras.hydroenergy.blocks.HEWaterStill;
import com.sinthoras.hydroenergy.client.gui.HEGuiHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class HE {
	public static final String MODID = "hydroenergy";
    public static final String VERSION = "1.0";
    public static final String MC_VERSION = "1.7.10";
    public static final String NAME = "HydroEnergy";
    public static final String COM_SINTHORAS_HYDROENERGY = "com.sinthoras.hydroenergy";
    public static final int FLOAT_SIZE = 4;
    public static SimpleNetworkWrapper network;
    public static Logger LOG;
    public static final int maxRenderDist = 16;
    public static final float minimalUpdateInterval = 0.001f; // in seconds
    public static final int spreadingDelayBetweenChunks = 1000; // in milliseconds
    public static float clippingOffset = 0.05f;  // This value must match the server or physics are off

    static {
        LOG = LogManager.getLogger(MODID);
    }
    public static boolean logicalClientLoaded = false;
    public static final String ERROR_serverIdsOutOfBounds = "Server uses invalid waterIds! Server message ignored. " +
            "Please make sure your config \"maxControllers\" is at least as big as the server you are connecting to!";
    
    // TODO: Move to config
	public static int maxControllers = 16;
    public static HEControllerBlock controller;
	public static final HEWaterStill[] waterBlocks = new HEWaterStill[maxControllers];
	public static final int[] waterBlockIds = new int[maxControllers];

	public static final int maxWaterSpreadWest = Integer.MAX_VALUE;
    public static final int maxWaterSpreadDown = Integer.MAX_VALUE;
    public static final int maxWaterSpreadNorth = Integer.MAX_VALUE;
    public static final int maxWaterSpreadEast = Integer.MAX_VALUE;
    public static final int maxWaterSpreadUp = Integer.MAX_VALUE;
    public static final int maxWaterSpreadSouth = Integer.MAX_VALUE;
	
	public static boolean DEBUGslowFill = false;
	public static final IGuiHandler guiHandler = new HEGuiHandler();

	// Texture locations
    public static String damBackgroundLocation = "textures/gui/he_dam.png";
    public static String damLimitBackgroundLocation = "textures/gui/he_dam_settings.png";
    public static String damTextureName = "he_dam";
    // To silence the water missing texture error. Points to a random but valid texture
    public static String dummyTexture = damTextureName;

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
