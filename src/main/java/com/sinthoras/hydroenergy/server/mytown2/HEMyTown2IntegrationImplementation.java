package com.sinthoras.hydroenergy.server.mytown2;

import mytown.new_datasource.MyTownUniverse;

public class HEMyTown2IntegrationImplementation extends HEMyTown2Integration {

    public Object getMyTown2PlayerObject(String playerName) {
        return MyTownUniverse.instance.getOrMakeResident(playerName);
    }
}
