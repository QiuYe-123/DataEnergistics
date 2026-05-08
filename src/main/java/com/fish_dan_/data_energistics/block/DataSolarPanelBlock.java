package com.fish_dan_.data_energistics.block;

import appeng.block.AEBaseBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.fish_dan_.data_energistics.blockentity.DataSolarPanelBlockEntity;
import com.fish_dan_.data_energistics.registry.ModBlockEntities;
import com.fish_dan_.data_energistics.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DataSolarPanelBlock extends AEBaseBlock implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public DataSolarPanelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DataSolarPanelBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DataSolarPanelBlockEntity solarPanel) {
            MenuOpener.open(ModMenus.DATA_SOLAR_PANEL.get(), player, MenuLocators.forBlockEntity(solarPanel));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlockEntities.DATA_SOLAR_PANEL_BLOCK_ENTITY.get()) {
            return null;
        }

        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (blockEntity instanceof DataSolarPanelBlockEntity solarPanel) {
                solarPanel.serverTick();
            }
        };
    }
}
