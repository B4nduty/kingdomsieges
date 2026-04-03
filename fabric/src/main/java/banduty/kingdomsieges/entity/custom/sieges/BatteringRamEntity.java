package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.siegeentity.SiegeProperties;
import banduty.stoneycore.util.BlockDamageTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BatteringRamEntity extends AbstractSiegeEntity implements GeoEntity {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("battering_ram")
            .health(150.0)
            .speed(0.025)
            .knockbackResist(265.0)
            .moveSound(ModSounds.SIEGE_ENGINE_MOVE)
            .reloadSound(ModSounds.ROPE_CHARGE_BR)
            .attackSound(ModSounds.RAM_IMPACT)
            .moveSoundDelay(150)
            .moveSoundRange(30.0)
            .reloadSoundRange(15.0)
            .attackSoundRange(40.0)
            .build();

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation attackAnim = RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE);

    public BatteringRamEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.MOVEMENT_SPEED, 0.025)
                .add(Attributes.KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    public InteractionResult handleSiegeInteraction(Player player, InteractionHand hand, ServerLevel serverLevel) {
        if (getFirstPassenger() != null) return InteractionResult.FAIL;

        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.isEmpty() && canAddPassenger(player) && !player.isShiftKeyDown()) {
            player.startRiding(this);
            setOwner(player);
            return InteractionResult.SUCCESS;
        }

        if (getCooldown() <= 0 && player.isShiftKeyDown()) {
            triggerAnimation("attack");
            setAttackHappened(false);
            setCooldown(50);
            setOwner(this);
            playReloadSound(serverLevel);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onSiegeTick(ServerLevel serverLevel) {
        BlockDamageTracker.clean(serverLevel);

        if (getCooldown() <= 6 && !hasAttackHappened()) {
            performAttack(serverLevel);
            setAttackHappened(true);
            playAttackSound(serverLevel);
        }
    }

    private void performAttack(ServerLevel serverLevel) {
        Vec3 lookVec = this.getViewVector(1.0F).normalize();
        Vec3 front = this.position().add(lookVec.scale(2.5));

        int baseX = (int) Math.floor(front.x);
        int baseY = (int) Math.floor(this.getY() + 1);
        int baseZ = (int) Math.floor(front.z);

        double radius = 1.5;
        Vec3 boxCenter = new Vec3(baseX + 0.5, baseY, baseZ + 0.5);
        Vec3 knockbackDir = this.getViewVector(1.0F).normalize().scale(2.5);
        float baseDamage = (float) getBaseDamage();

        // Damage entities
        serverLevel.getEntities(this, new AABB(
                        boxCenter.x - radius, boxCenter.y - 1, boxCenter.z - radius,
                        boxCenter.x + radius, boxCenter.y + 2, boxCenter.z + radius
                ), entity -> entity instanceof LivingEntity && !(entity instanceof Player player && player.isCreative())
        ).forEach(entity -> {
            entity.hurt(serverLevel.damageSources().mobAttack((LivingEntity) getOwner()), baseDamage);
            entity.addDeltaMovement(new Vec3(knockbackDir.x, 0.25, knockbackDir.z));
            entity.hurtMarked = true;
        });

        // Damage blocks
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = new BlockPos(baseX + x, baseY + y, baseZ + z);
                    BlockState state = serverLevel.getBlockState(pos);

                    if (state.isAir() || state.getDestroySpeed(serverLevel, pos) < 0) continue;

                    float hardness = state.getDestroySpeed(serverLevel, pos);
                    float damageFactor = 1.0f / (40 / baseDamage);
                    BlockDamageTracker.damageBlock(serverLevel, pos, damageFactor, hardness);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "anim_controller", state -> PlayState.STOP)
                .triggerableAnim("attack", attackAnim));
    }

    @Override
    public void triggerAnimation(String name) {
        if ("attack".equals(name)) triggerAnim("anim_controller", "attack");
    }

    @Override
    public void stopAnimation(String name) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -1.25) : new Vec3(0.0, 0.0, 1.25);
    }

    @Override
    public Vec3 getPlayerPOV() {
        return new Vec3(0.0, -0.7f, 0.0);
    }
}