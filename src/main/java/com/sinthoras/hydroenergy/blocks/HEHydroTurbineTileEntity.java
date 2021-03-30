package com.sinthoras.hydroenergy.blocks;

import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.mechanics.structure.IStructureDefinition;
import com.github.technus.tectech.mechanics.structure.StructureDefinition;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_Container_MultiMachineEM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_GUIContainer_MultiMachineEM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_MetaTileEntity_MultiblockBase_EM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.render.TT_RenderedExtendedFacingTexture;
import com.sinthoras.hydroenergy.HE;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static com.github.technus.tectech.mechanics.structure.StructureUtility.*;
import static com.github.technus.tectech.mechanics.structure.StructureUtility.ofBlock;

public class HEHydroTurbineTileEntity extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {

    /*
    TODO:
        - Handle Tiers
     */

    private static Textures.BlockIcons.CustomIcon textureScreenTurbineON;
    private static Textures.BlockIcons.CustomIcon textureScreenTurbineOFF;
    private static Textures.BlockIcons.CustomIcon textureScreenArrowDownAnimated;
    private final static int steelTextureIndex = 16;
    private final static int solidSteelCasingMeta = 0;

    private int countOfHatches = 0;

    private static final IStructureDefinition<HEHydroTurbineTileEntity> multiblockDefinition = StructureDefinition
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
                                            HEHydroTurbineTileEntity::addClassicToMachineList, steelTextureIndex,
                                            GregTech_API.sBlockCasings2, solidSteelCasingMeta
                                    )
                            ),
                            ofBlock(
                                    GregTech_API.sBlockCasings2, solidSteelCasingMeta
                            )
                    )
            ).addElement(
                    'C',
                    ofBlock(
                            GregTech_API.sBlockCasings2, solidSteelCasingMeta
                    )
            ).build();

    public HEHydroTurbineTileEntity(String name) {
        super(name);
    }

    public HEHydroTurbineTileEntity(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new HEHydroTurbineTileEntity(mName);
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
    public IStructureDefinition<HEHydroTurbineTileEntity> getStructure_EM() {
        return multiblockDefinition;
    }

    @Override
    public boolean checkRecipe_EM(ItemStack stack) {
        mMaxProgresstime = 1;
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick) {
        if(getBaseMetaTileEntity().isServerSide()) {
            mMaxProgresstime = 1;
        }
        super.onPostTick(baseMetaTileEntity, tick);
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        mProgresstime = 0;
        if(getBaseMetaTileEntity().isAllowedToWork()) {
            // TODO: move to config

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
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenTurbineON),
                        new TT_RenderedExtendedFacingTexture(textureScreenArrowDownAnimated)};
            }
            else {
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenTurbineOFF)};
            }
        }
        else {
            return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex)};
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

