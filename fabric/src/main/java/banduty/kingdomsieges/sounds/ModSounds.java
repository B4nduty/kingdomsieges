package banduty.kingdomsieges.sounds;

import banduty.kingdomsieges.Kingdomsieges;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public interface ModSounds {
    SoundEvent CANNON_CLOSE = registerSoundEvent("cannon_close");

    SoundEvent CANNON_DISTANT = registerSoundEvent("cannon_distant");

    SoundEvent RAM_IMPACT = registerSoundEvent("ram_impact");

    SoundEvent ROPE_CHARGE_GN = registerSoundEvent("rope_charge_gn");

    SoundEvent ROPE_CHARGE_BR = registerSoundEvent("rope_charge_br");

    SoundEvent SIEGE_ENGINE_MOVE = registerSoundEvent("siege_engine_move");

    SoundEvent MANGONEL_SHOOT = registerSoundEvent("mangonel_shoot");

    SoundEvent TREBUCHET_SHOOT = registerSoundEvent("trebuchet_shoot");

    private static SoundEvent registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(Kingdomsieges.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    static void registerSounds() {
        Kingdomsieges.LOG.info("Registering Sounds for " + Kingdomsieges.MOD_ID);
    }
}