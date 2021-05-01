package com.sinthoras.hydroenergy.server;

import com.sinthoras.hydroenergy.HE;
import mytown.new_datasource.MyTownUniverse;

public class HEMyTown2Implementation implements IHEMyTown2 {

    public void test() {
        HE.info("IMPLEMENTATION");
    }

    public void test2() {
        MyTownUniverse.instance.blocks.get(0, 1, 2);
    }
}
