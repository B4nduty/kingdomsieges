package banduty.kingdomsieges.util.servertick;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.config.IKSConfig;
import banduty.stoneycore.lands.util.Land;
import banduty.stoneycore.lands.util.LandState;
import banduty.stoneycore.siege.SiegeManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
    private static final Object2IntOpenHashMap<Land> siegeRingTimers = new Object2IntOpenHashMap<>();
    private static final Map<Land, List<BlockPos>> bellCache = new HashMap<>();
    private static int cacheCleanupTicker = 0;

    public static void tick(ServerLevel serverLevel, LandState landState) {
        tickChimeTasks(serverLevel);

        Collection<Land> allLands = landState.getAllLands();

        // Prevent memory leaks
        if (++cacheCleanupTicker % 6000 == 0) {
            bellCache.keySet().removeIf(land -> !allLands.contains(land));
        }

        for (Land land : allLands) {
            boolean underSiege = SiegeManager.isLandDefenseSiege(serverLevel, land);

            if (underSiege) {
                int ticksLeft = siegeRingTimers.getOrDefault(land, 800);
                if (ticksLeft > 0) {
                    if (ticksLeft % 30 == 0) {
                        ringBellsInLand(serverLevel, land, 0, 1.2F);
                    }
                    siegeRingTimers.put(land, ticksLeft - 1);
                }
            } else {
                siegeRingTimers.removeInt(land);
            }
        }

        handleHourlyChimes(serverLevel, allLands);
    }

    private static void handleHourlyChimes(ServerLevel serverLevel, Collection<Land> allLands) {
        long timeOfDay = serverLevel.getDayTime() % 24000;
        long currentIngameHour = (timeOfDay / 1000 + 6) % 24;
        int realHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        IKSConfig.Choices choice = Kingdomsieges.getConfig().getBellRingTime();
        if (!shouldRingBell(choice, currentIngameHour, realHour)) return;

        boolean isIngame = (choice == IKSConfig.Choices.EVERY_INGAME_HOUR || choice == IKSConfig.Choices.TWELVE_INGAME_HOURS);
        long currentHour = isIngame ? currentIngameHour : realHour;
        long last = isIngame ? lastHour : lastRealHour;

        if (currentHour != last) {
            if (isIngame) lastHour = currentHour;
            else lastRealHour = currentHour;

            int rings = (int) (currentHour % 12);
            rings = (rings == 0) ? 12 : rings;

            for (Land land : allLands) {
                // Don't double-ring if already ringing for siege
                if (siegeRingTimers.containsKey(land)) continue;
                chimeHourForLand(serverLevel, land, rings);
            }
        }
    }

    private static void chimeHourForLand(ServerLevel serverLevel, Land land, int rings) {
        List<BlockPos> bells = getBells(serverLevel, land);
        if (bells.isEmpty()) return;

        for (BlockPos pos : bells) {
            int totalDelay = 0;
            for (int i = 0; i <= (rings == 12 ? 24 : rings); i++) {
                float pitch = 0.8F;
                int gap = 50;

                if (rings == 12 && i > 0) {
                    boolean isEven = i % 2 == 0;
                    pitch = isEven ? 1.1F : 0.9F;
                    gap = isEven ? 10 : 50;
                }

                scheduleBellRing(serverLevel, pos, totalDelay, pitch);
                totalDelay += gap;
            }
        }
    }

    private static void ringBellsInLand(ServerLevel serverLevel, Land land, int delay, float pitch) {
        for (BlockPos pos : getBells(serverLevel, land)) {
            scheduleBellRing(serverLevel, pos, delay, pitch);
        }
    }

    private static List<BlockPos> getBells(ServerLevel serverLevel, Land land) {
        List<BlockPos> cachedBells = bellCache.get(land);

        if (cachedBells == null) {
            List<BlockPos> found = findBellsInClaim(serverLevel, land);
            bellCache.put(land, found);
            return found;
        }

        // Remove any bell that has been broke
        cachedBells.removeIf(pos -> !(serverLevel.getBlockState(pos).getBlock() instanceof BellBlock));

        return cachedBells;
    }

    private static List<BlockPos> findBellsInClaim(ServerLevel serverLevel, Land land) {
        List<BlockPos> bells = new ArrayList<>();
        int minY = serverLevel.getMinBuildHeight();
        int maxY = serverLevel.getMaxBuildHeight();

        for (BlockPos basePos : land.getClaimed()) {
            for (int y = minY; y <= maxY; y++) {
                BlockPos pos = new BlockPos(basePos.getX(), y, basePos.getZ());
                if (serverLevel.getBlockState(pos).getBlock() instanceof BellBlock) {
                    bells.add(pos.immutable());
                }
            }
        }
        return bells;
    }

    private static boolean shouldRingBell(IKSConfig.Choices choice, long ingameHour, int realHour) {
        return switch (choice) {
            case EVERY_INGAME_HOUR, EVERY_HOUR -> true;
            case TWELVE_INGAME_HOURS -> ingameHour % 12 == 0;
            case TWELVE_HOURS -> realHour % 12 == 0;
            default -> false;
        };
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