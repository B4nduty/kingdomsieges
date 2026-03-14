package banduty.kingdomsieges.util.servertick;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.config.IKSConfig;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.*;

public class BellRinger {

    private static long lastHour = -1;
    private static long lastRealHour = -1;

    private static final Map<ServerLevel, List<BellChimeTask>> chimeTasks = new HashMap<>();
    private static final Map<Land, Integer> siegeRingTimers = new HashMap<>();

    public static void tick(ServerLevel serverLevel, LandState landState) {
        tickChimeTasks(serverLevel);

        Set<Land> allLands = new HashSet<>(landState.getAllLands());
        Set<Land> landsUnderSiege = new HashSet<>();

        for (Land land : allLands) {
            if (SiegeManager.isLandDefenseSiege(serverLevel, land)) {
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
                for (BlockPos bellPos : findBellsInClaim(serverLevel, land)) {
                    scheduleBellRing(serverLevel, bellPos, 0, 1.2F);
                }
            }
        }

        long timeOfDay = serverLevel.getDayTime() % 24000;
        long currentIngameHour = (timeOfDay / 1000 + 6) % 24;

        Calendar calendar = Calendar.getInstance();
        int realHour = calendar.get(Calendar.HOUR_OF_DAY);

        IKSConfig config = Kingdomsieges.getConfig();
        IKSConfig.Choices choice = config.getBellRingTime();

        if (!shouldRingBell(choice, currentIngameHour, realHour)) return;

        boolean isIngame = (choice == IKSConfig.Choices.EVERY_INGAME_HOUR || choice == IKSConfig.Choices.TWELVE_INGAME_HOURS);
        long currentHour = isIngame ? currentIngameHour : realHour;
        long last = isIngame ? lastHour : lastRealHour;

        if (currentHour != last) {
            if (isIngame) lastHour = currentHour;
            else lastRealHour = currentHour;

            chimeHour(serverLevel, allLands, landsUnderSiege, currentHour);
        }
    }

    private static boolean shouldRingBell(IKSConfig.Choices choice, long ingameHour, int realHour) {
        return switch (choice) {
            case EVERY_INGAME_HOUR, EVERY_HOUR -> true;
            case TWELVE_INGAME_HOURS -> ingameHour % 12 == 0;
            case TWELVE_HOURS -> realHour % 12 == 0;
            default -> false;
        };
    }

    private static void chimeHour(ServerLevel serverLevel, Set<Land> allLands, Set<Land> landsUnderSiege, long hour) {
        int rings = (int) (hour % 12);
        rings = (rings == 0) ? 12 : rings;

        for (Land land : allLands) {
            if (landsUnderSiege.contains(land)) continue;

            for (BlockPos bellPos : findBellsInClaim(serverLevel, land)) {
                int totalDelay = 0;
                float pitch = 0.8F;

                scheduleBellRing(serverLevel, bellPos, totalDelay, pitch);
                totalDelay += 50;

                for (int i = 0; i < (rings == 12 ? 24 : rings); i++) {
                    boolean isEven = i % 2 == 0;
                    if (rings == 12) pitch = isEven ? 1.1F : 0.9F;

                    scheduleBellRing(serverLevel, bellPos, totalDelay, pitch);
                    totalDelay += (rings == 12) ? (isEven ? 10 : 50) : 50;
                }
            }
        }
    }

    private static List<BlockPos> findBellsInClaim(ServerLevel serverLevel, Land land) {
        List<BlockPos> bells = new ArrayList<>();
        int bottomY = serverLevel.getMinBuildHeight();
        int topY = serverLevel.getMaxBuildHeight();

        for (BlockPos basePos : land.getClaimed()) {
            for (int y = bottomY; y <= topY; y++) {
                BlockPos pos = new BlockPos(basePos.getX(), y, basePos.getZ());
                if (serverLevel.getBlockState(pos).getBlock() instanceof BellBlock) {
                    bells.add(pos);
                }
            }
        }
        return bells;
    }

    private static void scheduleBellRing(ServerLevel serverLevel, BlockPos pos, int delay, float pitch) {
        chimeTasks.computeIfAbsent(serverLevel, k -> new ArrayList<>()).add(new BellChimeTask(pos, delay, pitch));
    }

    private static void tickChimeTasks(ServerLevel serverLevel) {
        List<BellChimeTask> tasks = chimeTasks.get(serverLevel);
        if (tasks == null || tasks.isEmpty()) return;

        Iterator<BellChimeTask> iter = tasks.iterator();
        while (iter.hasNext()) {
            BellChimeTask task = iter.next();
            if (--task.delay <= 0) {
                BlockState state = serverLevel.getBlockState(task.pos);
                if (state.getBlock() instanceof BellBlock) {
                    ringBell(serverLevel, task.pos, state, task.pitch);
                }
                iter.remove();
            }
        }
    }

    private static void ringBell(ServerLevel serverLevel, BlockPos pos, BlockState state, float pitch) {
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (be instanceof BellBlockEntity bell) {
            Direction direction = state.getValue(BellBlock.FACING);
            bell.onHit(direction);
            serverLevel.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, pitch);
            serverLevel.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
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