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

public class RibauldequinStructure extends StructureSpawner {
    @Override
    public String[][] getBaseAisles() {
        return new String[][]{
                {
                        " R    ",
                        "  T   "
                },
                {
                        "  R   ",
                        "  L   "
                },
                {
                        "RRR   ",
                        "  LGSB"
                },
                {
                        "  R   ",
                        "  L   "
                },
                {
                        " R    ",
                        "  D   "
                }
        };
    }

    @Override
    public void applyKeyMatcher(BlockPatternBuilder builder, Direction dir) {
        new StructureMatcher(builder, dir)
                .matchLog('G', Blocks.DARK_OAK_LOG,
                        dir.getAxis() == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z)
                .matchLog('L', Blocks.DARK_OAK_LOG,
                        dir.getAxis() == Direction.Axis.X ? Direction.Axis.X : Direction.Axis.Z)
                .matchTrapdoorRelative('T', Blocks.DARK_OAK_TRAPDOOR, true)
                .matchTrapdoorRelative('D', Blocks.DARK_OAK_TRAPDOOR, false)
                .match('R', state ->
                        state.is(Blocks.IRON_TRAPDOOR) &&
                                !state.getValue(BlockStateProperties.WATERLOGGED) &&
                                !state.getValue(BlockStateProperties.POWERED))
                .matchSlab('B', Blocks.DARK_OAK_SLAB, SlabType.BOTTOM)
                .matchStairsPerpendicular('S', Blocks.DARK_OAK_STAIRS, false);
    }

    @Override
    public Entity createEntity(Level level, Direction dir) {
        Entity entity = ModEntities.RIBAULDEQUIN_ENTITY.create(level);

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
        return List.of(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_STAIRS);
    }
}
