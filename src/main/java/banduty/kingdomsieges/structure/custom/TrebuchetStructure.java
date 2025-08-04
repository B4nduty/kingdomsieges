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
    public Entity createEntity(World world) {
        return ModEntities.TREBUCHET_ENTITY.get().create(world);
    }

    @Override
    public List<Block> getBlockFinders() {
        return List.of(Blocks.DARK_OAK_FENCE, Blocks.IRON_BLOCK, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_STAIRS);
    }
}
