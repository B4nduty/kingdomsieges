package banduty.kingdomsieges.config;

import banduty.kingdomsieges.Kingdomsieges;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Kingdomsieges.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public class KSConfigs implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public IKSConfig.Choices bellRingTime = IKSConfig.Choices.EVERY_HOUR;

    @ConfigEntry.Gui.Tooltip
    public int cannonRange = 100;

    @ConfigEntry.Gui.Tooltip
    public int ribauldequinRange = 100;

    @ConfigEntry.Gui.Tooltip
    public int batteringRamRange = 100;

    @ConfigEntry.Gui.Tooltip
    public int mangonelRange = 100;

    @ConfigEntry.Gui.Tooltip
    public int trebuchetRange = 100;

    @ConfigEntry.Gui.Tooltip
    public int mantletRange = 100;

    @ConfigEntry.Gui.Tooltip
    public int cannonBallRange = 100;

    @ConfigEntry.Gui.Tooltip
    public int trebuchetProjectileRange = 100;
}