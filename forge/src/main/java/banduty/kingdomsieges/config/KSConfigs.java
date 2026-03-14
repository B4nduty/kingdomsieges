package banduty.kingdomsieges.config;

import banduty.kingdomsieges.Kingdomsieges;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = Kingdomsieges.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public class KSConfigs extends PartitioningSerializer.GlobalData {

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject()
    public Common common = new Common();

    @Config(name = Kingdomsieges.MOD_ID + "-common")
    public static final class Common implements ConfigData {
        @Comment("""
            When Land's Bell should Ring
            NEVER | EVERY_INGAME_HOUR | TWELVE_INGAME_HOURS | EVERY_HOUR | TWELVE_HOURS
            """)
        public IKSConfig.Choices bellRingTime = IKSConfig.Choices.EVERY_HOUR;

        @Comment("""
            Cannon Entity Range Render
            """)
        public int cannonRange = 100;

        @Comment("""
            Ribauldequin Entity Range Render
            """)
        public int ribauldequinRange = 100;

        @Comment("""
            Battering Ram Entity Range Render
            """)
        public int batteringRamRange = 100;

        @Comment("""
            Mangonel Entity Range Render
            """)
        public int mangonelRange = 100;

        @Comment("""
            Trebuchet Entity Range Render
            """)
        public int trebuchetRange = 100;

        @Comment("""
            Mantlet Entity Range Render
            """)
        public int mantletRange = 100;

        @Comment("""
            Cannon Ball Entity Range Render
            """)
        public int cannonBallRange = 100;

        @Comment("""
            Trebuchet Projectile Entity Range Render
            """)
        public int trebuchetProjectileRange = 100;
    }
}