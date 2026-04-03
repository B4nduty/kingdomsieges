package banduty.kingdomsieges.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class KSConfigs {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.EnumValue<IKSConfig.Choices> bellRingTime;

    public static final ForgeConfigSpec.IntValue cannonRange;
    public static final ForgeConfigSpec.IntValue ribauldequinRange;
    public static final ForgeConfigSpec.IntValue batteringRamRange;
    public static final ForgeConfigSpec.IntValue mangonelRange;
    public static final ForgeConfigSpec.IntValue trebuchetRange;
    public static final ForgeConfigSpec.IntValue mantletRange;
    public static final ForgeConfigSpec.IntValue cannonBallRange;
    public static final ForgeConfigSpec.IntValue trebuchetProjectileRange;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("common");

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

        builder.pop();

        SPEC = builder.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .preserveInsertionOrder()
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}