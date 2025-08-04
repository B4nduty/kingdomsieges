package banduty.kingdomsieges.sounds;

import banduty.kingdomsieges.Kingdomsieges;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Kingdomsieges.MOD_ID, RegistryKeys.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> CANNON_CLOSE = SOUND_EVENTS.register("cannon_close",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "cannon_close")));

    public static final RegistrySupplier<SoundEvent> CANNON_DISTANT = SOUND_EVENTS.register("cannon_distant",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "cannon_distant")));

    public static final RegistrySupplier<SoundEvent> RAM_IMPACT = SOUND_EVENTS.register("ram_impact",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "ram_impact")));

    public static final RegistrySupplier<SoundEvent> ROPE_CHARGE_GN = SOUND_EVENTS.register("rope_charge_gn",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "rope_charge_gn")));

    public static final RegistrySupplier<SoundEvent> ROPE_CHARGE_BR = SOUND_EVENTS.register("rope_charge_br",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "rope_charge_br")));

    public static final RegistrySupplier<SoundEvent> SIEGE_ENGINE_MOVE = SOUND_EVENTS.register("siege_engine_move",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "siege_engine_move")));

    public static final RegistrySupplier<SoundEvent> MANGONEL_SHOOT = SOUND_EVENTS.register("mangonel_shoot",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "mangonel_shoot")));

    public static final RegistrySupplier<SoundEvent> TREBUCHET_SHOOT = SOUND_EVENTS.register("trebuchet_shoot",
            () -> SoundEvent.of(new Identifier(Kingdomsieges.MOD_ID, "trebuchet_shoot")));

    public static void registerSounds() {
        SOUND_EVENTS.register();
        Kingdomsieges.LOGGER.info("Registering Sounds for " + Kingdomsieges.MOD_ID);
    }
}