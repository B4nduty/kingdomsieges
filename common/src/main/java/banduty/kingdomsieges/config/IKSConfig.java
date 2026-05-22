package banduty.kingdomsieges.config;

public interface IKSConfig {
    enum Choices {
        NEVER, EVERY_INGAME_HOUR, TWELVE_INGAME_HOURS, EVERY_HOUR, TWELVE_HOURS;
    }

    Choices getBellRingTime();

    int getCannonRange();
    int getRibauldequinRange();
    int getBatteringRamRange();
    int getMangonelRange();
    int getTrebuchetRange();
    int getMantletRange();
    int getCannonBallRange();
    int getTrebuchetProjectileRange();
}