package com.sinthoras.hydroenergy.blocks;

import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.mechanics.structure.IStructureDefinition;
import com.github.technus.tectech.mechanics.structure.StructureDefinition;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_MetaTileEntity_MultiblockBase_EM;
import com.sinthoras.hydroenergy.client.gui.HEControllerGui;
import com.sinthoras.hydroenergy.network.container.HEControllerContainer;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import static com.github.technus.tectech.mechanics.structure.StructureUtility.*;

public class HEHydroDamTileEntity extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {

    private final static int steelTextureIndex = 16;
    private final static int solidSteelCasingMeta = 0;

    private static final IStructureDefinition<HEHydroDamTileEntity> STRUCTURE_DEFINITION = StructureDefinition
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
        }

    public HEHydroDamTileEntity(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new HEHydroDamTileEntity(mName);
        }

        @Override
        protected boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
            return this.structureCheck_EM("main", 2, 3, 0);
    }

    @Override
    public void construct(ItemStack itemStack, boolean b) {
        this.structureBuild_EM("main", 2,3,0, b, itemStack);
    }

    @Override
    public String[] getStructureDescription(ItemStack itemStack) {
        return new String[] {"TEST"};
    }

    @Override
    public IStructureDefinition<HEHydroDamTileEntity> getStructure_EM() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkRecipe_EM(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        // water in out logic
        // this.getStoredFluids
        // addOutput
        return true;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new HEControllerContainer(null);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new HEControllerGui(new HEControllerContainer(null));
    }

    public long getEnergyStored() {
        return 1000000;
    }

    public long getEnergyCapacity() {
        return 5000000;
    }

    public long getEnergyPerTickIn() {
        return 0;
    }

    public long getEnergyPerTickOut() {
        return 0;
    }
}
