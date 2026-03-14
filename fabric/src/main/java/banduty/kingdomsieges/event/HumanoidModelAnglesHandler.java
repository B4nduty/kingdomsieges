package banduty.kingdomsieges.event;

import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.event.custom.HumanoidModelSetupAnimEvents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class HumanoidModelAnglesHandler implements HumanoidModelSetupAnimEvents.After {
    @Override
    public void afterSetAngles(HumanoidModel<?> model, LivingEntity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(entity.getVehicle() instanceof AbstractSiegeEntity abstractSiegeEntity)) return;
        if (abstractSiegeEntity instanceof CannonEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getDeltaMovement().x * abstractSiegeEntity.getDeltaMovement().x + abstractSiegeEntity.getDeltaMovement().z * abstractSiegeEntity.getDeltaMovement().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = Mth.cos(age * stepSpeed);

            model.rightArm.xRot = 0.0F;
            model.rightArm.yRot = (float) Math.toRadians(45.0);
            model.rightArm.xRot = (float) Math.toRadians(-(abstractSiegeEntity.getTrackedPitch() + 60));
            model.rightArm.z = 4.0F;
            model.rightArm.x = -3.5F;

            model.leftArm.yRot = (float) Math.toRadians(45.0);
            model.leftArm.xRot = (float) Math.toRadians(-90.0);
            model.leftArm.z = -4.0F;
            model.leftArm.x = 3.5F;

            model.rightLeg.xRot = 0.0F;
            model.rightLeg.xRot = swing * stepWidth * movement;
            model.rightLeg.yRot = (float) Math.toRadians(45.0);

            model.leftLeg.xRot = 0.0F;
            model.leftLeg.xRot = -swing * stepWidth * movement;
            model.leftLeg.yRot = (float) Math.toRadians(45.0);

            model.body.xRot = 0.0F;
            model.body.yRot = (float) Math.toRadians(45.0);

            model.head.xRot = headPitch * ((float)Math.PI / 180F);
            model.head.yRot = headYaw * ((float)Math.PI / 180F);

            model.hat.xRot = headPitch * ((float)Math.PI / 180F);
            model.hat.yRot = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof RibauldequinEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getDeltaMovement().x * abstractSiegeEntity.getDeltaMovement().x + abstractSiegeEntity.getDeltaMovement().z * abstractSiegeEntity.getDeltaMovement().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = Mth.cos(age * stepSpeed);

            model.rightArm.xRot = 0.0F;
            model.rightArm.yRot = (float) Math.toRadians(45.0);
            model.rightArm.xRot = (float) Math.toRadians(-(abstractSiegeEntity.getTrackedPitch() + 60));
            model.rightArm.z = 4.0F;
            model.rightArm.x = -3.5F;

            model.leftArm.yRot = (float) Math.toRadians(45.0);
            model.leftArm.xRot = (float) Math.toRadians(-90.0);
            model.leftArm.z = -4.0F;
            model.leftArm.x = 3.5F;

            model.rightLeg.xRot = 0.0F;
            model.rightLeg.xRot = swing * stepWidth * movement;
            model.rightLeg.yRot = (float) Math.toRadians(45.0);

            model.leftLeg.xRot = 0.0F;
            model.leftLeg.xRot = -swing * stepWidth * movement;
            model.leftLeg.yRot = (float) Math.toRadians(45.0);

            model.body.xRot = 0.0F;
            model.body.yRot = (float) Math.toRadians(45.0);

            model.head.xRot = headPitch * ((float)Math.PI / 180F);
            model.head.yRot = headYaw * ((float)Math.PI / 180F);

            model.hat.xRot = headPitch * ((float)Math.PI / 180F);
            model.hat.yRot = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof BatteringRamEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getDeltaMovement().x * abstractSiegeEntity.getDeltaMovement().x + abstractSiegeEntity.getDeltaMovement().z * abstractSiegeEntity.getDeltaMovement().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = Mth.cos(age * stepSpeed);

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

            model.rightLeg.xRot = 0.0F;
            model.rightLeg.xRot = swing * stepWidth * movement;
            model.rightLeg.z = 10;

            model.leftLeg.xRot = 0.0F;
            model.leftLeg.xRot = -swing * stepWidth * movement;
            model.leftLeg.z = 10;

            model.body.xRot = (float) Math.toRadians(50.0);
            model.body.y = 6;
            model.body.z = 0;
            model.body.yRot = 0.0F;

            model.head.y = 7;
            model.head.xRot = headPitch * ((float)Math.PI / 180F);
            model.head.yRot = headYaw * ((float)Math.PI / 180F);

            model.hat.y = 7;
            model.hat.xRot = headPitch * ((float)Math.PI / 180F);
            model.hat.yRot = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof MangonelEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getDeltaMovement().x * abstractSiegeEntity.getDeltaMovement().x + abstractSiegeEntity.getDeltaMovement().z * abstractSiegeEntity.getDeltaMovement().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = Mth.cos(age * stepSpeed);

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

            model.rightLeg.xRot = 0.0F;
            model.rightLeg.xRot = swing * stepWidth * movement;
            model.rightLeg.z = 10;

            model.leftLeg.xRot = 0.0F;
            model.leftLeg.xRot = -swing * stepWidth * movement;
            model.leftLeg.z = 10;

            model.body.xRot = (float) Math.toRadians(50.0);
            model.body.y = 6;
            model.body.z = 0;
            model.body.yRot = 0.0F;

            model.head.y = 7;
            model.head.xRot = headPitch * ((float)Math.PI / 180F);
            model.head.yRot = headYaw * ((float)Math.PI / 180F);

            model.hat.y = 7;
            model.hat.xRot = headPitch * ((float)Math.PI / 180F);
            model.hat.yRot = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof MantletEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getDeltaMovement().x * abstractSiegeEntity.getDeltaMovement().x + abstractSiegeEntity.getDeltaMovement().z * abstractSiegeEntity.getDeltaMovement().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = Mth.cos(age * stepSpeed);

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

            model.rightLeg.xRot = 0.0F;
            model.rightLeg.xRot = swing * stepWidth * movement;
            model.rightLeg.z = 10;

            model.leftLeg.xRot = 0.0F;
            model.leftLeg.xRot = -swing * stepWidth * movement;
            model.leftLeg.z = 10;

            model.body.xRot = (float) Math.toRadians(50.0);
            model.body.y = 6;
            model.body.z = 0;
            model.body.yRot = 0.0F;

            model.head.y = 7;
            model.head.xRot = headPitch * ((float)Math.PI / 180F);
            model.head.yRot = headYaw * ((float)Math.PI / 180F);

            model.hat.y = 7;
            model.hat.xRot = headPitch * ((float)Math.PI / 180F);
            model.hat.yRot = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }
    }
}
