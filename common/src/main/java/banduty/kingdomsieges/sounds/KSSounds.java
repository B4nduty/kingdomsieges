package banduty.kingdomsieges.sounds;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public interface KSSounds {
    Supplier<SoundEvent> CANNON_CLOSE = registerSound("cannon_close");

    Supplier<SoundEvent> CANNON_DISTANT = registerSound("cannon_distant");

    Supplier<SoundEvent> RAM_IMPACT = registerSound("ram_impact");

    Supplier<SoundEvent> ROPE_CHARGE_GN = registerSound("rope_charge_gn");

    Supplier<SoundEvent> ROPE_CHARGE_BR = registerSound("rope_charge_br");

    Supplier<SoundEvent> SIEGE_ENGINE_MOVE = registerSound("siege_engine_move");

    Supplier<SoundEvent> MANGONEL_SHOOT = registerSound("mangonel_shoot");

    Supplier<SoundEvent> TREBUCHET_SHOOT = registerSound("trebuchet_shoot");


    private static Supplier<SoundEvent> registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, name);

        return Services.PLATFORM.register(BuiltInRegistries.SOUND_EVENT, name,
                () -> SoundEvent.createVariableRangeEvent(id));
    }

    static void init() {
        Kingdomsieges.LOG.info("Registering Sounds for " + Kingdomsieges.MOD_ID);
    }
}