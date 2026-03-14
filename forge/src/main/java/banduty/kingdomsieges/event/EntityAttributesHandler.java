package banduty.kingdomsieges.event;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.ModEntities;
import banduty.kingdomsieges.entity.custom.sieges.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kingdomsieges.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributesHandler {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        Kingdomsieges.LOG.info("Registering Entity Attributes for {}", Kingdomsieges.MOD_ID);

        event.put(ModEntities.CANNON_ENTITY.get(), CannonEntity.createAttributes().build());
        event.put(ModEntities.BATTERING_RAM_ENTITY.get(), BatteringRamEntity.createAttributes().build());
        event.put(ModEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinEntity.createAttributes().build());
        event.put(ModEntities.MANGONEL_ENTITY.get(), MangonelEntity.createAttributes().build());
        event.put(ModEntities.TREBUCHET_ENTITY.get(), TrebuchetEntity.createAttributes().build());
        event.put(ModEntities.MANTLET_ENTITY.get(), MantletEntity.createAttributes().build());
    }
}
