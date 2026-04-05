package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.sounds.ModSounds;
import banduty.stoneycore.combat.melee.SCDamageType;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.entity.custom.siegeentity.LoadingStage;
import banduty.stoneycore.entity.custom.siegeentity.SiegeProperties;
import banduty.stoneycore.networking.ModMessages;
import banduty.stoneycore.networking.packet.SiegeYawS2CPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class TrebuchetEntity extends AbstractSiegeEntity implements GeoEntity {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("trebuchet")
            .health(200.0)
            .speed(0.0)
            .knockbackResist(265.0)
            .reloadSound(ModSounds.ROPE_CHARGE_GN.get())
            .shootSound(ModSounds.TREBUCHET_SHOOT.get())
            .reloadSoundRange(15.0)
            .shootSoundRange(75.0)
            .build();

    private static final LoadingStage[] AMMO_LOADS = {
            LoadingStage.of(Items.STONE),
            LoadingStage.of(Items.MAGMA_BLOCK),
            LoadingStage.of(Items.ROTTEN_FLESH, 9)
    };

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation shootAnim = RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation reloadAnim = RawAnimation.begin().then("reload", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation loadedAnim = RawAnimation.begin().then("loaded", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation unloadedAnim = RawAnimation.begin().then("unloaded", Animation.LoopType.PLAY_ONCE);

    public TrebuchetEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 265);
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    @Override
    public boolean canAddPassenger(Entity entity) { return false; }

    @Override
    public InteractionResult handleSiegeInteraction(Player player, InteractionHand hand, ServerLevel serverLevel) {
        if (getCooldown() > 0) return InteractionResult.FAIL;

        if (player.isShiftKeyDown()) {
            setTrackedYaw(getTrackedYaw() + 45.0f);
            setYRot(getTrackedYaw());
            setYBodyRot(getTrackedYaw());
            setYHeadRot(getTrackedYaw());

            List<ServerPlayer> players = serverLevel.players();
            for (ServerPlayer playerEntity : players) {
                ModMessages.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> playerEntity),
                        new SiegeYawS2CPacket(this.getYRot(), this.getXRot(), this.getWheelRotation())
                );
            }
            return InteractionResult.SUCCESS;
        }

        ItemStack itemStack = player.getItemInHand(hand);

        if (!hasAmmoLoaded()) {
            LoadingStage match = findMatchingAmmo(itemStack);

            if (match == null) {
                showAmmoListMessage(player);
                return InteractionResult.FAIL;
            }

            if (!player.isCreative()) {
                if (itemStack.getCount() < match.amount()) {
                    player.displayClientMessage(
                            Component.translatable("siege.loading.need_amount", match.amount(),
                                    match.item().getDefaultInstance().getHoverName()), true);
                    return InteractionResult.FAIL;
                }
                itemStack.shrink(match.amount());
            }

            setAmmoLoaded(match.item().toString());
            setReloadTime(getBaseReload());
            triggerAnimation("reload");
            playReloadSound(serverLevel);
            return InteractionResult.SUCCESS;
        }

        if (isReloadComplete()) {
            triggerAnimation("shoot");
            setCooldown(60);
            playShootSound(serverLevel);
            setOwner(player);
        }

        return InteractionResult.SUCCESS;
    }

    private LoadingStage findMatchingAmmo(ItemStack stack) {
        for (LoadingStage stage : AMMO_LOADS) {
            if (stack.is(stage.item())) return stage;
        }
        return null;
    }

    private void showAmmoListMessage(Player player) {
        Component ammoList = AMMO_LOADS[0].item().getDefaultInstance().getHoverName()
                .copy().append(", ").append(AMMO_LOADS[1].item().getDefaultInstance().getHoverName())
                .append(", ").append(AMMO_LOADS[2].item().getDefaultInstance().getHoverName());
        player.displayClientMessage(Component.translatable("siege.loading.need_one_of", ammoList), true);
    }

    @Override
    public void onSiegeTick(ServerLevel serverLevel) {
        if (hasAmmoLoaded()) {
            if (isReloadComplete() && getCooldown() <= 0) {
                triggerAnimation("loaded");
            }
            if (getCooldown() == 52) {
                fireTrebuchet(serverLevel);
            }
        } else if (getCooldown() <= 0 && isReloadComplete()) {
            triggerAnimation("unloaded");
        }
    }

    private void fireTrebuchet(ServerLevel serverLevel) {
        TrebuchetProjectile projectile = new TrebuchetProjectile(ModEntities.TREBUCHET_PROJECTILE.get(), this, serverLevel);
        projectile.setPos(this.getX(), this.getY() + 12.0, this.getZ());

        double blocksPerTick = getProjectileSpeed() / 20.0;
        float accuracyDegrees = getAccuracyMultiplier();
        float yawOffset = (random.nextFloat() - 0.5f) * 4 * accuracyDegrees;
        float adjustedYaw = getTrackedYaw() + yawOffset;
        float yawRad = (float) Math.toRadians(adjustedYaw);

        Vec3 direction = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        projectile.setDeltaMovement(direction.scale(blocksPerTick));

        projectile.setBaseDamage(getBaseDamage());
        projectile.setDamageType(SCDamageType.BLUDGEONING);
        projectile.setOwner(this);

        Item loadedItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(getAmmoLoaded()));

        if (loadedItem == Items.STONE) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.BREAK_BLOCKS);
            projectile.setTextureName("stone");
        } else if (loadedItem == Items.MAGMA_BLOCK) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.SPREAD_FIRE);
            projectile.setTextureName("magma");
        } else if (loadedItem == Items.ROTTEN_FLESH) {
            projectile.setImpactMode(TrebuchetProjectile.ImpactMode.SPREAD_EFFECT);
            projectile.setStatusEffectInstance(new MobEffectInstance(MobEffects.POISON, 100, 0, true, true, true));
            projectile.setCloudDuration(600);
            projectile.setTextureName("rotten_flesh");
        }

        serverLevel.addFreshEntity(projectile);
        setAmmoLoaded("");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "anim_controller", state -> PlayState.STOP)
                .triggerableAnim("shoot", shootAnim)
                .triggerableAnim("reload", reloadAnim)
                .setAnimationSpeed(getReloadTime() > 0 ? 200.0 / getReloadTime() : 1.0)
                .triggerableAnim("loaded", loadedAnim)
                .triggerableAnim("unloaded", unloadedAnim));
    }

    @Override
    public void triggerAnimation(String name) {
        switch (name) {
            case "shoot" -> triggerAnim("anim_controller", "shoot");
            case "reload" -> triggerAnim("anim_controller", "reload");
            case "loaded" -> triggerAnim("anim_controller", "loaded");
            case "unloaded" -> triggerAnim("anim_controller", "unloaded");
        }
    }

    @Override
    public void stopAnimation(String name) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return Vec3.ZERO;
    }

    @Override
    public double getVelocity(Entity entity) {
        return 0.0;
    }

    @Override
    public Vec3 getPlayerPOV() {
        return Vec3.ZERO;
    }
}