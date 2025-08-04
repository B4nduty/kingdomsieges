package banduty.kingdomsieges.util.servertick;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.config.KSConfigs;
import banduty.kingdomsieges.config.KingdomSiegesConfig;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

import java.util.*;

public class BellRinger {

    private static long lastHour = -1;
    private static long lastRealHour = -1;

    private static final Map<ServerWorld, List<BellChimeTask>> chimeTasks = new HashMap<>();
    private static final Map<Land, Integer> siegeRingTimers = new HashMap<>();

    public static void tick(ServerWorld world, LandState landState) {
        tickChimeTasks(world);

        Set<Land> allLands = new HashSet<>(landState.getAllLands());
        Set<Land> landsUnderSiege = new HashSet<>();

        for (Land land : allLands) {
            if (SiegeManager.isLandDefenseSiege(world, land)) {
                siegeRingTimers.putIfAbsent(land, 800); // 40s = 800 ticks
            }
        }

        Iterator<Map.Entry<Land, Integer>> iter = siegeRingTimers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Land, Integer> entry = iter.next();
            Land land = entry.getKey();
            int ticksLeft = entry.getValue();

            if (!allLands.contains(land) || ticksLeft <= 0) {
                iter.remove();
                continue;
            }

            landsUnderSiege.add(land);
            siegeRingTimers.put(land, ticksLeft - 1);

            if (ticksLeft % 30 == 0) {
                for (BlockPos bellPos : findBellsInClaim(world, land)) {
                    scheduleBellRing(world, bellPos, 0, 1.2F);
                }
            }
        }

        long timeOfDay = world.getTimeOfDay() % 24000;
        long currentIngameHour = (timeOfDay / 1000 + 6) % 24;

        Calendar calendar = Calendar.getInstance();
        int realHour = calendar.get(Calendar.HOUR_OF_DAY);

        KingdomSiegesConfig config = Kingdomsieges.getConfig();
        KSConfigs.Choices choice = config.bellRingTime();

        if (!shouldRingBell(choice, currentIngameHour, realHour)) return;

        boolean isIngame = (choice == KSConfigs.Choices.EVERY_INGAME_HOUR || choice == KSConfigs.Choices.TWELVE_INGAME_HOURS);
        long currentHour = isIngame ? currentIngameHour : realHour;
        long last = isIngame ? lastHour : lastRealHour;

        if (currentHour != last) {
            if (isIngame) lastHour = currentHour;
            else lastRealHour = currentHour;

            chimeHour(world, allLands, landsUnderSiege, currentHour);
        }
    }

    private static boolean shouldRingBell(KSConfigs.Choices choice, long ingameHour, int realHour) {
        return switch (choice) {
            case EVERY_INGAME_HOUR, EVERY_HOUR -> true;
            case TWELVE_INGAME_HOURS -> ingameHour % 12 == 0;
            case TWELVE_HOURS -> realHour % 12 == 0;
            default -> false;
        };
    }

    private static void chimeHour(ServerWorld world, Set<Land> allLands, Set<Land> landsUnderSiege, long hour) {
        int rings = (int) (hour % 12);
        rings = (rings == 0) ? 12 : rings;

        for (Land land : allLands) {
            if (landsUnderSiege.contains(land)) continue;

            for (BlockPos bellPos : findBellsInClaim(world, land)) {
                int totalDelay = 0;
                float pitch = 0.8F;

                scheduleBellRing(world, bellPos, totalDelay, pitch);
                totalDelay += 50;

                for (int i = 0; i < (rings == 12 ? 24 : rings); i++) {
                    boolean isEven = i % 2 == 0;
                    if (rings == 12) pitch = isEven ? 1.1F : 0.9F;

                    scheduleBellRing(world, bellPos, totalDelay, pitch);
                    totalDelay += (rings == 12) ? (isEven ? 10 : 50) : 50;
                }
            }
        }
    }

    private static List<BlockPos> findBellsInClaim(ServerWorld world, Land land) {
        List<BlockPos> bells = new ArrayList<>();
        int bottomY = world.getBottomY();
        int topY = world.getTopY();

        for (BlockPos basePos : land.getClaimed()) {
            for (int y = bottomY; y <= topY; y++) {
                BlockPos pos = new BlockPos(basePos.getX(), y, basePos.getZ());
                if (world.getBlockState(pos).getBlock() instanceof BellBlock) {
                    bells.add(pos);
                }
            }
        }
        return bells;
    }

    private static void scheduleBellRing(ServerWorld world, BlockPos pos, int delay, float pitch) {
        chimeTasks.computeIfAbsent(world, k -> new ArrayList<>()).add(new BellChimeTask(pos, delay, pitch));
    }

    private static void tickChimeTasks(ServerWorld world) {
        List<BellChimeTask> tasks = chimeTasks.get(world);
        if (tasks == null || tasks.isEmpty()) return;

        Iterator<BellChimeTask> iter = tasks.iterator();
        while (iter.hasNext()) {
            BellChimeTask task = iter.next();
            if (--task.delay <= 0) {
                BlockState state = world.getBlockState(task.pos);
                if (state.getBlock() instanceof BellBlock) {
                    ringBell(world, task.pos, state, task.pitch);
                }
                iter.remove();
            }
        }
    }

    private static void ringBell(ServerWorld world, BlockPos pos, BlockState state, float pitch) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BellBlockEntity bell) {
            Direction direction = state.get(BellBlock.FACING);
            bell.activate(direction);
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, pitch);
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    private static class BellChimeTask {
        final BlockPos pos;
        int delay;
        final float pitch;

        BellChimeTask(BlockPos pos, int delay, float pitch) {
            this.pos = pos;
            this.delay = delay;
            this.pitch = pitch;
        }
    }
}