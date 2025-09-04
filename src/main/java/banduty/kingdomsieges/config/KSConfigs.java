package banduty.kingdomsieges.config;

import banduty.kingdomsieges.Kingdomsieges;
import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = Kingdomsieges.MOD_ID)
@Config(name = Kingdomsieges.MOD_ID, wrapperName = "KingdomSiegesConfig")
public class KSConfigs {
    @Sync(Option.SyncMode.INFORM_SERVER)
    @Comment("""
            When Land's Bell should Ring
            NEVER | EVERY_INGAME_HOUR | TWELVE_INGAME_HOURS | EVERY_HOUR | TWELVE_HOURS
            """)
    public Choices bellRingTime = Choices.EVERY_HOUR;

    public enum Choices {
        NEVER, EVERY_INGAME_HOUR, TWELVE_INGAME_HOURS, EVERY_HOUR, TWELVE_HOURS;
    }

    @Nest
    @SectionHeader("siegeEnginesOptions")
    public SiegeEnginesOptions siegeEnginesOptions = new SiegeEnginesOptions();
    public static class SiegeEnginesOptions {
        @Sync(Option.SyncMode.INFORM_SERVER)
        @Comment("""
            Battering Ram Base Damage
            """)
        public float batteringRamBaseDamage = 16;

        @Sync(Option.SyncMode.INFORM_SERVER)
        @Comment("""
            Cannon Base Damage
            """)
        public float cannonBaseDamage = 34;

        @Sync(Option.SyncMode.INFORM_SERVER)
        @Comment("""
            Mangonel Base Damage
            """)
        public float mangonelBaseDamage = 20;

        @Sync(Option.SyncMode.INFORM_SERVER)
        @Comment("""
            Ribauldequin Base Damage
            """)
        public float ribauldequinBaseDamage = 32;

        @Sync(Option.SyncMode.INFORM_SERVER)
        @Comment("""
            Trebuchet Base Damage
            """)
        public float trebuchetBaseDamage = 26;
    }

    @Nest
    @SectionHeader("clientOptions")
    public ClientOptions clientOptions = new ClientOptions();
    public static class ClientOptions {
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
}