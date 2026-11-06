package banduty.kingdomsieges.entity.custom.sieges;

import banduty.kingdomsieges.sounds.KSSounds;
import banduty.stoneycore.entity.siege.AbstractSiegeEntity;
import banduty.stoneycore.entity.siege.SiegeProperties;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MantletEntity extends AbstractSiegeEntity implements KSGeoSiege {

    private static final SiegeProperties PROPERTIES = SiegeProperties.builder("mantlet")
            .health(300.0)
            .speed(0.01)
            .knockbackResist(265.0)
            .moveSound(KSSounds.SIEGE_ENGINE_MOVE.get())
            .build();

    private static final RawAnimation SET_UP = RawAnimation.begin().then("set_up", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation PICK_UP = RawAnimation.begin().then("pick_up", Animation.LoopType.HOLD_ON_LAST_FRAME);

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public MantletEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PROPERTIES.createAttributes();
    }

    @Override
    public SiegeProperties getProperties() { return PROPERTIES; }

    /** A mantlet can be picked up with anything in hand. */
    @Override
    protected boolean shouldMount(Player player, ItemStack heldItem) {
        return !player.isShiftKeyDown() && canAddPassenger(player);
    }

    @Override
    protected InteractionResult operate(Player player, ItemStack stack, ServerLevel level) {
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public RawAnimation stateAnimation() {
        return isPicked() ? PICK_UP : SET_UP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return animatableInstanceCache; }

    @Override
    public Vec3 getPassengerOffset(Entity entity) {
        return entity instanceof Horse ? new Vec3(0.0, 0.0, -2.0) : new Vec3(0.0, 0.0, 2.5);
    }

    @Override
    public Vec3 getPlayerPOV() { return new Vec3(0.0, -0.7f, 0.0); }
}
