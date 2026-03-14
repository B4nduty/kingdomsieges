package banduty.kingdomsieges.event;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.event.custom.HumanoidModelSetupAnimEvents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.phys.Vec3;

@Mod.EventBusSubscriber(modid = Kingdomsieges.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HumanoidModelAnglesHandler {

    @SubscribeEvent
    public static void onAfterSetAngles(HumanoidModelSetupAnimEvents.After event) {
        LivingEntity entity = event.getEntity();
        HumanoidModel<?> model = event.getModel();
        float age = event.getAge();
        float headYaw = event.getHeadYaw();
        float headPitch = event.getHeadPitch();

        if (!(entity.getVehicle() instanceof AbstractSiegeEntity abstractSiegeEntity)) return;

        Vec3 velocity = abstractSiegeEntity.getDeltaMovement();
        float movement = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        float stepSpeed = 0.1F;
        float stepWidth = 10.0F;
        float swing = Mth.cos(age * stepSpeed);

        if (abstractSiegeEntity instanceof CannonEntity || abstractSiegeEntity instanceof RibauldequinEntity) {
            applyCannonAnimations(model, abstractSiegeEntity, stepWidth, swing, movement, headYaw, headPitch);
            if (event.isCancelable()) event.setCanceled(true);
        }

        if (abstractSiegeEntity instanceof BatteringRamEntity || abstractSiegeEntity instanceof MangonelEntity || abstractSiegeEntity instanceof MantletEntity) {
            applySiegeEngineAnimations(model, swing, movement, headYaw, headPitch);
            if (event.isCancelable()) event.setCanceled(true);
        }
    }

    private static void applyCannonAnimations(HumanoidModel<?> model, AbstractSiegeEntity siege, float stepWidth, float swing, float movement, float headYaw, float headPitch) {
        model.rightArm.yRot = (float) Math.toRadians(45.0);
        model.rightArm.xRot = (float) Math.toRadians(-(siege.getTrackedPitch() + 60));
        model.rightArm.z = 4.0F;
        model.rightArm.x = -3.5F;

        model.leftArm.yRot = (float) Math.toRadians(45.0);
        model.leftArm.xRot = (float) Math.toRadians(-90.0);
        model.leftArm.z = -4.0F;
        model.leftArm.x = 3.5F;

        model.rightLeg.xRot = swing * stepWidth * movement;
        model.rightLeg.yRot = (float) Math.toRadians(45.0);

        model.leftLeg.xRot = -swing * stepWidth * movement;
        model.leftLeg.yRot = (float) Math.toRadians(45.0);

        model.body.yRot = (float) Math.toRadians(45.0);
        setupHead(model, headYaw, headPitch);
    }

    private static void applySiegeEngineAnimations(HumanoidModel<?> model, float swing, float movement, float headYaw, float headPitch) {
        model.rightArm.yRot = (float) Math.toRadians(-10.0);
        model.rightArm.zRot = (float) Math.toRadians(10.0);
        model.rightArm.xRot = (float) Math.toRadians(-45.0);
        model.rightArm.x = -4.0F;
        model.rightArm.y = 10;
        model.rightArm.z = -2.0F;

        model.leftArm.yRot = (float) Math.toRadians(10.0);
        model.leftArm.xRot = (float) Math.toRadians(-45.0);
        model.leftArm.zRot = (float) Math.toRadians(-10.0);
        model.leftArm.x = 4.0F;
        model.leftArm.y = 12;
        model.leftArm.z = -2.0F;

        model.rightLeg.xRot = swing * 10.0F * movement;
        model.rightLeg.z = 10;

        model.leftLeg.xRot = -swing * 10.0F * movement;
        model.leftLeg.z = 10;

        model.body.xRot = (float) Math.toRadians(50.0);
        model.body.y = 6;
        model.body.z = 0;

        setupHead(model, headYaw, headPitch);
        model.head.y = 7;
        model.hat.y = 7;
    }

    private static void setupHead(HumanoidModel<?> model, float headYaw, float headPitch) {
        float xRot = headPitch * ((float)Math.PI / 180F);
        float yRot = headYaw * ((float)Math.PI / 180F);
        model.head.xRot = xRot;
        model.head.yRot = yRot;
        model.hat.xRot = xRot;
        model.hat.yRot = yRot;
    }
}