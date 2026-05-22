package banduty.kingdomsieges.entity;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.custom.projectiles.CannonProjectile;
import banduty.kingdomsieges.entity.custom.projectiles.TrebuchetProjectile;
import banduty.kingdomsieges.entity.custom.sieges.*;
import banduty.kingdomsieges.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public interface KSEntities {
    Supplier<EntityType<CannonEntity>> CANNON_ENTITY =
            registerEntity("cannon",
                    () -> EntityType.Builder.of(CannonEntity::new, MobCategory.MISC)
                            .sized(2.0f, 1.5f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getCannonRange())
                            .updateInterval(1)
                            .build("cannon")
            );

    Supplier<EntityType<RibauldequinEntity>> RIBAULDEQUIN_ENTITY =
            registerEntity("ribauldequin",
                    () -> EntityType.Builder.of(RibauldequinEntity::new, MobCategory.MISC)
                            .sized(2.0f, 1.5f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getRibauldequinRange())
                            .updateInterval(1)
                            .build("ribauldequin")
            );

    Supplier<EntityType<BatteringRamEntity>> BATTERING_RAM_ENTITY =
            registerEntity("battering_ram",
                    () -> EntityType.Builder.of(BatteringRamEntity::new, MobCategory.MISC)
                            .sized(2.5f, 2.5f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getBatteringRamRange())
                            .updateInterval(1)
                            .build("battering_ram")
            );

    Supplier<EntityType<MangonelEntity>> MANGONEL_ENTITY =
            registerEntity("mangonel",
                    () -> EntityType.Builder.of(MangonelEntity::new, MobCategory.MISC)
                            .sized(2.8f, 2.5f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getMangonelRange())
                            .updateInterval(1)
                            .build("mangonel")
            );

    Supplier<EntityType<TrebuchetEntity>> TREBUCHET_ENTITY =
            registerEntity("trebuchet",
                    () -> EntityType.Builder.of(TrebuchetEntity::new, MobCategory.MISC)
                            .sized(4.0f, 3.0f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getTrebuchetRange())
                            .updateInterval(1)
                            .build("trebuchet")
            );

    Supplier<EntityType<MantletEntity>> MANTLET_ENTITY =
            registerEntity("mantlet",
                    () -> EntityType.Builder.of(MantletEntity::new, MobCategory.MISC)
                            .sized(2.5f, 2f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getMantletRange())
                            .updateInterval(1)
                            .build("mantlet")
            );

    Supplier<EntityType<CannonProjectile>> CANNON_BALL =
            registerEntity("cannon_ball",
                    () -> EntityType.Builder.<CannonProjectile>of(CannonProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getCannonBallRange())
                            .updateInterval(1)
                            .build("cannon_ball")
            );

    Supplier<EntityType<TrebuchetProjectile>> TREBUCHET_PROJECTILE =
            registerEntity("trebuchet_projectile",
                    () -> EntityType.Builder.<TrebuchetProjectile>of(TrebuchetProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(Kingdomsieges.getConfig().getTrebuchetProjectileRange())
                            .updateInterval(1)
                            .build("trebuchet_projectile")
            );

    @SuppressWarnings("unchecked")
    private static <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, Supplier<EntityType<T>> entitySupplier) {
        return Services.PLATFORM.register((Registry<EntityType<T>>) (Registry<?>) BuiltInRegistries.ENTITY_TYPE, name, entitySupplier);
    }

    static void init() {
        Kingdomsieges.LOG.info("Registering Entities for " + Kingdomsieges.MOD_ID);
    }
}
