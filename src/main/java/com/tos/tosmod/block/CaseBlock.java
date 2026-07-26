package com.tos.tosmod.block;

import com.tos.tosmod.block.entity.CaseBlockEntity;
import com.tos.tosmod.component.CaseDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Bloco genérico de Case. UM modelo visual diferente (notebook gamer, notebook fino,
 * torre, all-in-one, servidor) = UMA instância nova desta classe no registro (ModBlocks),
 * cada uma apontando pra uma CaseDefinition. Nenhum código novo precisa ser escrito
 * quando você adicionar um modelo 3D/textura nova - só a textura/modelo do bloco (JSON)
 * e essa linha de registro.
 */
public class CaseBlock extends BaseEntityBlock {

    private final CaseDefinition definition;

    public CaseBlock(Properties properties, CaseDefinition definition) {
        super(properties);
        this.definition = definition;
    }

    public CaseDefinition getDefinition() {
        return definition;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CaseBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Fase 1: usa o modelo padrão do blockstate (seus modelos 3D entram aqui via JSON,
        // sem precisar de um BlockEntityRenderer customizado nesta fase).
        return RenderShape.MODEL;
    }

    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Contagem de crash (PSU) e cálculo de temperatura (Fase 2) rodam aqui, uma vez por tick.
        if (level.isClientSide()) {
            return null; // toda essa lógica é de servidor, cliente só reflete o estado salvo
        }
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof CaseBlockEntity caseEntity) {
                caseEntity.tick();
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!definition.hasIntegratedScreen()) {
            if (!level.isClientSide()) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                        "Esta case não tem tela integrada - conecte um monitor com um cabo de vídeo."), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (level.isClientSide()) {
            // TosScreens decide entre o Desktop (Fase 9, se já tem SO instalado) e o
            // terminal cru (Fase 3/4, sem SO) - ela mesma lê o estado já sincronizado do
            // BlockEntity e manda comandos pro servidor via RunLuaCommandPayload.
            com.tos.tosmod.client.screen.TosScreens.open(pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
