package com.sinthoras.hydroenergy.blocks;

import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.mechanics.structure.IStructureDefinition;
import com.github.technus.tectech.mechanics.structure.StructureDefinition;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_MetaTileEntity_MultiblockBase_EM;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.render.TT_RenderedExtendedFacingTexture;
import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HETags;
import com.sinthoras.hydroenergy.client.gui.HEGuiHandler;
import com.sinthoras.hydroenergy.client.gui.HEHydroDamWaterGuiContainer;
import com.sinthoras.hydroenergy.config.HEConfig;
import com.sinthoras.hydroenergy.network.container.HEHydroDamWaterContainer;
import com.sinthoras.hydroenergy.server.HEServer;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import static com.github.technus.tectech.mechanics.structure.StructureUtility.*;

public class HEHydroDamTileEntity extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {

    /*
    TODO:
        - Clean up empty fluid stacks on input (probably automatic: test it)
        - Not ticking
        - Screwdriver GUI has inventory overlap
     */

    private static Textures.BlockIcons.CustomIcon Screen;
    private final static int steelTextureIndex = 16;
    private final static int solidSteelCasingMeta = 0;
    private int waterId = -1;
    private long waterStored = 0;
    private long waterCapacity = 0;
    private int waterPerTickIn = 0;
    private int waterPerTickOut = 0;

    private static final IStructureDefinition<HEHydroDamTileEntity> multiblockDefinition = StructureDefinition
        .<HEHydroDamTileEntity>builder()
        .addShape("main",
            transpose(new String[][]{
                {"HHHHH", "CCCCC", "CCCCC", "CCCCC", "CCCCC"},
                {"HHHHH", "C   C", "C   C", "C   C", "C   C"},
                {"HHHHH", "C   C", "C   C", "C   C", "C   C"},
                {"HH~HH", "C   C", "C   C", "C   C", "C   C"},
                {"HHHHH", "CCCCC", "CCCCC", "CCCCC", "CCCCC"}
            })
        ).addElement(
            'H',
            ofChain(
                ofHatchAdder(
                    HEHydroDamTileEntity::addClassicToMachineList, steelTextureIndex,
                    GregTech_API.sBlockCasings2, solidSteelCasingMeta
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

    public HEHydroDamTileEntity(String name) {
        super(name);

        // Disable maintenance requirements at block placement
        mWrench = true;
        mScrewdriver = true;
        mSoftHammer = true;
        mHardHammer = true;
        mSolderingTool = true;
        mCrowbar = true;
    }

    public HEHydroDamTileEntity(int id, String name, String nameRegional) {
        super(id, name, nameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new HEHydroDamTileEntity(mName);
    }

    @Override
    protected boolean checkMachine_EM(IGregTechTileEntity gregTechTileEntity, ItemStack itemStack) {
        return structureCheck_EM("main", 2, 3, 0);
    }

    @Override
    public void construct(ItemStack itemStack, boolean hintsOnly) {
        structureBuild_EM("main", 2,3,0, hintsOnly, itemStack);
    }

    @Override
    public IStructureDefinition<HEHydroDamTileEntity> getStructure_EM() {
        return multiblockDefinition;
    }

    @Override
    public boolean checkRecipe_EM(ItemStack stack) {
        mMaxProgresstime = 1;
        return true;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        mProgresstime = 0;
        waterPerTickIn = 0;
        waterPerTickOut = 0;

        waterCapacity = HEServer.instance.getWaterCapacity(waterId);
        waterStored = Math.min(waterStored, waterCapacity);

        int waterLevelOverController = (int) (HEServer.instance.getWaterLevel(waterId) - getBaseMetaTileEntity().getYCoord());
        getStoredFluids().stream().forEach(fluidStack -> {
            if(fluidStack.getFluidID() == HE.pressurizedWater.getID()
                    && HE.pressurizedWater.getPressure(fluidStack) >= waterLevelOverController) {
                long canStore = Math.min(waterCapacity - waterStored, fluidStack.amount);
                fluidStack.amount -= canStore;
                waterStored += canStore;
                waterPerTickIn += canStore;
                if(fluidStack.amount == 0) {
                    // TODO: delete fluid stack from hatch? Or is this done automatically?
                }
            }
        });

        int mBPerTickOut = (int)Math.min(HEConfig.damDrainPerSecond, waterStored) * getCurrentEfficiency(null);
        if(mBPerTickOut > 0) {
            if(addOutput(new FluidStack(HE.pressurizedWater, mBPerTickOut))) {
                waterStored -= mBPerTickOut;
                waterPerTickOut += mBPerTickOut;
            }
        }

        if(getBaseMetaTileEntity().getWorld().isRaining()) {
            waterStored += (long)(HEServer.instance.getRainedOnBlocks(waterId) * HEConfig.waterBonusPerSurfaceBlockPerRainTick);
            waterStored = Math.min(waterStored, waterCapacity);
        }
        return true;
    }

    @Override
    public Object getServerGUI(int id, InventoryPlayer playerInventory, IGregTechTileEntity baseMetaTileEntity) {
        return new HEHydroDamWaterContainer(playerInventory, baseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int id, InventoryPlayer playerInventory, IGregTechTileEntity baseMetaTileEntity) {
        return new HEHydroDamWaterGuiContainer(playerInventory, baseMetaTileEntity, getLocalName(), "EMDisplay.png");
    }

    @Override
    public void onScrewdriverRightClick(byte side, EntityPlayer player, float blockX, float blockY, float blockZ) {
        if(!player.isSneaking()) {
            if (getBaseMetaTileEntity().isServerSide()) {
                FMLNetworkHandler.openGui(player, HETags.MODID, HEGuiHandler.HydroDamConfigurationGuiId, getBaseMetaTileEntity().getWorld(),
                        getBaseMetaTileEntity().getXCoord(), getBaseMetaTileEntity().getYCoord(), getBaseMetaTileEntity().getZCoord());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister blockIconRegister) {
        Screen = new Textures.BlockIcons.CustomIcon("iconsets/he_dam");
        super.registerIcons(blockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, byte side, byte facing, byte colorIndex, boolean isActive, boolean hasRedstoneSignal) {
        if(side == facing) {
            return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex),
                    new TT_RenderedExtendedFacingTexture(Screen)};
        }
        else {
            return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(steelTextureIndex)};
        }
    }

    @Override
    public boolean doRandomMaintenanceDamage() {
        // Disable maintenance events
        return true;
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity baseMetaTileEntity) {
        if(waterId == -1 && getBaseMetaTileEntity().isServerSide()) {
            ForgeDirection direction = getExtendedFacing().getDirection();
            final int offsetX;
            final int offsetY = 1;
            final int offsetZ;
            if(direction == ForgeDirection.WEST) {
                offsetX = 2;
                offsetZ = 0;
            }
            else if(direction == ForgeDirection.NORTH) {
                offsetX = 0;
                offsetZ = 2;
            }
            else if(direction == ForgeDirection.EAST) {
                offsetX = -2;
                offsetZ = 0;
            }
            else {
                offsetX = 0;
                offsetZ = -2;
            }
            waterId = HEServer.instance.onPlacecontroller(getBaseMetaTileEntity().getWorld().provider.dimensionId,
                    getBaseMetaTileEntity().getXCoord(),
                    getBaseMetaTileEntity().getYCoord(),
                    getBaseMetaTileEntity().getZCoord(),
                    getBaseMetaTileEntity().getXCoord() + offsetX,
                    getBaseMetaTileEntity().getYCoord() + offsetY,
                    getBaseMetaTileEntity().getZCoord() + offsetZ);
            markDirty();
        }
        super.onFirstTick_EM(baseMetaTileEntity);
    }

    @Override
    public void onRemoval() {
        if(getBaseMetaTileEntity().isServerSide()) {
            HEServer.instance.onBreakController(waterId);
        }
        super.onRemoval();
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        super.saveNBTData(compound);
        compound.setInteger(HETags.waterId, waterId);
        compound.setLong(HETags.waterStored, waterStored);
        compound.setLong(HETags.waterCapacity, waterCapacity);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        super.loadNBTData(compound);
        waterId = compound.getInteger(HETags.waterId);
        waterStored = compound.getLong(HETags.waterStored);
        waterCapacity = compound.getLong(HETags.waterCapacity);
    }

    private final static String[] mouseOverDescription = new String[] {
            "Hydro Dam Controller",
            "Controller Block for the Hydro Dam",
            "Input is pressurized water from Hydro Pumps",
            "Output is pressurized water for Hydro Turbines",
            "Requires an Input and Output Hatch on the front!",
            HE.blueprintHintTecTech
    };

    public String[] getDescription() {
        return mouseOverDescription;
    }

    private static final String[] chatDescription = new String[] {
            "1 Fluid Intput Hatch",
            "1 Fluid Output Hatch",
            "Fill the rest with Solid Steel Casings",
            "No Maintenance Hatch required!"
    };

    @Override
    public String[] getStructureDescription(ItemStack itemStack) {
        return chatDescription;
    }

    public long getWaterStored() {
        return waterStored;
    }

    public long getWaterCapacity() {
        return waterCapacity;
    }

    public int getWaterPerTickIn() {
        return waterPerTickIn;
    }

    public int getWaterPerTickOut() {
        return waterPerTickOut;
    }

    public int getWaterId() {
        return waterId;
    }
}
