package com.sinthoras.hydroenergy.server.mytown2;

public class HEMyTown2IntegrationDummy extends HEMyTown2Integration {

    public boolean hasPlayerModificationRightsForChunk(String residentName, int dimension, int chunkX, int chunkZ) {
        return true;
    }
}
