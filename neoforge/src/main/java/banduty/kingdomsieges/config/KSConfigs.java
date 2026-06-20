package banduty.kingdomsieges.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class KSConfigs {

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.EnumValue<IKSConfig.Choices> bellRingTime;

    public static final ModConfigSpec.IntValue cannonRange;
    public static final ModConfigSpec.IntValue ribauldequinRange;
    public static final ModConfigSpec.IntValue batteringRamRange;
    public static final ModConfigSpec.IntValue mangonelRange;
    public static final ModConfigSpec.IntValue trebuchetRange;
    public static final ModConfigSpec.IntValue mantletRange;
    public static final ModConfigSpec.IntValue cannonBallRange;
    public static final ModConfigSpec.IntValue trebuchetProjectileRange;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        bellRingTime = builder
                .comment(
                        "When Land's Bell should Ring"
                )
                .defineEnum("bellRingTime", IKSConfig.Choices.EVERY_HOUR);

        cannonRange = builder
                .comment("Cannon Entity Range Render")
                .defineInRange("cannonRange", 100, 0, Integer.MAX_VALUE);

        ribauldequinRange = builder
                .comment("Ribauldequin Entity Range Render")
                .defineInRange("ribauldequinRange", 100, 0, Integer.MAX_VALUE);

        batteringRamRange = builder
                .comment("Battering Ram Entity Range Render")
                .defineInRange("batteringRamRange", 100, 0, Integer.MAX_VALUE);

        mangonelRange = builder
                .comment("Mangonel Entity Range Render")
                .defineInRange("mangonelRange", 100, 0, Integer.MAX_VALUE);

        trebuchetRange = builder
                .comment("Trebuchet Entity Range Render")
                .defineInRange("trebuchetRange", 100, 0, Integer.MAX_VALUE);

        mantletRange = builder
                .comment("Mantlet Entity Range Render")
                .defineInRange("mantletRange", 100, 0, Integer.MAX_VALUE);

        cannonBallRange = builder
                .comment("Cannon Ball Entity Range Render")
                .defineInRange("cannonBallRange", 100, 0, Integer.MAX_VALUE);

        trebuchetProjectileRange = builder
                .comment("Trebuchet Projectile Entity Range Render")
                .defineInRange("trebuchetProjectileRange", 100, 0, Integer.MAX_VALUE);

        SPEC = builder.build();
    }
}