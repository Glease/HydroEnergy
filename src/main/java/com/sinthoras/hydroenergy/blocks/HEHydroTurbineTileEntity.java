package com.sinthoras.hydroenergy.blocks;

import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_Container_MultiMachineEM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_GUIContainer_MultiMachineEM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.render.TT_RenderedExtendedFacingTexture;
import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.config.HEConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public abstract class HEHydroTurbineTileEntity extends HETieredTileEntity implements IConstructable {

    public static class LV extends HEHydroTurbineTileEntity {

        private static final int tierId = 1;

        public LV() {
            super(tierId);
        }

        public LV(int id) {
            super(id, tierId);
        }

        @Override
        protected int getTier() {
            return tierId;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntity.LV();
        }
    }

    public static class MV extends HEHydroTurbineTileEntity {

        private static final int tierId = 2;

        public MV() {
            super(tierId);
        }

        public MV(int id) {
            super(id, tierId);
        }

        @Override
        protected int getTier() {
            return tierId;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntity.MV();
        }
    }

    public static class HV extends HEHydroTurbineTileEntity {

        private static final int tierId = 3;

        public HV() {
            super(tierId);
        }

        public HV(int id) {
            super(id, tierId);
        }

        @Override
        protected int getTier() {
            return tierId;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntity.HV();
        }
    }

    public static class EV extends HEHydroTurbineTileEntity {

        private static final int tierId = 4;

        public EV() {
            super(tierId);
        }

        public EV(int id) {
            super(id, tierId);
        }

        @Override
        protected int getTier() {
            return tierId;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntity.EV();
        }
    }

    public static class IV extends HEHydroTurbineTileEntity {

        private static final int tierId = 5;

        public IV() {
            super(tierId);
        }

        public IV(int id) {
            super(id, tierId);
        }

        @Override
        protected int getTier() {
            return tierId;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntity.IV();
        }
    }

    private static Textures.BlockIcons.CustomIcon textureScreenTurbineON;
    private static Textures.BlockIcons.CustomIcon textureScreenTurbineOFF;
    private static Textures.BlockIcons.CustomIcon textureScreenArrowDownAnimated;

    private final int blockTextureIndex = getCasingTextureId();

    public HEHydroTurbineTileEntity(int tierId) {
        super("he_turbine_" + GT_Values.VN[tierId].toLowerCase());
    }

    public HEHydroTurbineTileEntity(int blockId, int tierId) {
        super(blockId + tierId, "he_turbine_" + GT_Values.VN[tierId].toLowerCase(), "Hydro Turbine (" + GT_Values.VN[tierId] + ")");
    }

    @Override
    public void onTick() {
        final int consumableWaterPerTick = (int)(getVoltage() * HEConfig.milliBucketPerEU);
        int consumedWater = 0;
        for(FluidStack fluidStack : getStoredFluids()) {
            if(fluidStack.getFluidID() == HE.pressurizedWater.getID()) {
                final int consumableWater = Math.max(0, consumableWaterPerTick - consumedWater);
                final int processedWater = Math.min(fluidStack.amount, consumableWater);
                fluidStack.amount -= processedWater;
                consumedWater += processedWater;
            }
        }
        float producedEU = consumedWater * HEConfig.euPerMilliBucket;
        producedEU *= getEfficiencyModifier();
        producedEU *= (float)getCurrentEfficiency(null) / 100_00.0f;
        addEnergyOutput_EM((int)producedEU, 1);

        addOutput(new FluidStack(FluidRegistry.WATER, consumedWater));
    }

    @Override
    public Object getServerGUI(int id, InventoryPlayer playerInventory, IGregTechTileEntity baseMetaTileEntity) {
        return new GT_Container_MultiMachineEM(playerInventory, baseMetaTileEntity, false, false, false);
    }

    @Override
    public Object getClientGUI(int id, InventoryPlayer playerInventory, IGregTechTileEntity baseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachineEM(playerInventory, baseMetaTileEntity, this.getLocalName(), "EMDisplay.png", false, false, false);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister blockIconRegister) {
        textureScreenTurbineOFF = new Textures.BlockIcons.CustomIcon("iconsets/he_turbine");
        textureScreenTurbineON = new Textures.BlockIcons.CustomIcon("iconsets/he_turbine_active");
        textureScreenArrowDownAnimated = new Textures.BlockIcons.CustomIcon("iconsets/he_arrow_down_animated");
        super.registerIcons(blockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, byte side, byte facing, byte colorIndex, boolean isActive, boolean hasRedstoneSignal) {
        if(side == facing) {
            if(isActive) {
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(blockTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenTurbineON),
                        new TT_RenderedExtendedFacingTexture(textureScreenArrowDownAnimated)};
            }
            else {
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(blockTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenTurbineOFF)};
            }
        }
        else {
            return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(blockTextureIndex)};
        }
    }

    @Override
    public String[] getDescription() {
        return new String[] {
                "Hydro Turbine Controller",
                "Controller Block for the Hydro Turbine",
                "Consumes pressurize water to produce EU",
                "Input is pressurized water from Hydro Dams",
                "Requires a Dynamo, Input, Output and Maintenance Hatch anywhere!",
                "Produces up to " + getMilliBucketsPerTick() + "mB Water per Tick",
                "Efficiency: " + getEfficiencyModifierInPercent(),
                HE.blueprintHintTecTech,
                "Use Redstone to automate!"
        };
    }

    @Override
    public String[] getStructureDescription(ItemStack itemStack) {
        return new String[] {
                "1 Dynamo Hatch",
                "1 Fluid Input Hatch",
                "1 Fluid Output Hatch",
                "1 Maintenance Hatch",
                "Fill the rest with " + getCasingName(),
        };
    }

    @Override
    protected long getEnergyConsumption() {
        return getVoltage();
    }
}

