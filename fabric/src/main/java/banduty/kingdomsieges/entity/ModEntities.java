package banduty.kingdomsieges.entity;

import banduty.kingdomsieges.KingdomSiegesFabric;
import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.entity.custom.sieges.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public interface ModEntities {
    EntityType<CannonEntity> CANNON_ENTITY =
            registerEntity("cannon",
                    FabricEntityTypeBuilder.create(MobCategory.MISC, CannonEntity::new)
                            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.cannonRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    EntityType<RibauldequinEntity> RIBAULDEQUIN_ENTITY =
            registerEntity("ribauldequin",
                    FabricEntityTypeBuilder.create(MobCategory.MISC, RibauldequinEntity::new)
                            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.ribauldequinRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    EntityType<BatteringRamEntity> BATTERING_RAM_ENTITY =
            registerEntity("battering_ram",
                    FabricEntityTypeBuilder.create(MobCategory.MISC, BatteringRamEntity::new)
                            .dimensions(EntityDimensions.fixed(2.5f, 2.5f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.batteringRamRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    EntityType<MangonelEntity> MANGONEL_ENTITY =
            registerEntity("mangonel",
                    FabricEntityTypeBuilder.create(MobCategory.MISC, MangonelEntity::new)
                            .dimensions(EntityDimensions.fixed(2.8f, 2.5f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.mangonelRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    EntityType<TrebuchetEntity> TREBUCHET_ENTITY =
            registerEntity("trebuchet",
                    FabricEntityTypeBuilder.create(MobCategory.MISC, TrebuchetEntity::new)
                            .dimensions(EntityDimensions.fixed(4.0f, 3.0f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.trebuchetRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    EntityType<MantletEntity> MANTLET_ENTITY =
            registerEntity("mantlet",
                    FabricEntityTypeBuilder.create(MobCategory.MISC, MantletEntity::new)
                            .dimensions(EntityDimensions.fixed(2.5f, 2f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.mantletRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    EntityType<CannonProjectile> CANNON_BALL =
            registerEntity("cannon_ball",
                    FabricEntityTypeBuilder.<CannonProjectile>create(MobCategory.MISC, CannonProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.cannonBallRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    EntityType<TrebuchetProjectile> TREBUCHET_PROJECTILE =
            registerEntity("trebuchet_projectile",
                    FabricEntityTypeBuilder.<TrebuchetProjectile>create(MobCategory.MISC, TrebuchetProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(KingdomSiegesFabric.getConfig().clientOptions.trebuchetProjectileRange())
                            .trackedUpdateRate(1)
                            .build()
            );

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType<T> entityType) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Kingdomsieges.MOD_ID, name), entityType);
    }

    static void registerEntities() {
        Kingdomsieges.LOG.info("Registering Entities for " + Kingdomsieges.MOD_ID);
    }
}
