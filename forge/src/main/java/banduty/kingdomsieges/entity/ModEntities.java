package banduty.kingdomsieges.entity;

import banduty.kingdomsieges.KingdomSiegesForge;
import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.entity.custom.sieges.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public interface ModEntities {
    DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Kingdomsieges.MOD_ID, Registries.ENTITY_TYPE);

    RegistrySupplier<EntityType<CannonEntity>> CANNON_ENTITY =
            ENTITY_TYPES.register("cannon_entity", () ->
                    FabricEntityTypeBuilder.create(MobCategory.MISC, CannonEntity::new)
                            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.cannonRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    RegistrySupplier<EntityType<RibauldequinEntity>> RIBAULDEQUIN_ENTITY =
            ENTITY_TYPES.register("ribauldequin_entity", () ->
                    FabricEntityTypeBuilder.create(MobCategory.MISC, RibauldequinEntity::new)
                            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.ribauldequinRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    RegistrySupplier<EntityType<BatteringRamEntity>> BATTERING_RAM_ENTITY =
            ENTITY_TYPES.register("battering_ram_entity", () ->
                    FabricEntityTypeBuilder.create(MobCategory.MISC, BatteringRamEntity::new)
                            .dimensions(EntityDimensions.fixed(2.5f, 2.5f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.batteringRamRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    RegistrySupplier<EntityType<MangonelEntity>> MANGONEL_ENTITY =
            ENTITY_TYPES.register("mangonel_entity", () ->
                    FabricEntityTypeBuilder.create(MobCategory.MISC, MangonelEntity::new)
                            .dimensions(EntityDimensions.fixed(2.8f, 2.5f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.mangonelRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    RegistrySupplier<EntityType<TrebuchetEntity>> TREBUCHET_ENTITY =
            ENTITY_TYPES.register("trebuchet_entity", () ->
                    FabricEntityTypeBuilder.create(MobCategory.MISC, TrebuchetEntity::new)
                            .dimensions(EntityDimensions.fixed(4.0f, 3.0f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.trebuchetRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    RegistrySupplier<EntityType<MantletEntity>> MANTLET_ENTITY =
            ENTITY_TYPES.register("mantlet_entity", () ->
                    FabricEntityTypeBuilder.create(MobCategory.MISC, MantletEntity::new)
                            .dimensions(EntityDimensions.fixed(2.5f, 2f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.mantletRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    RegistrySupplier<EntityType<CannonProjectile>> CANNON_BALL =
            ENTITY_TYPES.register("cannon_ball", () ->
                    FabricEntityTypeBuilder.<CannonProjectile>create(MobCategory.MISC, CannonProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.cannonBallRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    RegistrySupplier<EntityType<TrebuchetProjectile>> TREBUCHET_PROJECTILE =
            ENTITY_TYPES.register("trebuchet_projectile", () ->
                    FabricEntityTypeBuilder.<TrebuchetProjectile>create(MobCategory.MISC, TrebuchetProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(KingdomSiegesForge.CONFIG.common.trebuchetProjectileRange)
                            .trackedUpdateRate(1)
                            .build()
            );

    static void registerEntities() {
        ENTITY_TYPES.register();
        Kingdomsieges.LOG.info("Registering Entities for " + Kingdomsieges.MOD_ID);
    }
}
