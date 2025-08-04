package banduty.kingdomsieges.entity;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.entity.custom.sieges.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKeys;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Kingdomsieges.MOD_ID, RegistryKeys.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<CannonEntity>> CANNON_ENTITY =
            ENTITY_TYPES.register("cannon_entity", () ->
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, CannonEntity::new)
                            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().cannonRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static final RegistrySupplier<EntityType<RibauldequinEntity>> RIBAULDEQUIN_ENTITY =
            ENTITY_TYPES.register("ribauldequin_entity", () ->
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, RibauldequinEntity::new)
                            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().ribauldequinRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static final RegistrySupplier<EntityType<BatteringRamEntity>> BATTERING_RAM_ENTITY =
            ENTITY_TYPES.register("battering_ram_entity", () ->
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, BatteringRamEntity::new)
                            .dimensions(EntityDimensions.fixed(2.5f, 2.5f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().batteringRamRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static final RegistrySupplier<EntityType<MangonelEntity>> MANGONEL_ENTITY =
            ENTITY_TYPES.register("mangonel_entity", () ->
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, MangonelEntity::new)
                            .dimensions(EntityDimensions.fixed(2.8f, 2.5f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().mangonelRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static final RegistrySupplier<EntityType<TrebuchetEntity>> TREBUCHET_ENTITY =
            ENTITY_TYPES.register("trebuchet_entity", () ->
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, TrebuchetEntity::new)
                            .dimensions(EntityDimensions.fixed(4.0f, 3.0f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().trebuchetRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static final RegistrySupplier<EntityType<MantletEntity>> MANTLET_ENTITY =
            ENTITY_TYPES.register("mantlet_entity", () ->
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, MantletEntity::new)
                            .dimensions(EntityDimensions.fixed(2.5f, 2f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().mantletRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static final RegistrySupplier<EntityType<CannonProjectile>> CANNON_BALL =
            ENTITY_TYPES.register("cannon_ball", () ->
                    FabricEntityTypeBuilder.<CannonProjectile>create(SpawnGroup.MISC, CannonProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().cannonBallRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static final RegistrySupplier<EntityType<TrebuchetProjectile>> TREBUCHET_PROJECTILE =
            ENTITY_TYPES.register("trebuchet_projectile", () ->
                    FabricEntityTypeBuilder.<TrebuchetProjectile>create(SpawnGroup.MISC, TrebuchetProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(Kingdomsieges.getConfig().trebuchetProjectileRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    public static void registerEntities() {
        ENTITY_TYPES.register();
        Kingdomsieges.LOGGER.info("Registering Entities for " + Kingdomsieges.MOD_ID);
    }
}
