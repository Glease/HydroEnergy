package com.sinthoras.hydroenergy;

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

    static{
        LOG = LogManager.getLogger(MODID);
    }
    public static boolean logicalClientLoaded = false;
    
    // TODO: Move to config
	public static final int worldHeight = 256;
	public static final int maxController = 16;
	public static final float waterRenderResolution = 16.0f;
	
	public static final int maxRerenderChunksPerRenderTick = 8;
	
	public static boolean DEBUGslowFill = false;
}
