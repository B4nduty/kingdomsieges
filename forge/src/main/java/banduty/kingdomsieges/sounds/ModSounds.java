package banduty.kingdomsieges.sounds;

import banduty.kingdomsieges.Kingdomsieges;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public interface ModSounds {
    DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Kingdomsieges.MOD_ID, Registries.SOUND_EVENT);

    RegistrySupplier<SoundEvent> CANNON_CLOSE = SOUND_EVENTS.register("cannon_close",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "cannon_close")));

    RegistrySupplier<SoundEvent> CANNON_DISTANT = SOUND_EVENTS.register("cannon_distant",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "cannon_distant")));

    RegistrySupplier<SoundEvent> RAM_IMPACT = SOUND_EVENTS.register("ram_impact",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "ram_impact")));

    RegistrySupplier<SoundEvent> ROPE_CHARGE_GN = SOUND_EVENTS.register("rope_charge_gn",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "rope_charge_gn")));

    RegistrySupplier<SoundEvent> ROPE_CHARGE_BR = SOUND_EVENTS.register("rope_charge_br",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "rope_charge_br")));

    RegistrySupplier<SoundEvent> SIEGE_ENGINE_MOVE = SOUND_EVENTS.register("siege_engine_move",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "siege_engine_move")));

    RegistrySupplier<SoundEvent> MANGONEL_SHOOT = SOUND_EVENTS.register("mangonel_shoot",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "mangonel_shoot")));

    RegistrySupplier<SoundEvent> TREBUCHET_SHOOT = SOUND_EVENTS.register("trebuchet_shoot",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Kingdomsieges.MOD_ID, "trebuchet_shoot")));

    static void registerSounds() {
        SOUND_EVENTS.register();
        Kingdomsieges.LOG.info("Registering Sounds for " + Kingdomsieges.MOD_ID);
    }
}