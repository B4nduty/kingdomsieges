package banduty.kingdomsieges.structure.custom;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.stoneycore.structure.StructureSpawner;
import banduty.stoneycore.util.patterns.StructureMatcher;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class MangonelStructure extends StructureSpawner {
    @Override
    public String[][] getBaseAisles() {
        return new String[][] {
                {
                        "      ",
                        "      ",
                        " T   T"
                },
                {
                        "  UU  ",
                        " U U  ",
                        " LBUBL"
                },
                {
                        "   W  ",
                        "IBBP  ",
                        " L   L"
                },
                {
                        "  UU  ",
                        " U U  ",
                        " LBUBL"
                },
                {
                        "      ",
                        "      ",
                        " D   D"
                }
        };
    }

    @Override
    public void applyKeyMatcher(BlockPatternBuilder builder, Direction dir) {
        new StructureMatcher(builder, dir)
                .matchLog('U', Blocks.DARK_OAK_LOG, Direction.Axis.Y)
                .matchLog('L', Blocks.DARK_OAK_LOG,
                        dir.getAxis() == Direction.Axis.X ? Direction.Axis.X : Direction.Axis.Z)
                .match('I', Blocks.IRON_BLOCK)
                .match('W', Blocks.HAY_BLOCK)
                .matchTrapdoorRelative('T', Blocks.DARK_OAK_TRAPDOOR, true)
                .matchTrapdoorRelative('D', Blocks.DARK_OAK_TRAPDOOR, false)
                .matchSlab('B', Blocks.DARK_OAK_SLAB, SlabType.TOP)
                .match('P', Blocks.DARK_OAK_PLANKS);
    }

    @Override
    public Entity createEntity(World world) {
        return ModEntities.MANGONEL_ENTITY.get().create(world);
    }

    @Override
    public List<Block> getBlockFinders() {
        return List.of(Blocks.DARK_OAK_LOG, Blocks.IRON_BLOCK, Blocks.HAY_BLOCK, Blocks.DARK_OAK_TRAPDOOR, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
    }
}
