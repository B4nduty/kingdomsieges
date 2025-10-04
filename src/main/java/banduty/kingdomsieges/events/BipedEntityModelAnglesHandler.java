package banduty.kingdomsieges.events;

import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.stoneycore.entity.custom.AbstractSiegeEntity;
import banduty.stoneycore.event.custom.BipedEntityModelAnglesEvents;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BipedEntityModelAnglesHandler implements BipedEntityModelAnglesEvents.After {
    @Override
    public void afterSetAngles(BipedEntityModel<?> model, LivingEntity entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(entity.getVehicle() instanceof AbstractSiegeEntity abstractSiegeEntity)) return;
        if (abstractSiegeEntity instanceof CannonEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getVelocity().x * abstractSiegeEntity.getVelocity().x + abstractSiegeEntity.getVelocity().z * abstractSiegeEntity.getVelocity().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = MathHelper.cos(age * stepSpeed);

            model.rightArm.pitch = 0.0F;
            model.rightArm.yaw = (float) Math.toRadians(45.0);
            model.rightArm.pitch = (float) Math.toRadians(-(abstractSiegeEntity.getTrackedPitch() + 60));
            model.rightArm.pivotZ = 4.0F;
            model.rightArm.pivotX = -3.5F;

            model.leftArm.yaw = (float) Math.toRadians(45.0);
            model.leftArm.pitch = (float) Math.toRadians(-90.0);
            model.leftArm.pivotZ = -4.0F;
            model.leftArm.pivotX = 3.5F;

            model.rightLeg.pitch = 0.0F;
            model.rightLeg.pitch = swing * stepWidth * movement;
            model.rightLeg.yaw = (float) Math.toRadians(45.0);

            model.leftLeg.pitch = 0.0F;
            model.leftLeg.pitch = -swing * stepWidth * movement;
            model.leftLeg.yaw = (float) Math.toRadians(45.0);

            model.body.pitch = 0.0F;
            model.body.yaw = (float) Math.toRadians(45.0);

            model.head.pitch = headPitch * ((float)Math.PI / 180F);
            model.head.yaw = headYaw * ((float)Math.PI / 180F);

            model.hat.pitch = headPitch * ((float)Math.PI / 180F);
            model.hat.yaw = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof RibauldequinEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getVelocity().x * abstractSiegeEntity.getVelocity().x + abstractSiegeEntity.getVelocity().z * abstractSiegeEntity.getVelocity().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = MathHelper.cos(age * stepSpeed);

            model.rightArm.pitch = 0.0F;
            model.rightArm.yaw = (float) Math.toRadians(45.0);
            model.rightArm.pitch = (float) Math.toRadians(-(abstractSiegeEntity.getTrackedPitch() + 60));
            model.rightArm.pivotZ = 4.0F;
            model.rightArm.pivotX = -3.5F;

            model.leftArm.yaw = (float) Math.toRadians(45.0);
            model.leftArm.pitch = (float) Math.toRadians(-90.0);
            model.leftArm.pivotZ = -4.0F;
            model.leftArm.pivotX = 3.5F;

            model.rightLeg.pitch = 0.0F;
            model.rightLeg.pitch = swing * stepWidth * movement;
            model.rightLeg.yaw = (float) Math.toRadians(45.0);

            model.leftLeg.pitch = 0.0F;
            model.leftLeg.pitch = -swing * stepWidth * movement;
            model.leftLeg.yaw = (float) Math.toRadians(45.0);

            model.body.pitch = 0.0F;
            model.body.yaw = (float) Math.toRadians(45.0);

            model.head.pitch = headPitch * ((float)Math.PI / 180F);
            model.head.yaw = headYaw * ((float)Math.PI / 180F);

            model.hat.pitch = headPitch * ((float)Math.PI / 180F);
            model.hat.yaw = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof BatteringRamEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getVelocity().x * abstractSiegeEntity.getVelocity().x + abstractSiegeEntity.getVelocity().z * abstractSiegeEntity.getVelocity().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = MathHelper.cos(age * stepSpeed);

            model.rightArm.yaw = (float) Math.toRadians(-10.0);
            model.rightArm.roll = (float) Math.toRadians(10.0);
            model.rightArm.pitch = (float) Math.toRadians(-45.0);
            model.rightArm.pivotX = -4.0F;
            model.rightArm.pivotY = 10;
            model.rightArm.pivotZ = -2.0F;

            model.leftArm.yaw = (float) Math.toRadians(10.0);
            model.leftArm.pitch = (float) Math.toRadians(-45.0);
            model.leftArm.roll = (float) Math.toRadians(-10.0);
            model.leftArm.pivotX = 4.0F;
            model.leftArm.pivotY = 12;
            model.leftArm.pivotZ = -2.0F;

            model.rightLeg.pitch = 0.0F;
            model.rightLeg.pitch = swing * stepWidth * movement;
            model.rightLeg.pivotZ = 10;

            model.leftLeg.pitch = 0.0F;
            model.leftLeg.pitch = -swing * stepWidth * movement;
            model.leftLeg.pivotZ = 10;

            model.body.pitch = (float) Math.toRadians(50.0);
            model.body.pivotY = 6;
            model.body.pivotZ = 0;
            model.body.yaw = 0.0F;

            model.head.pivotY = 7;
            model.head.pitch = headPitch * ((float)Math.PI / 180F);
            model.head.yaw = headYaw * ((float)Math.PI / 180F);

            model.hat.pivotY = 7;
            model.hat.pitch = headPitch * ((float)Math.PI / 180F);
            model.hat.yaw = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof MangonelEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getVelocity().x * abstractSiegeEntity.getVelocity().x + abstractSiegeEntity.getVelocity().z * abstractSiegeEntity.getVelocity().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = MathHelper.cos(age * stepSpeed);

            model.rightArm.yaw = (float) Math.toRadians(-10.0);
            model.rightArm.roll = (float) Math.toRadians(10.0);
            model.rightArm.pitch = (float) Math.toRadians(-45.0);
            model.rightArm.pivotX = -4.0F;
            model.rightArm.pivotY = 10;
            model.rightArm.pivotZ = -2.0F;

            model.leftArm.yaw = (float) Math.toRadians(10.0);
            model.leftArm.pitch = (float) Math.toRadians(-45.0);
            model.leftArm.roll = (float) Math.toRadians(-10.0);
            model.leftArm.pivotX = 4.0F;
            model.leftArm.pivotY = 12;
            model.leftArm.pivotZ = -2.0F;

            model.rightLeg.pitch = 0.0F;
            model.rightLeg.pitch = swing * stepWidth * movement;
            model.rightLeg.pivotZ = 10;

            model.leftLeg.pitch = 0.0F;
            model.leftLeg.pitch = -swing * stepWidth * movement;
            model.leftLeg.pivotZ = 10;

            model.body.pitch = (float) Math.toRadians(50.0);
            model.body.pivotY = 6;
            model.body.pivotZ = 0;
            model.body.yaw = 0.0F;

            model.head.pivotY = 7;
            model.head.pitch = headPitch * ((float)Math.PI / 180F);
            model.head.yaw = headYaw * ((float)Math.PI / 180F);

            model.hat.pivotY = 7;
            model.hat.pitch = headPitch * ((float)Math.PI / 180F);
            model.hat.yaw = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }

        if (abstractSiegeEntity instanceof MantletEntity) {
            float movement = (float) Math.sqrt(abstractSiegeEntity.getVelocity().x * abstractSiegeEntity.getVelocity().x + abstractSiegeEntity.getVelocity().z * abstractSiegeEntity.getVelocity().z);
            float stepSpeed = 0.1F; // Lower = slower steps
            float stepWidth = 10.0F; // Higher = bigger leg swing
            float swing = MathHelper.cos(age * stepSpeed);

            model.rightArm.yaw = (float) Math.toRadians(-10.0);
            model.rightArm.roll = (float) Math.toRadians(10.0);
            model.rightArm.pitch = (float) Math.toRadians(-45.0);
            model.rightArm.pivotX = -4.0F;
            model.rightArm.pivotY = 10;
            model.rightArm.pivotZ = -2.0F;

            model.leftArm.yaw = (float) Math.toRadians(10.0);
            model.leftArm.pitch = (float) Math.toRadians(-45.0);
            model.leftArm.roll = (float) Math.toRadians(-10.0);
            model.leftArm.pivotX = 4.0F;
            model.leftArm.pivotY = 12;
            model.leftArm.pivotZ = -2.0F;

            model.rightLeg.pitch = 0.0F;
            model.rightLeg.pitch = swing * stepWidth * movement;
            model.rightLeg.pivotZ = 10;

            model.leftLeg.pitch = 0.0F;
            model.leftLeg.pitch = -swing * stepWidth * movement;
            model.leftLeg.pivotZ = 10;

            model.body.pitch = (float) Math.toRadians(50.0);
            model.body.pivotY = 6;
            model.body.pivotZ = 0;
            model.body.yaw = 0.0F;

            model.head.pivotY = 7;
            model.head.pitch = headPitch * ((float)Math.PI / 180F);
            model.head.yaw = headYaw * ((float)Math.PI / 180F);

            model.hat.pivotY = 7;
            model.hat.pitch = headPitch * ((float)Math.PI / 180F);
            model.hat.yaw = headYaw * ((float)Math.PI / 180F);

            ci.cancel();
        }
    }
}
