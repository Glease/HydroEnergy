package com.sinthoras.hydroenergy.blocks;

import com.github.technus.tectech.mechanics.constructable.IConstructable;
import com.github.technus.tectech.mechanics.structure.IStructureDefinition;
import com.github.technus.tectech.mechanics.structure.StructureDefinition;
import com.github.technus.tectech.thing.metaTileEntity.multi.base.GT_MetaTileEntity_MultiblockBase_EM;
import com.sinthoras.hydroenergy.HETags;
import com.sinthoras.hydroenergy.config.HEConfig;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static com.github.technus.tectech.mechanics.structure.StructureUtility.*;
import static com.github.technus.tectech.mechanics.structure.StructureUtility.ofBlock;

public abstract class HETieredTileEntity extends GT_MetaTileEntity_MultiblockBase_EM implements IConstructable {

    private int countOfHatches = 0;
    private IStructureDefinition<HETieredTileEntity> multiblockDefinition = null;

    protected HETieredTileEntity(int blockId, String name, String nameRegional) {
        super(blockId, name, nameRegional);
    }

    protected HETieredTileEntity(String name) {
        super(name);
    }

    protected abstract int getTier();

    protected Block getCasingBlock() {
        switch(getTier()) {
            default:
            case 1:
            case 2:
                return GregTech_API.sBlockCasings2;
            case 3:
            case 4:
            case 5:
                return GregTech_API.sBlockCasings4;
        }
    }

    protected int getCasingMeta() {
        switch(getTier()) {
            default:
            case 1:
            case 5:
                return 0;
            case 2:
            case 3:
                return 1;
            case 4:
                return 2;
        }
    }

    protected int getCasingTextureId() {
        final Block casingBlock = getCasingBlock();
        final int metaId = getCasingMeta();
        if(casingBlock == GregTech_API.sBlockCasings1) {
            return metaId;
        }
        if(casingBlock == GregTech_API.sBlockCasings2) {
            return 16 + metaId;
        }
        if(casingBlock == GregTech_API.sBlockCasings3) {
            return 2 * 16 + metaId;
        }
        if(casingBlock == GregTech_API.sBlockCasings4) {
            return 3 * 16 + metaId;
        }
        if(casingBlock == GregTech_API.sBlockCasings5) {
            return 4 * 16 + metaId;
        }
        if(casingBlock == GregTech_API.sBlockCasings6) {
            return 5 * 16 + metaId;
        }
        if(casingBlock == GregTech_API.sBlockCasings8) {
            return 7 * 16 + metaId;
        }
        return 0;
    }

    protected String getCasingName() {
        return GT_LanguageManager.getTranslation(getCasingBlock().getUnlocalizedName() + "." + getCasingMeta() + ".name");
    }

    protected long getVoltage() {
        return GT_Values.V[getTier()];
    }

    protected float getPressure() {
        return getTier() * HEConfig.pressureIncreasePerTier;
    }

    protected float getEfficiencyModifier() {
        return 1.0f - getTier() * HEConfig.efficiencyLossPerTier;
    }

    protected String getEfficiencyModifierInPercent() {
        return String.format("%.0f", getEfficiencyModifier() * 100.0f) + "%";
    }

    protected int getMilliBucketsPerTick() {
        return (int)(getVoltage() * HEConfig.milliBucketPerEU);
    }

    protected abstract void onTick();

    @Override
    public boolean onRunningTick(ItemStack stack) {
        mProgresstime = 0;
        if (getBaseMetaTileEntity().isAllowedToWork() && energyFlowOnRunningTick(stack, false)) {
            onTick();
        }
        return true;
    }

    protected abstract long getEnergyConsumption();

    @Override
    public boolean checkRecipe_EM(ItemStack stack) {
        mMaxProgresstime = 1;
        mEUt = (int)getEnergyConsumption();
        mEfficiencyIncrease = 100_00;
        return true;
    }

    @Override
    protected boolean checkMachine_EM(IGregTechTileEntity gregTechTileEntity, ItemStack itemStack) {
        countOfHatches = 0;
        return structureCheck_EM(HETags.mainStructure, 1, 1, 0) && countOfHatches == 4;
    }

    @Override
    public void construct(ItemStack itemStack, boolean hintsOnly) {
        structureBuild_EM(HETags.mainStructure, 1,1,0, hintsOnly, itemStack);
    }

    @Override
    public IStructureDefinition<HETieredTileEntity> getStructure_EM() {
        if(multiblockDefinition == null) {
            final Block casingBlock = getCasingBlock();
            final int casingMeta = getCasingMeta();
            multiblockDefinition = StructureDefinition
                    .<HETieredTileEntity>builder()
                    .addShape(HETags.mainStructure,
                            transpose(new String[][]{
                                    {"CCC", "CCC", "CCC"},
                                    {"C~C", "C C", "CCC"},
                                    {"CCC", "CCC", "CCC"}
                            })
                    ).addElement(
                            'C',
                            ofChain(
                                    onElementPass(x -> x.countOfHatches++,
                                            ofHatchAdder(
                                                    HETieredTileEntity::addClassicToMachineList, getCasingTextureId(),
                                                    casingBlock, casingMeta
                                            )
                                    ),
                                    ofBlock(
                                            casingBlock, casingMeta
                                    )
                            )
                    ).build();
        }
        return multiblockDefinition;
    }
}
