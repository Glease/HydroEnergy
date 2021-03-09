package com.sinthoras.hydroenergy;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

import java.util.Arrays;

public class HEModContainer extends DummyModContainer {
    public HEModContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "he";
        meta.name = "HydroEnergy";
        meta.description = "Provides an immersive alternative to battery buffers";
        meta.version = "1.7.10-1.0";
        meta.authorList = Arrays.asList("Sinthoras");
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
