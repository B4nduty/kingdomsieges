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
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.List;

public class TrebuchetStructure extends StructureSpawner {
    @Override
    public String[][] getBaseAisles() {
        return new String[][] {
                {
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "    R  "
                },
                {
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "    R  ",
                        "    B  "
                },
                {
                        "       ",
                        "       ",
                        "       ",
                        "    P  ",
                        "   S T ",
                        "  S B T"
                },
                {
                        "    F  ",
                        "    F  ",
                        "    F  ",
                        "    F  ",
                        "    I  ",
                        "BBBBBB "
                },
                {
                        "       ",
                        "       ",
                        "       ",
                        "    P  ",
                        "   S T ",
                        "  S B T"
                },
                {
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "    A  ",
                        "    B  "
                },
                {
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "    A  "
                }
        };
    }

    @Override
    public void applyKeyMatcher(BlockPatternBuilder builder, Direction dir) {
        new StructureMatcher(builder, dir)
                .match('F', Blocks.DARK_OAK_FENCE)
                .match('I', Blocks.IRON_BLOCK)
                .matchSlab('B', Blocks.DARK_OAK_SLAB, SlabType.BOTTOM)
                .match('P', Blocks.DARK_OAK_PLANKS)
                .matchStairsPerpendicular('S', Blocks.DARK_OAK_STAIRS, true)
                .matchStairsPerpendicular('T', Blocks.DARK_OAK_STAIRS, false)
                .matchStairsRelative('A', Blocks.DARK_OAK_STAIRS, true)
                .matchStairsRelative('R', Blocks.DARK_OAK_STAIRS, false);
    }

    @Override
    public Entity createEntity(Level level) {
        return ModEntities.TREBUCHET_ENTITY.get().create(level);
    }

    @Override
    public List<Block> getBlockFinders() {
        return List.of(Blocks.DARK_OAK_FENCE, Blocks.IRON_BLOCK, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_STAIRS);
    }
}
