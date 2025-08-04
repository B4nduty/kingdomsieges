package banduty.kingdomsieges.structure.custom;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.stoneycore.structure.StructureSpawner;
import banduty.stoneycore.util.patterns.StructureMatcher;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
                        state.isOf(Blocks.IRON_TRAPDOOR) &&
                                !state.get(Properties.WATERLOGGED) &&
                                !state.get(Properties.POWERED))
                .matchSlab('B', Blocks.DARK_OAK_SLAB, SlabType.BOTTOM)
                .matchStairsPerpendicular('S', Blocks.DARK_OAK_STAIRS, false);
    }

    @Override
    public Entity createEntity(World world) {
        return ModEntities.RIBAULDEQUIN_ENTITY.get().create(world);
    }

    @Override
    public List<Block> getBlockFinders() {
        return List.of(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_STAIRS);
    }
}
