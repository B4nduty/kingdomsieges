package banduty.kingdomsieges.config;

import banduty.kingdomsieges.Kingdomsieges;
import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = Kingdomsieges.MOD_ID)
@Config(name = Kingdomsieges.MOD_ID, wrapperName = "KingdomSiegesConfig")
public class KSConfigs {
    @Sync(Option.SyncMode.NONE)
    @Comment("""
            When Land's Bell should Ring
            NEVER | EVERY_INGAME_HOUR | TWELVE_INGAME_HOURS | EVERY_HOUR | TWELVE_HOURS
            """)
    public Choices bellRingTime = Choices.EVERY_HOUR;

    public enum Choices {
        NEVER, EVERY_INGAME_HOUR, TWELVE_INGAME_HOURS, EVERY_HOUR, TWELVE_HOURS;
    }

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RestartRequired
    @Comment("""
            Price formula for extending the Kingdom
            """)
    public String expandFormula = "radius - 24";

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Cannon Entity Range Render
            """)
    public int cannonRange = 100;

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Ribauldequin Entity Range Render
            """)
    public int ribauldequinRange = 100;

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Battering Ram Entity Range Render
            """)
    public int batteringRamRange = 100;

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Mangonel Entity Range Render
            """)
    public int mangonelRange = 100;

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Trebuchet Entity Range Render
            """)
    public int trebuchetRange = 100;

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Mantlet Entity Range Render
            """)
    public int mantletRange = 100;

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Cannon Ball Entity Range Render
            """)
    public int cannonBallRange = 100;

    @Sync(Option.SyncMode.NONE)
    @RestartRequired
    @Comment("""
            Trebuchet Projectile Entity Range Render
            """)
    public int trebuchetProjectileRange = 100;
}