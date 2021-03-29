package com.sinthoras.hydroenergy.blocks;

import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.mechanics.structure.IStructureDefinition;
import com.github.technus.tectech.mechanics.structure.StructureDefinition;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_MetaTileEntity_MultiblockBase_EM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.render.TT_RenderedExtendedFacingTexture;
import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.server.HEReflection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static com.github.technus.tectech.mechanics.structure.StructureUtility.*;
import static com.github.technus.tectech.mechanics.structure.StructureUtility.ofBlock;

public class HEHydroPumpTileEntity extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {

    /*
    TODO:
        - Get onTick called
        - How to handle Tiers?
     */

    private static Textures.BlockIcons.CustomIcon textureScreenON;
    private static Textures.BlockIcons.CustomIcon textureScreenOff;
    private static Textures.BlockIcons.CustomIcon textureScreenArrowAnimated;
    private final static int steelTextureIndex = 16;
    private final static int solidSteelCasingMeta = 0;

    private int countOfHatches = 0;

    private static final IStructureDefinition<HEHydroPumpTileEntity> multiblockDefinition = StructureDefinition
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
                            HEHydroPumpTileEntity::addClassicToMachineList, steelTextureIndex,
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

    public HEHydroPumpTileEntity(String name) {
        super(name);
    }

    public HEHydroPumpTileEntity(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new HEHydroPumpTileEntity(mName);
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
    public IStructureDefinition<HEHydroPumpTileEntity> getStructure_EM() {
        return multiblockDefinition;
    }

    @Override
    public boolean checkRecipe_EM(ItemStack stack) {
        return true;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if(getBaseMetaTileEntity().isAllowedToWork()) {
            // TODO: move to config
            int waterPressureLV = 8;
            int mbPerTickOutLV = 100 * getCurrentEfficiency(null);
            FluidStack fluidStack = new FluidStack(HE.pressurizedWater, mbPerTickOutLV);
            HE.pressurizedWater.setPressure(fluidStack, waterPressureLV);
            if (HEReflection.invokeDumpFluid(this, fluidStack)) {
                mEUt = 32;
            } else {
                mEUt = 0;
            }
        }
        return true;
    }

    @Override
    public Object getServerGUI(int id, InventoryPlayer playerInventory, IGregTechTileEntity baseMetaTileEntity) {
        return super.getServerGUI(id, playerInventory, baseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int id, InventoryPlayer playerInventory, IGregTechTileEntity baseMetaTileEntity) {
        return super.getClientGUI(id, playerInventory, baseMetaTileEntity);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister blockIconRegister) {
        textureScreenOff = new Textures.BlockIcons.CustomIcon("iconsets/he_pump");
        textureScreenON = new Textures.BlockIcons.CustomIcon("iconsets/he_pump_active");
        textureScreenArrowAnimated = new Textures.BlockIcons.CustomIcon("iconsets/he_arrow_animated");
        super.registerIcons(blockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, byte side, byte facing, byte colorIndex, boolean isActive, boolean hasRedstoneSignal) {
        if(side == facing) {
            if(isActive) {
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenON),
                        new TT_RenderedExtendedFacingTexture(textureScreenArrowAnimated)};
            }
            else {
                return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex),
                        new TT_RenderedExtendedFacingTexture(textureScreenOff)};
            }
        }
        else {
            return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex)};
        }
    }

    private final static String[] mouseOverDescription = new String[] {
            "Hydro Pump Controller",
            "Controller Block for the Hydro Pump",
            "Consumes EU to pressurize water",
            "Output is pressurized water for Hydro Dams",
            "Requires an Energy and Output Hatch in the center row!",
            HE.blueprintHintTecTech,
            "Use Redstone to automate!"
    };

    public String[] getDescription() {
        return mouseOverDescription;
    }

    private static final String[] chatDescription = new String[] {
            "1 Energy Hatch",
            "1 Fluid Output Hatch",
            "1 Maintenance Hatch",
            "Fill the rest with Solid Steel Casings",
    };

    @Override
    public String[] getStructureDescription(ItemStack itemStack) {
        return chatDescription;
    }
}

