package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.KSSounds;
import banduty.stoneycore.entity.siege.AbstractSiegeEntity;
import banduty.stoneycore.entity.siege.SiegeAnimation;
import banduty.stoneycore.entity.siege.SiegeProperties;
import banduty.stoneycore.util.BlockDamageTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;

public class BatteringRamEntity extends AbstractSiegeEntity implements KSGeoSiege {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("battering_ram")
            .health(150.0)
            .speed(0.025)
            .knockbackResist(265.0)
            .moveSound(KSSounds.SIEGE_ENGINE_MOVE.get())
            .reloadSound(KSSounds.ROPE_CHARGE_BR.get())
            .attackSound(KSSounds.RAM_IMPACT.get())
            .reloadSoundRange(15.0)
            .attackSoundRange(40.0)
            .cooldown(50)
            .fireDelay(44)
            .build();

    private static final Map<SiegeAnimation, RawAnimation> ANIMATIONS = Map.of(
            SiegeAnimation.ATTACK, RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private boolean attackPending;

    public BatteringRamEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PROPERTIES.createAttributes();
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    protected InteractionResult operate(Player player, ItemStack stack, ServerLevel level) {
        if (getCooldown() <= 0 && player.isShiftKeyDown()) {
            setOwner(player);
            attackPending = true;
            startCooldown();
            playAnimation(SiegeAnimation.ATTACK);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean isReloading() {
        return attackPending && getCooldown() > PROPERTIES.fireTriggerCooldown();
    }

    @Override
    protected void onSiegeTick(ServerLevel level) {
        if (attackPending && getCooldown() <= PROPERTIES.fireTriggerCooldown()) {
            attackPending = false;
            performAttack(level);
            playAttackSound(level);
        }
    }

    private void performAttack(ServerLevel level) {
        Vec3 look = getViewVector(1.0F).normalize();
        Vec3 front = position().add(look.scale(2.5));

        int baseX = (int) Math.floor(front.x);
        int baseY = (int) Math.floor(getY() + 1);
        int baseZ = (int) Math.floor(front.z);

        double radius = 1.5;
        Vec3 knockback = look.scale(2.5);
        float damage = (float) getBaseDamage();

        AABB area = new AABB(baseX + 0.5 - radius, baseY - 1, baseZ + 0.5 - radius,
                baseX + 0.5 + radius, baseY + 2, baseZ + 0.5 + radius);

        level.getEntities(this, area,
                e -> e instanceof LivingEntity && !(e instanceof Player p && p.isCreative())
        ).forEach(entity -> {
            entity.hurt(level.damageSources().mobAttack(this), damage);
            entity.addDeltaMovement(new Vec3(knockback.x, 0.25, knockback.z));
            entity.hurtMarked = true;
        });

        BlockDamageTracker.clean(level);
        float blockDamage = damage / 40f;
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = new BlockPos(baseX + x, baseY + y, baseZ + z);
                    BlockState state = level.getBlockState(pos);
                    float hardness = state.getDestroySpeed(level, pos);
                    if (state.isAir() || hardness < 0) continue;
                    BlockDamageTracker.damageBlock(level, pos, blockDamage, hardness);
                }
            }
        }
    }

    @Override
    public Map<SiegeAnimation, RawAnimation> triggeredAnimations() { return ANIMATIONS; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animatableInstanceCache; }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -1.25) : new Vec3(0.0, 0.0, 1.25);
    }

    @Override
    public Vec3 getPlayerPOV() { return new Vec3(0.0, -0.7f, 0.0); }
}
