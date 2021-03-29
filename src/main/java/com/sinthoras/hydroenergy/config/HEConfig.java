package com.sinthoras.hydroenergy.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.List;

public class HEConfig {
    private class Defaults {
        public static final int maxDams = 16;
        public static final int minimalWaterUpdateInterval = 1000; // in milliseconds
        public static final int spreadingDelayBetweenPerChunks = 2000; // in milliseconds
        public static final int minLightUpdateTimePerSubChunk = 10; // in milliseconds
        public static final float clippingOffset = 0.05f; // in blocks
        public static final int maxWaterSpreadWest = 1000; // in blocks
        public static final int maxWaterSpreadDown = 1000; // in blocks
        public static final int maxWaterSpreadNorth = 1000; // in blocks
        public static final int maxWaterSpreadEast = 1000; // in blocks
        public static final int maxWaterSpreadUp = 1000; // in blocks
        public static final int maxWaterSpreadSouth = 10000; // in blocks
        public static final int energyPerWaterBlock = 10000; // in GregTech EU
        public static final int overworldId = 0;
        public static final int damDrainPerSecond = 1000; // in mB
        public static final float waterBonusPerSurfaceBlockPerRainTick = 1.0f; // in mB
        public static final int blockIdOffset = 17000;
    }

    private class Categories {
        public static final String general = "General";
        public static final String spreading = "Spreading";
        public static final String energyBalance = "Energy Balance";
    }

    public static int maxDams = Defaults.maxDams;
    public static int minimalWaterUpdateInterval = Defaults.minimalWaterUpdateInterval;
    public static int spreadingDelayBetweenPerChunks = Defaults.spreadingDelayBetweenPerChunks;
    public static int minLightUpdateTimePerSubChunk = Defaults.minLightUpdateTimePerSubChunk;
    public static float clippingOffset = Defaults.clippingOffset;
    public static List<Integer> dimensionIdWhitelist = new ArrayList<Integer>();
    public static float energyPerWaterBlock = Defaults.energyPerWaterBlock;
    public static int maxWaterSpreadWest = Defaults.maxWaterSpreadWest;
    public static int maxWaterSpreadDown = Defaults.maxWaterSpreadDown;
    public static int maxWaterSpreadNorth = Defaults.maxWaterSpreadNorth;
    public static int maxWaterSpreadEast = Defaults.maxWaterSpreadEast;
    public static int maxWaterSpreadUp = Defaults.maxWaterSpreadUp;
    public static int maxWaterSpreadSouth = Defaults.maxWaterSpreadSouth;
    public static int damDrainPerSecond = Defaults.damDrainPerSecond;
    public static float waterBonusPerSurfaceBlockPerRainTick = Defaults.waterBonusPerSurfaceBlockPerRainTick;
    public static int blockIdOffset = Defaults.blockIdOffset;

    private static Configuration configuration;

    public static void syncronizeConfiguration(java.io.File configurationFile) {
        configuration = new Configuration(configurationFile);
        configuration.load();

        Property maxDamsProperty = configuration.get(Categories.general, "maxDams", Defaults.maxDams,
                "[SERVER] How many dams should the game support. At least as many as the server you want to connect" +
                        " to. Each dam will receive it's own water block and it will also have a minuscule performance" +
                        " impact. Keep it only as long as you need. You can always just rise, but not shorten the value.");
        maxDams = maxDamsProperty.getInt();

        Property minimalWaterUpdateIntervalProperty = configuration.get(Categories.general,
                "minimalWaterUpdateInterval", Defaults.minimalWaterUpdateInterval, "[SERVER] Minimum delay" +
                        " in milliseconds beween update packets from the server to ALL clients.");
        minimalWaterUpdateInterval = minimalWaterUpdateIntervalProperty.getInt();

        Property spreadingDelayBetweenPerChunksProperty = configuration.get(Categories.general,
                "spreadingDelayBetweenPerChunks", Defaults.spreadingDelayBetweenPerChunks, "[SERVER] Delay" +
                        " in milliseconds the game will wait between processing a chunk for water spreading. Keep in " +
                        "mind, that a single tick takes care of a whole chunk between y=0 and y=255 at once!");
        spreadingDelayBetweenPerChunks = spreadingDelayBetweenPerChunksProperty.getInt();

        Property minLightUpdateTimePerSubChunkProperty = configuration.get(Categories.general,
                "minLightUpdateTimePerSubChunk", Defaults.minLightUpdateTimePerSubChunk, "[CLIENT] Light " +
                        "calculation required all affected chunks to be rerendered. When a change in waterLevel induces " +
                        "rerendering it will also calculate a minimum delay before it can happen again. Light updates " +
                        "will not be lost, just delayed. For every subChunk (16 blocks high) that was rerendered because" +
                        " of this update event, the game will add the specified delay (in milliseconds) up for the actual delay. You " +
                        "should expect the number of rerendered subChunks to be in the low hundreds");
        minLightUpdateTimePerSubChunk = minLightUpdateTimePerSubChunkProperty.getInt();

        Property clippingOffsetProperty = configuration.get(Categories.general, "clippingOffset",
                Defaults.clippingOffset, "[SERVER + CLIENT] If water is sitting too narrow over a block there" +
                        " are graphical issues (Depth buffer resolution). To fix this, the game will not render a " +
                        "waterLevel that sits lower then the specified value over a block. This value is also used for " +
                        "physics calculation and is synced from the server all clients.");
        clippingOffset = (float)clippingOffsetProperty.getDouble();

        Property dimensionIdWhitelistProperties = configuration.get(Categories.general, "dimensionIdWhitelist",
                new int[] {Defaults.overworldId}, "[SERVER] List of dimension a player is allowed to place a controller");
        dimensionIdWhitelist.clear();
        for(int id : dimensionIdWhitelistProperties.getIntList()) {
            dimensionIdWhitelist.add(id);
        }

        configuration.addCustomCategoryComment(Categories.spreading, "Water spreading will quickly get out of " +
                "controll if somebody missclicks their limits on their controllers. Here are game wide limits for " +
                "spreading.");

        Property maxWaterSpreadWestProperty = configuration.get(Categories.spreading, "maxWaterSpreadWest",
                Defaults.maxWaterSpreadWest, "[SERVER]");
        maxWaterSpreadWest = maxWaterSpreadWestProperty.getInt();
        Property maxWaterSpreadDownProperty = configuration.get(Categories.spreading, "maxWaterSpreadDown",
                Defaults.maxWaterSpreadDown, "[SERVER]");
        maxWaterSpreadDown = maxWaterSpreadDownProperty.getInt();
        Property maxWaterSpreadNorthProperty = configuration.get(Categories.spreading, "maxWaterSpreadNorth",
                Defaults.maxWaterSpreadNorth, "[SERVER]");
        maxWaterSpreadNorth = maxWaterSpreadNorthProperty.getInt();
        Property maxWaterSpreadEastProperty = configuration.get(Categories.spreading, "maxWaterSpreadEast",
                Defaults.maxWaterSpreadEast, "[SERVER]");
        maxWaterSpreadEast = maxWaterSpreadEastProperty.getInt();
        Property maxWaterSpreadUpProperty = configuration.get(Categories.spreading, "maxWaterSpreadUp",
                Defaults.maxWaterSpreadUp, "[SERVER]");
        maxWaterSpreadUp = maxWaterSpreadUpProperty.getInt();
        Property maxWaterSpreadSouthProperty = configuration.get(Categories.spreading, "maxWaterSpreadSouth",
                Defaults.maxWaterSpreadSouth, "[SERVER]");
        maxWaterSpreadSouth = maxWaterSpreadSouthProperty.getInt();

        Property damDrainPerSecondProperty = configuration.get(Categories.energyBalance, "damDrainPerSecond",
                Defaults.damDrainPerSecond, "[SERVER] How many mB a dam will provide for turbines per tick.");
        damDrainPerSecond = damDrainPerSecondProperty.getInt();

        Property waterBonusPerSurfaceBlockPerRainTickProperty = configuration.get(Categories.energyBalance,
                "waterBonusPerSurfaceBlockPerRainTick", Defaults.waterBonusPerSurfaceBlockPerRainTick,
                "[SERVER] How many mb of water are added to a dam during rain for each water block on the" +
                        " highest Y coordinate");

        Property blockIdOffsetProperty = configuration.get(Categories.general, "blockIdOffset", Defaults.blockIdOffset,
                "[SERVER + CLIENT] Offset of blockIds for GregTech block registration");
        blockIdOffset = blockIdOffsetProperty.getInt();

        if(configuration.hasChanged()) {
            configuration.save();
        }
    }
}
