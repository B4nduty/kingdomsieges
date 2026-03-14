package banduty.kingdomsieges.entity;

import banduty.kingdomsieges.KingdomSiegesForge;
import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.entity.custom.sieges.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface ModEntities {
    DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Kingdomsieges.MOD_ID);

    RegistryObject<EntityType<CannonEntity>> CANNON_ENTITY =
            ENTITY_TYPES.register("cannon_entity", () ->
                    EntityType.Builder.of(CannonEntity::new, MobCategory.MISC)
                            .sized(2.0f, 1.5f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.cannonRange)
                            .updateInterval(1)
                            .build("cannon_entity")
            );

    RegistryObject<EntityType<RibauldequinEntity>> RIBAULDEQUIN_ENTITY =
            ENTITY_TYPES.register("ribauldequin_entity", () ->
                    EntityType.Builder.of(RibauldequinEntity::new, MobCategory.MISC)
                            .sized(2.0f, 1.5f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.ribauldequinRange)
                            .updateInterval(1)
                            .build("ribauldequin_entity")
            );

    RegistryObject<EntityType<BatteringRamEntity>> BATTERING_RAM_ENTITY =
            ENTITY_TYPES.register("battering_ram_entity", () ->
                    EntityType.Builder.of(BatteringRamEntity::new, MobCategory.MISC)
                            .sized(2.5f, 2.5f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.batteringRamRange)
                            .updateInterval(1)
                            .build("battering_ram_entity")
            );

    RegistryObject<EntityType<MangonelEntity>> MANGONEL_ENTITY =
            ENTITY_TYPES.register("mangonel_entity", () ->
                    EntityType.Builder.of(MangonelEntity::new, MobCategory.MISC)
                            .sized(2.8f, 2.5f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.mangonelRange)
                            .updateInterval(1)
                            .build("mangonel_entity")
            );

    RegistryObject<EntityType<TrebuchetEntity>> TREBUCHET_ENTITY =
            ENTITY_TYPES.register("trebuchet_entity", () ->
                    EntityType.Builder.of(TrebuchetEntity::new, MobCategory.MISC)
                            .sized(4.0f, 3.0f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.trebuchetRange)
                            .updateInterval(1)
                            .build("trebuchet_entity")
            );

    RegistryObject<EntityType<MantletEntity>> MANTLET_ENTITY =
            ENTITY_TYPES.register("mantlet_entity", () ->
                    EntityType.Builder.of(MantletEntity::new, MobCategory.MISC)
                            .sized(2.5f, 2f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.mantletRange)
                            .updateInterval(1)
                            .build("mantlet_entity")
            );

    RegistryObject<EntityType<CannonProjectile>> CANNON_BALL =
            ENTITY_TYPES.register("cannon_ball", () ->
                    EntityType.Builder.<CannonProjectile>of(CannonProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.cannonBallRange)
                            .updateInterval(1)
                            .build("cannon_ball")
            );

    RegistryObject<EntityType<TrebuchetProjectile>> TREBUCHET_PROJECTILE =
            ENTITY_TYPES.register("trebuchet_projectile", () ->
                    EntityType.Builder.<TrebuchetProjectile>of(TrebuchetProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(KingdomSiegesForge.CONFIG.common.trebuchetProjectileRange)
                            .updateInterval(1)
                            .build("trebuchet_projectile")
            );

    static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
        Kingdomsieges.LOG.info("Registering Entities for " + Kingdomsieges.MOD_ID);
    }
}
