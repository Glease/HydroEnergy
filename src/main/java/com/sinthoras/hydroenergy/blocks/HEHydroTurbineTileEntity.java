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
import net.minecraftforge.fluids.FluidStack;

import static com.github.technus.tectech.mechanics.structure.StructureUtility.*;
import static com.github.technus.tectech.mechanics.structure.StructureUtility.ofBlock;

public abstract class HEHydroTurbineTileEntity extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {

    public static class HEHydroTurbineTileEntityLV extends HEHydroTurbineTileEntity {

        public HEHydroTurbineTileEntityLV(String name) {
            super(name);
            blockTextureIndex = steelCasingTextureIndex;
        }

        public HEHydroTurbineTileEntityLV(int id, String name, String nameRegional) {
            super(id, name, nameRegional);
            blockTextureIndex = steelCasingTextureIndex;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntityLV(mName);
        }

        @Override
        public int getTier() {
            return 1;
        }

        private static final int solidSteelCasingMeta = 0;
        private static final int steelCasingTextureIndex = 16;
        private static final IStructureDefinition<HEHydroTurbineTileEntity> multiblockDefinition = getStructureDefinition(GregTech_API.sBlockCasings2, solidSteelCasingMeta, steelCasingTextureIndex);

        @Override
        public IStructureDefinition<HEHydroTurbineTileEntity> getStructure_EM() {
            return multiblockDefinition;
        }
    }

    public static class HEHydroTurbineTileEntityMV extends HEHydroTurbineTileEntity {

        public HEHydroTurbineTileEntityMV(String name) {
            super(name);
            blockTextureIndex = aluminiumCasingTextureIndex;
        }

        public HEHydroTurbineTileEntityMV(int id, String name, String nameRegional) {
            super(id, name, nameRegional);
            blockTextureIndex = aluminiumCasingTextureIndex;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntityMV(mName);
        }

        @Override
        public int getTier() {
            return 2;
        }

        private static final int aluminiumCasingMeta = 1;
        private static final int aluminiumCasingTextureIndex = 17;
        private static final IStructureDefinition<HEHydroTurbineTileEntity> multiblockDefinition = getStructureDefinition(GregTech_API.sBlockCasings2, aluminiumCasingMeta, aluminiumCasingTextureIndex);

        @Override
        public IStructureDefinition<HEHydroTurbineTileEntity> getStructure_EM() {
            return multiblockDefinition;
        }
    }

    public static class HEHydroTurbineTileEntityHV extends HEHydroTurbineTileEntity {

        public HEHydroTurbineTileEntityHV(String name) {
            super(name);
            blockTextureIndex = stainlessSteelCasingTextureIndex;
        }

        public HEHydroTurbineTileEntityHV(int id, String name, String nameRegional) {
            super(id, name, nameRegional);
            blockTextureIndex = stainlessSteelCasingTextureIndex;
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
            return new HEHydroTurbineTileEntityHV(mName);
        }

        @Override
        public int getTier() {
            return 3;
        }

        private static final int stainlessSteelCasingMeta = 1;
        private static final int stainlessSteelCasingTextureIndex = 58;
        private static final IStructureDefinition<HEHydroTurbineTileEntity> multiblockDefinition = getStructureDefinition(GregTech_API.sBlockCasings4, stainlessSteelCasingMeta, stainlessSteelCasingTextureIndex);

        @Override
        public IStructureDefinition<HEHydroTurbineTileEntity> getStructure_EM() {
            return multiblockDefinition;
        }
    }

    private static Textures.BlockIcons.CustomIcon textureScreenTurbineON;
    private static Textures.BlockIcons.CustomIcon textureScreenTurbineOFF;
    private static Textures.BlockIcons.CustomIcon textureScreenArrowDownAnimated;
    protected int blockTextureIndex = 16;

    private int countOfHatches = 0;

    protected static IStructureDefinition<HEHydroTurbineTileEntity> getStructureDefinition(Block casingBlock, int casingMeta, int blockTextureIndex) {
        return StructureDefinition
                .<HEHydroTurbineTileEntity>builder()
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
                                                HEHydroTurbineTileEntity::addClassicToMachineList, blockTextureIndex,
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

    public HEHydroTurbineTileEntity(String name) {
        super(name);
    }

    public HEHydroTurbineTileEntity(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    @Override
    protected boolean checkMachine_EM(IGregTechTileEntity gregTechTileEntity, ItemStack itemStack) {
        countOfHatches = 0;
        return structureCheck_EM("main", 1, 1, 0) && countOfHatches == 3;
    }

    @Override
    public void construct(ItemStack itemStack, boolean hintsOnly) {
        structureBuild_EM("main", 1,1,0, hintsOnly, itemStack);
    }

    @Override
    public boolean checkRecipe_EM(ItemStack stack) {
        mMaxProgresstime = 1;
        mEUt = getTierVoltage();
        mEfficiencyIncrease = 100_00;
        return true;
    }

    protected float getTierEfficiency() {
        return (float)HEConfig.efficiency[getTier()];
    }

    protected int getTierVoltage() {
        return (int)GT_Values.V[getTier()];
    }

    protected abstract int getTier();

    @Override
    public boolean onRunningTick(ItemStack stack) {
        mProgresstime = 0;
        if(getBaseMetaTileEntity().isAllowedToWork() && energyFlowOnRunningTick(stack, false)) {
            final int consumableWaterPerTick = (int)(getTierVoltage() * HEConfig.milliBucketPerEU);
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
            producedEU *= getTierEfficiency();
            producedEU *= (float)getCurrentEfficiency(null) / 100_00.0f;
            addEnergyOutput_EM((int)producedEU, 1);
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

    private final static String[] mouseOverDescription = new String[] {
            "Hydro Turbine Controller",
            "Controller Block for the Hydro Turbine",
            "Consumes pressurize water to produce EU",
            "Input is pressurized water from Hydro Dams",
            "Requires a Dynamo and Input Hatch in the center row!",
            HE.blueprintHintTecTech,
            "Use Redstone to automate!"
    };

    public String[] getDescription() {
        return mouseOverDescription;
    }

    private static final String[] chatDescription = new String[] {
            "1 Dynamo Hatch",
            "1 Fluid Input Hatch",
            "1 Maintenance Hatch",
            "Fill the rest with Solid Steel Casings",
    };

    @Override
    public String[] getStructureDescription(ItemStack itemStack) {
        return chatDescription;
    }
}

