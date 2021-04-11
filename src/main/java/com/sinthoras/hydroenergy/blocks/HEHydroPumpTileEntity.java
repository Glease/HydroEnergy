package com.sinthoras.hydroenergy.blocks;

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

public abstract class HEHydroPumpTileEntity extends HETieredTileEntity {

    public static class LV extends HEHydroPumpTileEntity {

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
            return new LV();
        }
    }

    public static class MV extends HEHydroPumpTileEntity {

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
            return new MV();
        }
    }

    public static class HV extends HEHydroPumpTileEntity {

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
            return new HV();
        }
    }

    public static class EV extends HEHydroPumpTileEntity {

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
            return new EV();
        }
    }

    public static class IV extends HEHydroPumpTileEntity {

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
            return new IV();
        }
    }

    private static Textures.BlockIcons.CustomIcon textureScreenPumpON;
    private static Textures.BlockIcons.CustomIcon textureScreenPumpOFF;
    private static Textures.BlockIcons.CustomIcon textureScreenArrowUpAnimated;

    private final int blockTextureIndex = getCasingTextureId();

    public HEHydroPumpTileEntity(int tierId) {
        super("he_pump_" + GT_Values.VN[tierId].toLowerCase());
    }

    public HEHydroPumpTileEntity(int blockId, int tierId) {
        super(blockId + tierId, "he_pump_" + GT_Values.VN[tierId].toLowerCase(), "Hydro Pump (" + GT_Values.VN[tierId] + ")");
    }

    @Override
    public void onTick() {
        int requiredWater = (int)(GT_Values.V[getTier()] * HEConfig.milliBucketPerEU);
        for(FluidStack fluidStack : getStoredFluids()) {
            if(fluidStack.getFluid().getID() == FluidRegistry.WATER.getID()) {
                final int consumedWater = Math.min(fluidStack.amount, requiredWater);
                requiredWater -= consumedWater;
                fluidStack.amount -= consumedWater;
            }
        }
        if(requiredWater > 0) {
            stopMachine();
            return;
        }

        float pumpedWater = getVoltage() * HEConfig.milliBucketPerEU;
        pumpedWater *= getEfficiencyModifier();
        pumpedWater *= ((float)getCurrentEfficiency(null)) / 100_00.0f;
        final FluidStack fluidStack = new FluidStack(HE.pressurizedWater, (int)pumpedWater);
        HE.pressurizedWater.setPressure(fluidStack, getPressure());
        addOutput(fluidStack);
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
        textureScreenPumpOFF = new Textures.BlockIcons.CustomIcon("iconsets/he_pump");
        textureScreenPumpON = new Textures.BlockIcons.CustomIcon("iconsets/he_pump_active");
        textureScreenArrowUpAnimated = new Textures.BlockIcons.CustomIcon("iconsets/he_arrow_up_animated");
        super.registerIcons(blockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, byte side, byte facing, byte colorIndex, boolean isActive, boolean hasRedstoneSignal) {
        if(side == facing) {
            if(isActive) {
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(blockTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenPumpON),
                        new TT_RenderedExtendedFacingTexture(textureScreenArrowUpAnimated)};
            }
            else {
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(blockTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenPumpOFF)};
            }
        }
        else {
            return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(blockTextureIndex)};
        }
    }

    @Override
    public String[] getDescription() {
        return new String[] {
                "Hydro Pump Controller",
                "Controller Block for the Hydro Pump",
                "Consumes EU to pressurize water",
                "Output is pressurized water for Hydro Dams",
                "Requires a Energy, Input, Output and Maintenance Hatch anywhere!",
                "Requires " + getMilliBucketsPerTick() + "mB Water per Tick",
                "Efficiency: " + getEfficiencyModifierInPercent(),
                HE.blueprintHintTecTech,
                "Use Redstone to automate!"
        };
    }

    @Override
    public String[] getStructureDescription(ItemStack itemStack) {
        return new String[] {
                "1 Energy Hatch",
                "1 Fluid Input Hatch",
                "1 Fluid Output Hatch",
                "1 Maintenance Hatch",
                "Fill the rest with " + getCasingName(),
        };
    }

    @Override
    protected long getEnergyConsumption() {
        return - getVoltage();
    }
}

