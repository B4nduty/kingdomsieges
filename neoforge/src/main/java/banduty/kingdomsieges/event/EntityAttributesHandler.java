package banduty.kingdomsieges.event;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.entity.KSEntities;
import banduty.kingdomsieges.entity.custom.sieges.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = Kingdomsieges.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EntityAttributesHandler {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        Kingdomsieges.LOG.info("Registering Entity Attributes for {}", Kingdomsieges.MOD_ID);

        event.put(KSEntities.CANNON_ENTITY.get(), CannonEntity.createAttributes().build());
        event.put(KSEntities.BATTERING_RAM_ENTITY.get(), BatteringRamEntity.createAttributes().build());
        event.put(KSEntities.RIBAULDEQUIN_ENTITY.get(), RibauldequinEntity.createAttributes().build());
        event.put(KSEntities.MANGONEL_ENTITY.get(), MangonelEntity.createAttributes().build());
        event.put(KSEntities.TREBUCHET_ENTITY.get(), TrebuchetEntity.createAttributes().build());
        event.put(KSEntities.MANTLET_ENTITY.get(), MantletEntity.createAttributes().build());
    }
}