package banduty.kingdomsieges.sounds;

import banduty.kingdomsieges.Kingdomsieges;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Kingdomsieges.MOD_ID);

    public static final RegistryObject<SoundEvent> CANNON_CLOSE = register("cannon_close");
    public static final RegistryObject<SoundEvent> CANNON_DISTANT = register("cannon_distant");
    public static final RegistryObject<SoundEvent> RAM_IMPACT = register("ram_impact");
    public static final RegistryObject<SoundEvent> ROPE_CHARGE_GN = register("rope_charge_gn");
    public static final RegistryObject<SoundEvent> ROPE_CHARGE_BR = register("rope_charge_br");
    public static final RegistryObject<SoundEvent> SIEGE_ENGINE_MOVE = register("siege_engine_move");
    public static final RegistryObject<SoundEvent> MANGONEL_SHOOT = register("mangonel_shoot");
    public static final RegistryObject<SoundEvent> TREBUCHET_SHOOT = register("trebuchet_shoot");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        new ResourceLocation(Kingdomsieges.MOD_ID, name)));
    }

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
        Kingdomsieges.LOG.info("Registering Sounds for " + Kingdomsieges.MOD_ID);
    }
}