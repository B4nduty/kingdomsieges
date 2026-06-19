package banduty.kingdomsieges.structure.custom;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.stoneycore.structure.StructureSpawner;
import banduty.stoneycore.util.patterns.StructureMatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.List;

public class MantletStructure extends StructureSpawner {
    @Override
    public String[][] getBaseAisles() {
        return new String[][]{
                // Depth 1
                {
                        "    ", // Height 2
                        "    ", // Height 1
                        "   T" // Height 0
                },
                // Depth 2
                {
                        "   L",
                        "   P",
                        "I  L"
                },
                // Depth 3
                {
                        "   L",
                        "   P",
                        "BBBL"
                },
                // Depth 4
                {
                        "   L",
                        "   P",
                        "I  L"
                },
                // Depth 5
                {
                        "    ",
                        "    ",
                        "   D"
                }
        };
    }

    @Override
    public void applyKeyMatcher(BlockPatternBuilder builder, Direction dir) {
        new StructureMatcher(builder, dir)
                .matchLog('L', Blocks.DARK_OAK_LOG,
                        dir.getAxis() == Direction.Axis.X ? Direction.Axis.X : Direction.Axis.Z)
                .match('I', state ->
                        state.is(Blocks.IRON_TRAPDOOR) &&
                                !state.getValue(BlockStateProperties.WATERLOGGED) &&
                                !state.getValue(BlockStateProperties.POWERED))
                .matchSlab('B', Blocks.DARK_OAK_SLAB, SlabType.BOTTOM)
                .match('P', Blocks.DARK_OAK_PLANKS)
                .matchTrapdoorRelative('T', Blocks.DARK_OAK_TRAPDOOR, true)
                .matchTrapdoorRelative('D', Blocks.DARK_OAK_TRAPDOOR, false);
    }

    @Override
    public Entity createEntity(Level level, Direction dir) {
        Entity entity = ModEntities.MANTLET_ENTITY.get().create(level);

        if (entity != null) {
            float yaw = dir.toYRot();
            entity.setYRot(yaw - 90);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(entity.getYRot());
        }

        return entity;
    }

    @Override
    public List<Block> getBlockFinders() {
        return List.of(Blocks.DARK_OAK_LOG, Blocks.IRON_TRAPDOOR, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_TRAPDOOR);
    }
}
