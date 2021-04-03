package com.sinthoras.hydroenergy.blocks;

import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.mechanics.structure.IStructureDefinition;
import com.github.technus.tectech.mechanics.structure.StructureDefinition;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_Container_MultiMachineEM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_GUIContainer_MultiMachineEM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_MetaTileEntity_MultiblockBase_EM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.render.TT_RenderedExtendedFacingTexture;
import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.config.HEConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static com.github.technus.tectech.mechanics.structure.StructureUtility.*;
import static com.github.technus.tectech.mechanics.structure.StructureUtility.ofBlock;

public abstract class HEHydroPumpTileEntity extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {

    public static class HEHydroPumpTileEntityLV extends HEHydroPumpTileEntity {

        public HEHydroPumpTileEntityLV(String name) {
            super(name);
            blockTextureIndex = steelCasingTextureIndex;
        }

        public HEHydroPumpTileEntityLV(int id, String name, String nameRegional) {
            super(id, name, nameRegional);
            blockTextureIndex = steelCasingTextureIndex;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroPumpTileEntityLV(mName);
        }

        @Override
        protected int getTier() {
            return 1;
        }

        private static final int solidSteelCasingMeta = 0;
        private static final int steelCasingTextureIndex = 16;
        private static final IStructureDefinition<HEHydroPumpTileEntity> multiblockDefinition = getStructureDefinition(GregTech_API.sBlockCasings2, solidSteelCasingMeta, steelCasingTextureIndex);

        @Override
        public IStructureDefinition<HEHydroPumpTileEntity> getStructure_EM() {
            return multiblockDefinition;
        }

        private static final String[] chatDescription = new String[] {
                "1 Energy Hatch",
                "1 Fluid Input Hatch",
                "1 Fluid Output Hatch",
                "1 Maintenance Hatch",
                "Fill the rest with Solid Steel Casings",
        };

        @Override
        public String[] getStructureDescription(ItemStack itemStack) {
            return chatDescription;
        }

        private final static String[] mouseOverDescription = new String[] {
                "Hydro Pump Controller",
                "Controller Block for the Hydro Pump",
                "Consumes EU to pressurize water",
                "Output is pressurized water for Hydro Dams",
                "Requires an Energy and Output Hatch in the center row!",
                "Requires " + ((int)(32 * HEConfig.milliBucketPerEU)) + "mB Water per Tick",
                "Efficiency: " + HEConfig.efficiency[0],
                HE.blueprintHintTecTech,
                "Use Redstone to automate!"
        };

        @Override
        public String[] getDescription() {
            return mouseOverDescription;
        }
    }

    public static class HEHydroPumpTileEntityMV extends HEHydroPumpTileEntity {

        public HEHydroPumpTileEntityMV(String name) {
            super(name);
            blockTextureIndex = aluminiumCasingTextureIndex;
        }

        public HEHydroPumpTileEntityMV(int id, String name, String nameRegional) {
            super(id, name, nameRegional);
            blockTextureIndex = aluminiumCasingTextureIndex;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroPumpTileEntityMV(mName);
        }

        @Override
        protected int getTier() {
            return 2;
        }

        private static final int aluminiumCasingMeta = 1;
        private static final int aluminiumCasingTextureIndex = 17;
        private static final IStructureDefinition<HEHydroPumpTileEntity> multiblockDefinition = getStructureDefinition(GregTech_API.sBlockCasings2, aluminiumCasingMeta, aluminiumCasingTextureIndex);

        @Override
        public IStructureDefinition<HEHydroPumpTileEntity> getStructure_EM() {
            return multiblockDefinition;
        }

        private static final String[] chatDescription = new String[] {
                "1 Energy Hatch",
                "1 Fluid Input Hatch",
                "1 Fluid Output Hatch",
                "1 Maintenance Hatch",
                "Fill the rest with Frost Proof Casings",
        };

        @Override
        public String[] getStructureDescription(ItemStack itemStack) {
            return chatDescription;
        }

        private final static String[] mouseOverDescription = new String[] {
                "Hydro Pump Controller",
                "Controller Block for the Hydro Pump",
                "Consumes EU to pressurize water",
                "Output is pressurized water for Hydro Dams",
                "Requires an Energy and Output Hatch in the center row!",
                "Requires " + ((int)(128 * HEConfig.milliBucketPerEU)) + "mB Water per Tick",
                "Efficiency: " + HEConfig.efficiency[1],
                HE.blueprintHintTecTech,
                "Use Redstone to automate!"
        };

        @Override
        public String[] getDescription() {
            return mouseOverDescription;
        }
    }

    public static class HEHydroPumpTileEntityHV extends HEHydroPumpTileEntity {

        public HEHydroPumpTileEntityHV(String name) {
            super(name);
            blockTextureIndex = stainlessSteelCasingTextureIndex;
        }

        public HEHydroPumpTileEntityHV(int id, String name, String nameRegional) {
            super(id, name, nameRegional);
            blockTextureIndex = stainlessSteelCasingTextureIndex;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroPumpTileEntityHV(mName);
        }

        @Override
        protected int getTier() {
            return 3;
        }

        private static final int stainlessSteelCasingMeta = 1;
        private static final int stainlessSteelCasingTextureIndex = 58;
        private static final IStructureDefinition<HEHydroPumpTileEntity> multiblockDefinition = getStructureDefinition(GregTech_API.sBlockCasings4, stainlessSteelCasingMeta, stainlessSteelCasingTextureIndex);

        @Override
        public IStructureDefinition<HEHydroPumpTileEntity> getStructure_EM() {
            return multiblockDefinition;
        }

        private static final String[] chatDescription = new String[] {
                "1 Energy Hatch",
                "1 Fluid Input Hatch",
                "1 Fluid Output Hatch",
                "1 Maintenance Hatch",
                "Fill the rest with Clean Stainless Steel Casings",
        };

        @Override
        public String[] getStructureDescription(ItemStack itemStack) {
            return chatDescription;
        }

        private final static String[] mouseOverDescription = new String[] {
                "Hydro Pump Controller",
                "Controller Block for the Hydro Pump",
                "Consumes EU to pressurize water",
                "Output is pressurized water for Hydro Dams",
                "Requires an Energy and Output Hatch in the center row!",
                "Requires " + ((int)(512 * HEConfig.milliBucketPerEU)) + "mB Water per Tick",
                "Efficiency: " + HEConfig.efficiency[2],
                HE.blueprintHintTecTech,
                "Use Redstone to automate!"
        };

        @Override
        public String[] getDescription() {
            return mouseOverDescription;
        }
    }

    private static Textures.BlockIcons.CustomIcon textureScreenPumpON;
    private static Textures.BlockIcons.CustomIcon textureScreenPumpOFF;
    private static Textures.BlockIcons.CustomIcon textureScreenArrowUpAnimated;
    protected int blockTextureIndex = 16;

    private int countOfHatches = 0;

    protected static IStructureDefinition<HEHydroPumpTileEntity> getStructureDefinition(Block casingBlock, int casingMeta, int blockTextureIndex) {
        return StructureDefinition
                .<HEHydroPumpTileEntity>builder()
                .addShape("main",
                        transpose(new String[][]{
                                {"CCC", "CCC", "CCC"},
                                {"C~C", "H H", "HHH"},
                                {"CCC", "CCC", "CCC"}
                        })
                ).addElement(
                        'H',
                        ofChain(
                                onElementPass(x -> x.countOfHatches++,
                                        ofHatchAdder(
                                                HEHydroPumpTileEntity::addClassicToMachineList, blockTextureIndex,
                                                casingBlock, casingMeta
                                        )
                                ),
                                ofBlock(
                                        casingBlock, casingMeta
                                )
                        )
                ).addElement(
                        'C',
                        ofBlock(
                                casingBlock, casingMeta
                        )
                ).build();
    }

    public HEHydroPumpTileEntity(String name) {
        super(name);
    }

    public HEHydroPumpTileEntity(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    @Override
    protected boolean checkMachine_EM(IGregTechTileEntity gregTechTileEntity, ItemStack itemStack) {
        countOfHatches = 0;
        return structureCheck_EM("main", 1, 1, 0) && countOfHatches == 4;
    }

    @Override
    public void construct(ItemStack itemStack, boolean hintsOnly) {
        structureBuild_EM("main", 1,1,0, hintsOnly, itemStack);
    }

    @Override
    public boolean checkRecipe_EM(ItemStack stack) {
        mMaxProgresstime = 1;
        mEUt = -getTierVoltage(); // TODO: check voltage for limits and good practices
        mEfficiencyIncrease = 100_00;
        return true;
    }

    protected abstract int getTier();

    protected float getTierEfficiency() {
        return (float)HEConfig.efficiency[getTier()];
    }

    protected float getTierPressure() {
        return (float)HEConfig.pressure[getTier()];
    }

    protected int getTierVoltage() {
        return (int)GT_Values.V[getTier()];
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        mProgresstime = 0;
        if(getBaseMetaTileEntity().isAllowedToWork() && energyFlowOnRunningTick(stack, false)) {
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
                return false;
            }

            float pumpedWater = getTierVoltage() * HEConfig.milliBucketPerEU;
            pumpedWater *= getTierEfficiency();
            pumpedWater *= ((float)getCurrentEfficiency(null)) / 100_00.0f;
            final FluidStack fluidStack = new FluidStack(HE.pressurizedWater, (int)pumpedWater);
            HE.pressurizedWater.setPressure(fluidStack, getTierPressure());
            addOutput(fluidStack);
        }
        return true;
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
}

